(ns es.command.health
  (:require [es.http :as http]))

(defn health [host port]
  (http/get host port "/_cluster/health"))

(defn go [args {:keys [host port]}]
  (let [res (health host port)
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
    (apply str (interpose " " (vals res)))))
