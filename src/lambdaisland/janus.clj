(ns lambdaisland.janus
  (:require [clojure.string :as str])
  (:import [com.vladsch.flexmark.ast Heading BulletList BulletListItem]
           [com.vladsch.flexmark.parser Parser]
           [com.vladsch.flexmark.util.ast Block Node]))

(set! *warn-on-reflection* true)

(defn remove-nil-vals [item]
  (into {} (filter (comp some? val)) item))

(defn node-text
  [^Block node]
  (when node
    (.toString (.getChars node))))

(defn extract-version-components
  "Extracts version-id, date and sha data from a given string
  e.g. \"0.0-601 (2020-03-11 / 6b88d96)\""
  [s]
  (when-let [[_ version date sha] (re-find #"#\s+([^ ]+)(?:\s+\(([0-9-]+)?(?:[ /]+([a-f0-9]+)?)?\))?" s)]
    #_(prn sha)
    (cond-> {:version-id version}
      date (assoc :date date)
      sha  (assoc :sha sha))))

(defn extract-version-data
  [^Heading node]
  (extract-version-components (node-text node)))

(defn is-version?
  [^Node node]
  (and
   (= (type node) Heading)
   (= (.getLevel ^Heading node) 1))) ;; In CHANGELOG domain everything with "# " or level 1 heading is version info


(defn block-seq [^Block node]
  (->> node
       (iterate #(.getNext ^Block %))
       (take-while identity)))

(defn retrieve-component
  [^String tag ^Block node]
  (when node
    (let [tag-repr (str "## " tag)]
      (->> (block-seq node)
           next
           (take-while #(not (is-version? %)))
           (some #(when (= tag-repr (node-text %)) %))))))

(defn extract-list
  [^String tag ^Block node]
  (let [^Block component (retrieve-component tag node)
        bullet-list (when (some? component) (.getNext component))]
    (when (= (type bullet-list) BulletList)
      (->> (.getDescendants ^BulletList bullet-list)
           (filter #(= (type %) BulletListItem))
           (map node-text)
           (map #(str/replace % #"\A- " ""))
           seq))))

(defn build-item
  [node]
  (let [version-data (extract-version-data node)
        added        (extract-list "Added" node)
        fixed        (extract-list "Fixed" node)
        changed      (extract-list "Changed" node)]
    {:version-id (:version-id version-data)
     :date       (:date version-data)
     :sha        (:sha version-data)
     :added      added
     :fixed      fixed
     :changed    changed}))

(defn build-changelog
  [^Node document]
  (->> document
       .getChildren
       (filter is-version?)
       (map (comp remove-nil-vals build-item))))

(defn parse
  [^String s]
  (let [^Parser parser (.build (Parser/builder))
        ^Node document (.parse parser s)]
    (build-changelog document)))
