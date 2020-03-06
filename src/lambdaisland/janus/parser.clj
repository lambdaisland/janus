(ns lambdaisland.janus.parser
  (:import com.vladsch.flexmark.parser.Parser))

(defprotocol MarkdownParser
  "Contains the signature needed to parse markdown formatted strings using external
  markdown parsing libraries"
  (markdown-to-parser [s])
  (parser-to-janus [document]))

(defrecord FlexmarkParser [s]
  MarkdownParser
  (markdown-to-parser [s]
    (let [parser (.build (Parser/builder))]
      (.parse parser)))
  (parser-to-janus [document] nil))
;; TODO Implementation of parser-to-janus
