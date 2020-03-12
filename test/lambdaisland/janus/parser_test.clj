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

(def empty-changelog "")

(def nil-changelog nil)

(def optimistic-changelog-document (.parse test-parser optimistic-changelog))

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
