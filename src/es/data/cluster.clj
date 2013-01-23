(ns es.data.cluster
  (:refer-clojure :exclude [count])
  (:require [es.data.replica :as replica]
            [es.format.uri :as uri]
            [es.util :as util]))

(defn health
  ([http]
     (http "/_cluster/health"))
  ([http indices]
     (let [healths (http "/_cluster/health?level=indices")]
       (->> (for [[nam data] (:indices healths)]
              (if (util/match-any? (name nam) indices)
                [nam data]))
            (filter identity)
            (into {})))))

(defn state [http]
  (http "/_cluster/state?filter_metadata=1"))

(defn unassigned-shards
  ([http]
     (unassigned-shards http []))
  ([http indices]
     (let [st (state http)]
       (filter identity
               (for [replica (-> st :routing_nodes :unassigned)]
                 (replica/maybe replica indices))))))

(defn shards
  ([http]
     (shards http []))
  ([http indices]
     (let [st (state http)]
       (filter identity
               (for [[idxname index] (-> st :routing_table :indices)
                     [shname shard] (:shards index)
                     replica shard]
                 (replica/maybe replica indices))))))

(defn count
  ([http]
     (count http "*:*"))
  ([http query]
     (http (str "/_count?q=" (uri/encode query)))))
