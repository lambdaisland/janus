(ns lambdaisland.janus.parser-test
  (:require [clojure.test :refer :all]
            [lambdaisland.janus.parser :refer :all]))

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
  - Wait for websocket client namespace to load before attempting to connect. This\n
  should help in particular with reliability when running against a browser\n
  environment.\n
  ## Changed\n
  - Pick a free port for websockets automatically instead of using a hard-coded port\n
  ")

(def empty-changelog "")

(def nil-changelog nil)

;; Expected Results
(def optimistic-changelog-expected-result
  '({:version-id "Unreleased",
     :added '(),
     :fixed '(),
     :changed '()}
    {:version-id "0.0-71",
     :date "2020-02-24",
     :sha "773860f",
     :added '(),
     :fixed '("- Depend on an actual version of Glogi, instead or \"RELEASE\"\n"),
     :changed '("- Pick a free port for websockets automatically instead of using a hard-coded port\n")}
    {:version-id "0.0-68",
     :date "2019-12-25",
     :sha "71c2d86",
     :added '(),
     :fixed '("- Wait for websocket client namespace to load before attempting to connect.
     This\n  should help in particular with reliability when running against a browser\n  environment.\n"),
     :changed '("- Pick a free port for websockets automatically instead of using a hard-coded port\n")}))

(def empty-changelog-expected-result '())

(def nil-changelog-expected-result '())

(deftest parse-test
  (testing "Testing happy scenario"
    (is (= 0 1))))

(deftest build-version-test
  (testing "Testing happy scenario"
    (is (= 0 1))))
