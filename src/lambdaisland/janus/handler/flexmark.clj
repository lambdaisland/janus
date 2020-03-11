(ns lambdaisland.janus.handler.flexmark
  (:require [lambdaisland.janus.util :as util])
  (:import [com.vladsch.flexmark.ast Heading]
           [com.vladsch.flexmark.ast BulletList]
           [com.vladsch.flexmark.ast BulletListItem]))

(defn extract-version-data [node]
  (-> node
      (.getChars)
      (.toString)
      (util/extract-version-components)))

(defn is-version? [node]
  (and
   (= (type node) com.vladsch.flexmark.ast.Heading)
   (= (.getLevel node) 1))) ;; In CHANGELOG domain everything with "# " or level 1 heading is version info

(defn is-segment? [node]
  (and
   (= (type node) com.vladsch.flexmark.ast.Heading)
   (= (.getLevel node) 2)))

(defn transform-to-list [iterable-node]
  (iterator-seq (.iterator iterable-node)))

(defn object-to-text [node]
  (.toString (.getChars node)))

(defn retrieve-component [tag node]
  (let [tag-repr (str "## " tag)]
    (loop [aux-node node
           found? false]
      (if (or (nil? aux-node) (= found? true))
        aux-node
        (let [shared-node (when (and (not (nil? aux-node)) (is-segment? (.getNext aux-node)))
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
      ;; (map (fn [x] (object-to-text x))
      ;;      (iterator-seq (.iterator (.getChildren bullet-list))))
      ;; (loop [bullet-item (.getFirstChild bullet-list)
      ;;        result (list)]
      ;;   (if (or (nil? bullet-item)
      ;;           (not (= (type bullet-item) com.vladsch.flexmark.ast.BulletListItem)))
      ;;     result
      ;;     (recur
      ;;      (let [next-item (.getNext bullet-item)]
      ;;        (if (= (type next-item) com.vladsch.flexmark.ast.BulletListItem)
      ;;          next-item
      ;;          nil))
      ;;      (conj result (object-to-text bullet-item)))))
      (map (fn [x] (object-to-text x))
           (filter (fn [x] (= (type x) com.vladsch.flexmark.ast.BulletListItem))
              (transform-to-list (.getDescendants bullet-list))))
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

(defn clean-item [item]
  (into {} (filter #(not (nil? (val %))) item)))

(defn build-changelog [document]
  (let [version-list (filter
                      (fn [x] (is-version? x))
                      (iterator-seq (.iterator (.getChildren document))))]
    (map (fn [x] (clean-item (build-item x))) version-list)))
