(ns es.data.nodes
  (:refer-clojure :exclude [false? true? name])
  (:require [es.http :as http]
            [es.util.math :as math]))

(def defaults
  {:client false
   :data true
   :master true})

(defn nodes [http]
  (-> "/_nodes?all" http :nodes))

(defn stats
  ([http]
     (-> "/_nodes/stats?all" http :nodes))
  ([http id]
     (-> (format "/_nodes/%s/stats?all"
                 (clojure.core/name id)) http :nodes id)))

(defn master [http]
  (http
   (str "/_cluster/state?"
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

(defn node [http id]
  (let [nodes (nodes http)
        id (keyword id)]
    (nodes id)))

(defn mem [mem-stat mem-info]
  (let [used (:heap_used_in_bytes mem-stat)
        max (:heap_max_in_bytes mem-info)]
    (merge
     mem-stat
     mem-info
     {:heap_used_percent (math/percent used max)})))
