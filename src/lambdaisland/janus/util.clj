(ns lambdaisland.janus.util)

(defn extract-version-components
  "Extracts version-id, date and sha data from a given string"
  [s]
  (zipmap '(:version-id :date :hash)
          (filter
           (fn [x] (not (= x "")))
           (-> s
               (clojure.string/replace #"[#\n()/]" "")
               (clojure.string/split #" ")))))

(defn clean-item [item]
  (into {} (filter #(not (nil? (val %))) item)))

(defn transform-to-list [iterable-node]
  (iterator-seq (.iterator iterable-node)))
