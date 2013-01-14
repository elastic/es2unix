(ns es.command.health
  (:require [es.data.cluster :as cluster]))

(defn go [args {:keys [url]}]
  (let [res (cluster/health url)
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
