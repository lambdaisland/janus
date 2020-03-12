(ns lambdaisland.janus.parser
  (:require [lambdaisland.janus.util :as util])
  (:import [com.vladsch.flexmark.ast.Heading]
           [com.vladsch.flexmark.ast.BulletList]
           [com.vladsch.flexmark.ast.BulletListItem]
           [com.vladsch.flexmark.parser.Parser]))

(set! *warn-on-reflection* true)

(defn extract-version-data [node]
  (-> node
      (.getChars)
      (.toString)
      (util/extract-version-components)))

(defn is-version? [node]
  (and
   (= (type node) com.vladsch.flexmark.ast.Heading)
   (= (.getLevel node) 1))) ;; In CHANGELOG domain everything with "# " or level 1 heading is version info

(defn object-to-text [node]
  (.toString (.getChars node)))

(defn retrieve-component [tag node]
  (let [tag-repr (str "## " tag)]
    (loop [aux-node node
           found? false]
      (if (or (nil? aux-node) (= found? true))
        aux-node
        (let [shared-node (when (and (not (nil? aux-node))
                                     (not (is-version? (.getNext aux-node))))
                            (.getNext aux-node))]
          (recur shared-node (if (not (nil? shared-node))
                               (= (object-to-text shared-node) tag-repr)
                               false)))))))

(defn extract-list [tag node]
  (let [component (retrieve-component tag node)
        bullet-list
        (if (nil? component)
          (list)
          (.getNext component))]
    (if (and (not (nil? bullet-list))
             (= (type bullet-list) com.vladsch.flexmark.ast.BulletList))
      (map (fn [x] (object-to-text x))
           (filter (fn [x] (= (type x) com.vladsch.flexmark.ast.BulletListItem))
              (util/transform-to-list (.getDescendants bullet-list))))
      (list))))

(defn extract-changes [node]
  (extract-list "Changed" node))

(defn extract-fixtures [node]
  (extract-list "Fixed" node))

(defn extract-additions [node]
  (extract-list "Added" node))

(defn build-item [node]
  (let [version-data (extract-version-data node)]
    {:version-id (:version-id version-data)
     :date       (:date version-data)
     :sha        (:hash version-data)
     :added      (extract-additions node)
     :fixed      (extract-fixtures  node)
     :changed    (extract-changes node)}))

(defn build-changelog [document]
  (let [version-list (filter
                      (fn [x] (is-version? x))
                      (util/transform-to-list (.getChildren document)))]
    (map (fn [x] (util/clean-item (build-item x))) version-list)))

(defn parse [s]
  (let [parser (.build (Parser/builder))
        document (.parse parser s)]
    (build-changelog document)))
