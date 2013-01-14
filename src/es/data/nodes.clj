(ns es.data.nodes
  (:refer-clojure :exclude [false? true?])
  (:require [es.http :as http]))

(def defaults
  {:client false
   :data true
   :master true})

(defn nodes [url]
  (http/get (str url "/_cluster/nodes")))

(defn master [url]
  (http/get (format "%s%s" url
                    (str "/_cluster/state?"
                         "filter_metadata=1&"
                         "filter_routing_table=1&"
                         "filter_indices=1"))))

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

(defn node-name [nodes id]
  (let [id (keyword id)]
    (if-let [node (-> nodes :nodes id)]
      (:name node)
      "")))
