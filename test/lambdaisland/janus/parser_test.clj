(ns lambdaisland.janus.parser-test
  (:require [clojure.test :refer :all]
            [lambdaisland.janus.parser :refer :all])
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
  '({:version-id "Unreleased",
     :added (),
     :fixed (),
     :changed ()}
    {:version-id "0.0-71",
     :date "2020-02-24",
     :sha "773860f",
     :added (),
     :fixed ("- Depend on an actual version of Glogi, instead or \"RELEASE\"\n"),
     :changed ()}
    {:version-id "0.0-68",
     :date "2019-12-25",
     :sha "71c2d86",
     :added (),
     :fixed ("- Wait for websocket client namespace to load before attempting to connect. This\n  should help in particular with reliability when running against a browser\n  environment.\n"),
     :changed ("- Pick a free port for websockets automatically instead of using a hard-coded port\n")}))

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
                       :added '(),
                       :fixed '("- Depend on an actual version of Glogi, instead or \"RELEASE\"\n"),
                       :changed '()}]
    (testing "Node with data"
      (is (= expected-item
             (build-item test-node))))))

(deftest extract-changes-test
  (let [node-list (iterator-seq (.iterator (.getChildren optimistic-changelog-document)))
        test-node-with-changes (nth node-list  9)
        test-node-without-changes (nth node-list 0)
        expected-changes '("- Pick a free port for websockets automatically instead of using a hard-coded port\n")]
    (testing "Node with additions"
      (is (= expected-changes
             (extract-changes test-node-with-changes))))
    (testing "Node without additions"
      (is (= '()
             (extract-changes test-node-without-changes))))))

(deftest extract-fixtures-test
  (let [node-list (iterator-seq (.iterator (.getChildren optimistic-changelog-document)))
        test-node-with-fixtures (nth node-list  4)
        test-node-without-fixtures (nth node-list 0)
        expected-fixtures '("- Depend on an actual version of Glogi, instead or \"RELEASE\"\n")]
    (testing "Node with fixtures"
      (is (= expected-fixtures
             (extract-fixtures test-node-with-fixtures))))
    (testing "Node without fixtures"
      (is (= '()
             (extract-fixtures test-node-without-fixtures))))))

(deftest extract-additions-test
  (let [node-list (iterator-seq (.iterator (.getChildren optimistic-changelog-with-added-document)))
        test-node-with-additions (nth node-list  4)
        test-node-without-additions (nth node-list 0)
        expected-additions '("- Added point for testing purposes\n")]
    (testing "Node with additions"
      (is (= expected-additions
             (extract-additions test-node-with-additions))))
    (testing "Node without additions"
      (is (= '()
             (extract-additions test-node-without-additions))))))

(deftest extract-list-test
  (let [node-list (iterator-seq (.iterator (.getChildren optimistic-changelog-document)))]
    (testing "Edge case parameters"
      (is (= '()
             (extract-list nil (nth node-list 4))))
      (is (= '()
             (extract-list "Fixed" nil))))
    (testing "Existing tag with no info"
      (is (= '()
             (extract-list "Added" (nth node-list 0)))))
    (testing "Existing tag with info"
      (is (= '("- Depend on an actual version of Glogi, instead or \"RELEASE\"\n")
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
              :hash "773860f"}
             (extract-version-data (nth node-list 4)))))))
