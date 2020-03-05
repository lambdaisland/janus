(ns lambdaisland.janus.core)

(defn parse [s parser] ;; I think passing parser keep the code open for extension
  (-> parser (.parse s)))
