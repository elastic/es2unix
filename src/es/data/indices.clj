(ns es.data.indices
  (:require [es.http :as http]
            [es.util :as util]))

(defn status
  ([url]
     (status url []))
  ([url indices]
     (let [lst (util/comma-list indices)
           lst (if (pos? (count lst)) (str "/" lst) "")]
       (http/get (str url lst "/_status")))))

(defn indices
  ([url]
     (indices url []))
  ([url indices]
     (:indices (status url indices))))

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
     (->> (for [[idxname index] (:indices (status url))
                [shname shard] (:shards index)
                replica shard]
            (if-let [rep (util/maybe-rep replica indices)]
              [(make-replica-key (:routing rep)) rep]))
          (filter identity)
          (apply concat)
          (apply hash-map))))
