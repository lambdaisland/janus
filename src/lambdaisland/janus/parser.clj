(ns lambdaisland.janus.parser
  (:require [lambdaisland.janus.handler.flexmark :as flexmark])
  (:import com.vladsch.flexmark.parser.Parser))

(defprotocol MarkdownParser
  "Contains the signature needed to parse markdown formatted strings using external
  markdown parsing libraries"
  (parse [this]))

(defrecord FlexmarkParser [text]
  MarkdownParser
  (parse [this]
    (let [parser (.build (Parser/builder))
          document (.parse parser (:text this))]
      (flexmark/build-changelog document))))
