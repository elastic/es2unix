(ns es.data.indices
  (:require [es.data.cluster :as cluster]
            [es.util :as util]))

(defn index-slice
  ([http endpoint indices]
     (let [lst (util/comma-list indices)
           lst (if (pos? (count lst)) (str "/" lst) "")]
       (http (str lst endpoint)))))

(defn status
  ([http]
     (status http []))
  ([http indices]
     (index-slice http "/_status" indices)))

(defn stats
  ([http]
     (status http []))
  ([http indices]
     (index-slice http "/_stats" indices)))

(defn indices
  ([http]
     (indices http []))
  ([http indices]
     (util/merge-transpose
      {:health (cluster/health http indices)}
      {:status (:indices (status http indices))}
      {:stats (get-in (stats http indices) [:_all :indices])})))

(defn make-replica-key [routing]
  [
   (:index routing)
   (:shard routing)
   (:primary routing)
   (:node routing)
   ])

(defn shards
  ([http]
     (shards http []))
  ([http indices]
     (->> (for [[idxname index] (:indices (status http indices))
                [shard replicas] (:shards index)
                replica replicas]
            [(make-replica-key (:routing replica)) replica])
          (into {}))))
