(ns es.data.indices
  (:require [es.data.cluster :as cluster]
            [es.http :as http]
            [es.util :as util]
            [slingshot.slingshot :refer [throw+]]))

(defn status
  ([url]
     (status url []))
  ([url indices]
     (let [lst (util/comma-list indices)
           lst (if (pos? (count lst)) (str "/" lst) "")]
       (http/get (str url lst "/_status")))))

(defn replica-totals [url indices]
  (->> (for [[nam data] (:indices (status url indices))]
         [nam {:total
               (apply
                merge-with +
                (for [[id replicas] (:shards data)
                      replica replicas]
                  {:bytes (-> replica :index :size_in_bytes)
                   :docs (-> replica :docs :num_docs)}))}])
       (into {})))

(defn indices
  ([url]
     (indices url []))
  ([url indices]
     (merge-with
      merge
      (cluster/health url indices)
      (:indices (status url indices))
      (replica-totals url indices))))

(defn make-replica-key [routing]
  [
   (:index routing)
   (:shard routing)
   (:primary routing)
   (:node routing)
   ])

(defn shards
  ([url]
     (shards url []))
  ([url indices]
     (->> (for [[idxname index] (:indices (status url indices))
                [shard replicas] (:shards index)
                replica replicas]
            [(make-replica-key (:routing replica)) replica])
          (into {}))))
