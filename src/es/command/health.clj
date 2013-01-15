(ns es.command.health
  (:require [es.data.cluster :as cluster]))

(def cols
  [:cluster_name
   :status
   :number_of_nodes
   :number_of_data_nodes
   :active_primary_shards
   :active_shards
   :relocating_shards
   :initializing_shards
   :unassigned_shards])

(defn go [args {:keys [url verbose]}]
  (concat
   (if verbose
     [(map name cols)])
   (let [res (cluster/health url)
         vals (apply juxt cols)]
     [(vals res)])))
