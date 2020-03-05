(ns lambdaisland.janus.data)

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
  (extract-version-id [node])
  (extract-date       [node])
  (extract-sha        [node])
  (extract-changes    [node])
  (extract-fixtures   [node])
  (extract-additions  [node])
  (build-item         [node])
  (build-Changelog    [document]))

(defn extract-simple-component
  "Extracts version-id, date or sha data, depending on the value of s"
  [s]
  nil) ;; TODO

(defn extract-sequential-component
  "Extracts changes, fixtures or additions data, depending on the value of s"
  [s]
  nil) ;; TODO

(comment (defrecord FlexmarkDataCollector [document]))
