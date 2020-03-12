(ns lambdaisland.janus.util-test
  (:require [clojure.test :refer :all]
            [lambdaisland.janus.util :refer :all]))

(deftest extract-version-components-test
  (testing "Correct version"
    (is (= {:version-id "0.0-601"
            :date "2020-03-11"
            :hash "6b88d96"}
           (extract-version-components "# 0.0-601 (2020-03-11 / 6b88d96)"))))
  (testing "Empty version"
    (is (= {}
           (extract-version-components "# "))))
  (testing "Partial version"
    (is (= {:version-id "0.0-601"}
           (extract-version-components "# 0.0-601 ( / )")))
    (is (= {:version-id "0.0-601"
            :date "2020-03-11"}
           (extract-version-components "# 0.0-601 (2020-03-11 / )")))))

(deftest clean-item-test
  (testing "No nil values"
    (is (= {:key1 "a"
            :key2 "b"
            :key3 "c"}
           (clean-item {:key1 "a"
                        :key2 "b"
                        :key3 "c"}))))
  (testing "All nil values"
    (is (= {}
           (clean-item {:key1 nil
                        :key2 nil
                        :key3 nil}))))
  (testing "Some nil values"
    (is (= {:key1 "a" :key3 "c"}
           (clean-item {:key1 "a"
                        :key2 nil
                        :key3 "c"})))
    (is (= {:key2 "b"}
           (clean-item {:key1 nil
                        :key2 "b"
                        :key3 nil})))))

(deftest transform-to-list-test
  (testing "Empty sequence"
    (is (= nil
           (transform-to-list '()))))
  (testing "Valid sequence"
    (is (= '("Ask" "Janus" "for" "changes")
           (transform-to-list (.splitAsStream #"\s+" "Ask Janus for changes"))))))
