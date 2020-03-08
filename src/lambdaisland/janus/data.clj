(ns lambdaisland.janus.data)

(defn extract-version-components
  "Extracts version-id, date and sha data from a given string"
  [s]
  (zipmap '(:version-id :date :hash)
          (filter
           (fn [x] (not (= x "")))
           (-> s
               (clojure.string/replace #"[#()/]" "")
               (clojure.string/split #" ")))))

(defrecord Changed
    [changes])

(defrecord Fixed
    [fixtures])

(defrecord Added
    [additions])

(defrecord Item
    [version-id date sha added fixed changed])

(defrecord Changelog
    [items])

(defprotocol DataBuilder
  "Contains the signatures needed to extract data from object obtained from external
  data parsing libraries"
  (extract-version-data [node])
  (retrieve-component   [component node])
  (extract-changes      [node])
  (extract-fixtures     [node])
  (extract-additions    [node])
  (build-item           [node])
  (build-changelog      [document]))

(defrecord FlexmarkDataBuilder [document]
  DataBuilder
  (extract-version-data [node]
    (-> node
        (.getChars)
        (.toString)
        (extract-version-components)))

  (retrieve-component [component node]
    (let [component-repr (str "## " component)]
      (loop [aux-node node
             found? false]
        (if (or (nil? aux-node) (= found? true))
          aux-node
          (let [shared-node (when (not (nil? aux-node)) (.getNext aux-node))]
            (recur shared-node (if (not (nil? shared-node))
                                 (= (.toString (.getChars shared-node)) component-repr)
                                 false)))))))

  (extract-changes [node]
    (if (not (nil? (retrive-component "Changed" node)))
      nil ;; TODO
      nil))

  (extract-fixtures [node]
    (if (not (nil? (retrive-component "Fixed" node)))
      nil ;; TODO
      nil))

  (extract-additions [node]
    (if (not (nil? (retrive-component "Added" node)))
      nil ;; TODO
      nil))

  (build-item [node]
    (let [version-data (extract-version-data node)]
      (Item. (:version-id version-data)
             (:date version-data)
             (:sha version-data)
             (Added. (extract-additions node))
             (Fixed. (extract-fixtures  node))
             (Changed. (extract-changes   node)))))

  (build-changelog [document]
    nil)) ;; TODO
