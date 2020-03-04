(ns lambdaisland.janus.core)

(defrecord Changed
    [changes])

(defrecord Fixed
    [fixtures])

(defrecord Added
    [additions])

(defrecord Version
    [added fixed changed])

(defrecord Changelog
    [version_list])

(defprotocol DataCollector
  "Contains the signatures needed to extract data from object obtained from external
  data parsing libraries"
  (extract-changes   [version-id document])
  (extract-fixtures  [version-id document])
  (extract-additions [version-id document]))

(defprotocol MarkdownParser
  "Contains the signature needed to parse markdown formatted strings using external
  markdown parsing libraries"
  (parse [input]))

(defn build-version
  "Build a version object given version-id(string), additions(list of string), fixtures(list of string), changes(list of string)"
  [version-id additions fixtures changes]
  (Version. version-id
            (Added. additions)
            (Fixed. fixtures)
            (Changed. changes)))

(defn parse
  "Parse a given formatted string to a Changelog object"
  [s]
  nil) ;; implementation pending
