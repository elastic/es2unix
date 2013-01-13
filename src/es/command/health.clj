(ns es.command.health
  (:require [es.http :as http]))

(defn health [url]
  (http/get (str url "/_cluster/health")))

(defn go [args {:keys [url]}]
  (let [res (health url)
        vals (juxt
              :cluster_name
              :status
              :number_of_nodes
              :number_of_data_nodes
              :active_primary_shards
              :active_shards
              :relocating_shards
              :initializing_shards
              :unassigned_shards)]
    (or
     (:http-error res)
     [(vals res)])))
