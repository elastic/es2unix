(ns es.command.nodes
  (:refer-clojure :exclude [false? true?])
  (:require [es.http :as http]
            [es.command.master :refer [master-id]]
            [es.format.network :refer [parse-addr]]))

(def defaults
  {:client false
   :data true
   :master true})

(defn true? [attr]
  (= attr "true"))

(defn false? [attr]
  (not (true? attr)))

(defn true-attr? [attrs attr]
  (if attrs
    (if (attrs attr)
      (true? (attrs attr))
      (defaults attr))
    (defaults attr)))

(defn store-data? [node]
  (true-attr? (:attributes node) :data))

(defn client? [node]
  (true-attr? (:attributes node) :client))

(defn master-eligible? [node]
  (if (client? node)
    false
    (true-attr? (:attributes node) :master)))

(defn nodes [url]
  (http/get (str url "/_cluster/nodes")))

(defn go [args {:keys [url]}]
  (let [res (nodes url)
        mast (master-id url)]
    (for [[id node] (:nodes res)]
      (let [httpaddr (-> node :http_address parse-addr)
            tranaddr (-> node :transport_address parse-addr)
            I-am-master? (= (name id) mast)
            master? (if (master-eligible? node)
                      (if I-am-master?
                        "*"
                        " ")
                      "-")
            data? (if (client? node)
                    "c"
                    (if (store-data? node)
                      "d"
                      "-"))]
        [(name id)
         (:ip httpaddr)
         (:port httpaddr)
         (:ip tranaddr)
         (:port tranaddr)
         master?
         data?
         (:name node)]))))
