(ns es.data.cluster
  (:require [es.http :as http]
            [es.util :refer [maybe-rep]]))

(defn health
  ([url]
     (http/get (str url "/_cluster/health")))
  ([url indices]
     (let [healths (http/get (str url "/_cluster/health?level=indices"))]
       (->> (for [[nam data] (:indices healths)]
              (if (util/match-any? (name nam) indices)
                [nam {:health data}]))
            (filter identity)
            (into {})))))

(defn state [url]
  (http/get (str url "/_cluster/state?filter_metadata=1")))

(defn unassigned-shards
  ([url]
     (unassigned-shards url []))
  ([url indices]
     (let [st (state url)]
       (filter identity
               (for [replica (-> st :routing_nodes :unassigned)]
                 (maybe-rep replica indices))))))

(defn shards
  ([url]
     (shards url []))
  ([url indices]
     (let [st (state url)]
       (filter identity
               (for [[idxname index] (-> st :routing_table :indices)
                     [shname shard] (:shards index)
                     replica shard]
                 (maybe-rep replica indices))))))
