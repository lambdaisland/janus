(ns lambdaisland.janus-test
  (:require [clojure.test :refer :all]
            [lambdaisland.janus :refer :all])
  (:import [com.vladsch.flexmark.parser Parser]))

(def test-parser (.build (Parser/builder)))

;; Input definitions
(def optimistic-changelog
  "# Unreleased\n
  ## Added\n\n
  ## Fixed\n\n
  ## Changed\n\n
  # 0.0-71 (2020-02-24 / 773860f)\n
  ## Fixed\n
  - Depend on an actual version of Glogi, instead or \"RELEASE\"\n
  # 0.0-68 (2019-12-25 / 71c2d86)\n
  ## Fixed\n
  - Wait for websocket client namespace to load before attempting to connect. This
  should help in particular with reliability when running against a browser
  environment.\n
  ## Changed\n
  - Pick a free port for websockets automatically instead of using a hard-coded port\n
  ")

(def optimistic-changelog-with-added
  "# Unreleased\n
  ## Added\n\n
  ## Fixed\n\n
  ## Changed\n\n
  # 0.0-71 (2020-02-24 / 773860f)\n
  ## Added\n
  - Added point for testing purposes\n
  ## Fixed\n
  - Depend on an actual version of Glogi, instead or \"RELEASE\"\n
  # 0.0-68 (2019-12-25 / 71c2d86)\n
  ## Fixed\n
  - Wait for websocket client namespace to load before attempting to connect. This
  should help in particular with reliability when running against a browser
  environment.\n
  ## Changed\n
  - Pick a free port for websockets automatically instead of using a hard-coded port\n
  ")

(def empty-changelog "")

(def nil-changelog nil)

(def optimistic-changelog-document (.parse test-parser optimistic-changelog))

(def optimistic-changelog-with-added-document (.parse test-parser optimistic-changelog-with-added))

(def empty-changelog-document (.parse test-parser empty-changelog))

(def nil-changelog-document (.parse test-parser nil-changelog))

;; Expected Results
(def optimistic-changelog-expected-result
  '({:version-id "Unreleased"}
    {:version-id "0.0-71",
     :date "2020-02-24",
     :sha "773860f",
     :fixed ["Depend on an actual version of Glogi, instead or \"RELEASE\"\n"]}
    {:version-id "0.0-68",
     :date "2019-12-25",
     :sha "71c2d86",
     :fixed ["Wait for websocket client namespace to load before attempting to connect. This\n  should help in particular with reliability when running against a browser\n  environment.\n"],
     :changed ["Pick a free port for websockets automatically instead of using a hard-coded port\n"]}))

;; Test definitions
(deftest parse-test
  (testing "Valid CHANGELOG data"
    (is (= optimistic-changelog-expected-result
           (parse optimistic-changelog))))
  (testing "Nil and empty CHANGELOG data"
    (is (= '()
           (parse empty-changelog)))
    (is (= '()
           (parse nil-changelog)))))

(deftest build-changelog-test
  (testing "Valid CHANGELOG document data"
    (is (= optimistic-changelog-expected-result
           (build-changelog optimistic-changelog-document))))
  (testing "Nil and empty CHANGELOG document data"
    (is (= '()
           (build-changelog empty-changelog-document)))
    (is (= '()
           (build-changelog nil-changelog-document)))))

(deftest build-item-test
  (let [test-node (nth (iterator-seq (.iterator (.getChildren optimistic-changelog-document))) 4)
        expected-item {:version-id "0.0-71",
                       :date "2020-02-24",
                       :sha "773860f",
                       :added nil,
                       :fixed ["Depend on an actual version of Glogi, instead or \"RELEASE\"\n"],
                       :changed nil}]
    (testing "Node with data"
      (is (= expected-item
             (build-item test-node))))))

(deftest extract-list-test
  (let [node-list (iterator-seq (.iterator (.getChildren optimistic-changelog-document)))]
    (testing "Edge case parameters"
      (is (= nil
             (extract-list nil (nth node-list 4))))
      (is (= nil
             (extract-list "Fixed" nil))))
    (testing "Existing tag with no info"
      (is (= nil
             (extract-list "Added" (nth node-list 0)))))
    (testing "Existing tag with info"
      (is (= ["Depend on an actual version of Glogi, instead or \"RELEASE\"\n"]
             (extract-list "Fixed" (nth node-list 4)))))))

(deftest retrieve-component-test
  (let [node-list (iterator-seq (.iterator (.getChildren optimistic-changelog-document)))]
    (testing "Edge case parameters"
      (is (= nil
             (retrieve-component nil (nth node-list 4))))
      (is (= nil
             (retrieve-component "Added" nil))))
    (testing "Existing tag"
      (is (= (nth node-list 1)
             (retrieve-component "Added" (nth node-list 0)))))
    (testing "Non existing tag"
      (is (= nil
             (retrieve-component "Addad" (nth node-list 0)))))))

(deftest is-version-test
  (let [node-list (iterator-seq (.iterator (.getChildren optimistic-changelog-document)))]
    (testing "Non version node"
      (is (= false
             (is-version? (nth node-list 1)))))
    (testing "Version node"
      (is (= true
             (is-version? (nth node-list 0)))))))

(deftest extract-version-data-test
  (let [node-list (iterator-seq (.iterator (.getChildren optimistic-changelog-document)))]
    (testing "Node with incomplete data"
      (is (= {:version-id "Unreleased"}
             (extract-version-data (nth node-list 0)))))
    (testing "Node with complete data"
      (is (= {:version-id "0.0-71",
              :date "2020-02-24",
              :sha "773860f"}
             (extract-version-data (nth node-list 4)))))))

(deftest extract-version-components-test
  (testing "Correct version"
    (is (= {:version-id "0.0-601"
            :date "2020-03-11"
            :sha "6b88d96"}
           (extract-version-components "# 0.0-601 (2020-03-11 / 6b88d96)"))))
  (testing "Empty version"
    (is (= nil
           (extract-version-components "# "))))
  (testing "Partial version"
    (is (= {:version-id "0.0-601"}
           (extract-version-components "# 0.0-601 ( / )")))
    (is (= {:version-id "0.0-601"
            :date "2020-03-11"}
           (extract-version-components "# 0.0-601 (2020-03-11 / )")))))

(deftest remove-nil-vals-test
  (testing "No nil values"
    (is (= {:key1 "a"
            :key2 "b"
            :key3 "c"}
           (remove-nil-vals {:key1 "a"
                             :key2 "b"
                             :key3 "c"}))))
  (testing "All nil values"
    (is (= {}
           (remove-nil-vals {:key1 nil
                             :key2 nil
                             :key3 nil}))))
  (testing "Some nil values"
    (is (= {:key1 "a" :key3 "c"}
           (remove-nil-vals {:key1 "a"
                             :key2 nil
                             :key3 "c"})))
    (is (= {:key2 "b"}
           (remove-nil-vals {:key1 nil
                             :key2 "b"
                             :key3 nil})))))
