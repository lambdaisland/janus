(ns repl-sessions.parse-changelog
  (:require [lambdaisland.janus :as janus]
            [clojure.string :as str]
            [clojure.java.io :as io]))

(janus/parse
 (slurp "../kaocha/CHANGELOG.md"))

(def logfiles
  (->> (io/file "..")
       file-seq
       (filter #(str/ends-with? (str %) "CHANGELOG.md"))))

(doseq [{:keys [project version-id date added fixed changed]}
        (->> (for [logfile logfiles
                   section (janus/parse (slurp logfile))
                   :when (#{"Unreleased"} (:version-id section))]
               (assoc section :project (-> logfile
                                           str
                                           (str/replace "../" "")
                                           (str/replace #"/.*" ""))))
             (sort-by :date)
             reverse
             )]
  (println (str "*" project "*"))
  (doseq [added added]
    (println "- added:" (str/trim (str/replace added #"- " ""))))
  (doseq [fixed fixed]
    (println "- fixed:" (str/trim (str/replace fixed #"- " ""))))
  (doseq [changed changed]
    (println "- changed:" (str/trim (str/replace changed #"- " ""))))
  )

(doseq [{:keys [project version-id date added fixed changed]}
        (->> (for [logfile logfiles
                   section (janus/parse (slurp logfile))
                   :when (:date section)
                   :when (not (#{"Unreleased" "Changelog"} (:version-id section)))
                   ]
               (assoc section :project (-> logfile
                                           str
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


(for [logfile logfiles
      section (janus/parse (slurp logfile))
      :when (#{"Unreleased"} (:version-id section))]
  (assoc section :project (-> logfile
                              str
                              (str/replace "../" "")
                              (str/replace #"/.*" ""))))
