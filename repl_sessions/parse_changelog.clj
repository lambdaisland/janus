(ns repl-sessions.parse-changelog
  (:require [lambdaisland.janus.core :as janus]
            [lambdaisland.janus.parser :as parser]
            [clojure.string :as str]))

(reverse
 (sort-by :date
          (for [logfile ["../ansi/CHANGELOG.md"
                         "../edn-lines/CHANGELOG.md"
                         "../glogi/CHANGELOG.md"
                         "../kaocha-boot/CHANGELOG.md"
                         "../kaocha-cljs/CHANGELOG.md"
                         "../kaocha-cucumber/CHANGELOG.md"
                         "../kaocha-midje/CHANGELOG.md"
                         "../nrepl/CHANGELOG.md"
                         "../tools.namespace/CHANGELOG.md"
                         "../uri/CHANGELOG.md"
                         "../deep_diff/CHANGELOG.md"
                         "../fetch/CHANGELOG.md"
                         "../janus-parser/CHANGELOG.md"
                         "../kaocha/CHANGELOG.md"
                         "../kaocha-cloverage/CHANGELOG.md"
                         "../kaocha-junit-xml/CHANGELOG.md"
                         "../logback-clojure-filter/CHANGELOG.md"
                         "../regal/CHANGELOG.md"
                         "../trikl/CHANGELOG.md"
                         "../zipper-viz/CHANGELOG.md"]
                section (parser/parse (slurp logfile))
                :when (:date section)
                :when (not (= "Unreleased" (:version-id section)))]
            (assoc section :project (-> logfile
                                        (str/replace "../" "")
                                        (str/replace #"/.*" ""))))))


(parser/parse
 (slurp "../kaocha/CHANGELOG.md"))
