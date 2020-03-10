(ns lambdaisland.janus.util)

(defn extract-version-components
  "Extracts version-id, date and sha data from a given string"
  [s]
  (zipmap '(:version-id :date :hash)
          (filter
           (fn [x] (not (= x "")))
           (-> s
               (clojure.string/replace #"[#()/]" "")
               (clojure.string/split #" ")))))
