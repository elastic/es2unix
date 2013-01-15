(ns es.data.nodes
  (:refer-clojure :exclude [false? true? name])
  (:require [es.http :as http]
            [es.data.cluster :as cluster]))

(def defaults
  {:client false
   :data true
   :master true})

(defn nodes [url]
  (-> (str url "/_nodes")
      http/get
      :nodes))

(defn master [url]
  (http/get
   (str url "/_cluster/state?"
        "filter_metadata=1&"
        "filter_routing_table=1&"
        "filter_indices=1")))

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

(defn master-id [url]
  (:master_node (master url)))

(defn node [url id]
  (let [nodes (nodes url)
        id (keyword id)]
    (nodes id)))
