(ns repl-sessions.parse-changelog
  (:require [lambdaisland.janus :as janus]
            [clojure.string :as str]))

(doseq [{:keys [project version-id date added fixed changed]}
        (->> (for [logfile ["../ansi/CHANGELOG.md"
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
                            "../janus/CHANGELOG.md"
                            "../kaocha/CHANGELOG.md"
                            "../kaocha-cloverage/CHANGELOG.md"
                            "../kaocha-junit-xml/CHANGELOG.md"
                            "../logback-clojure-filter/CHANGELOG.md"
                            "../regal/CHANGELOG.md"
                            "../trikl/CHANGELOG.md"
                            "../zipper-viz/CHANGELOG.md"]
                   section (janus/parse (slurp logfile))
                   :when (:date section)
                   :when (not (#{"Unreleased" "Changelog"} (:version-id section)))
                   ]
               (assoc section :project (-> logfile
                                           (str/replace "../" "")
                                           (str/replace #"/.*" ""))))
             (sort-by :date)
             reverse
             (take 10))]
  (println (str "*" project " " version-id "*"))
  (doseq [added added]
    (println "- added:" (str/trim (str/replace added #"- " ""))))
  (doseq [fixed fixed]
    (println "- fixed:" (str/trim (str/replace fixed #"- " ""))))
  (doseq [changed changed]
    (println "- changed:" (str/trim (str/replace changed #"- " ""))))
  )


(janus/parse
 (slurp "../kaocha/CHANGELOG.md"))
