(ns es.data.cluster
  (:refer-clojure :exclude [count])
  (:require [es.data.replica :as replica]
            [es.data.nodes :as nodes]
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

(defn get-state [http]
  (http "/_cluster/state?filter_metadata=1"))

(defn get-shard-stats [http]
  (http "/_stats?level=shards&all=1"))

(defn shard-stats
  ([stats]
     (shard-stats stats []))
  ([stats indices]
     (->>
      (for [[idxname index] (get-in stats [:_all :indices])
            [shardid shards] (get index :shards)
            replica shards]
        (let [routing {:index idxname
                       :shard shardid
                       :primary (get-in replica [:routing :primary])
                       :node (get-in replica [:routing :node])}]
          (if (replica/maybe routing indices)
            [(replica/make-key routing) replica])))
      (filter identity)
      (into {}))))

(defn shards
  ([state]
     (shards state {} []))
  ([state indices]
     (let [nodes (:nodes state)]
       ( ->>
         (for [[idxname index] (get-in state [:routing_table :indices])
               [_ shard] (get index :shards)
               replica shard]
           (if-let [rep (replica/maybe replica indices)]
             (let [node-id (:node rep)]
               (-> rep
                   (assoc-in [:key] (replica/make-key rep))
                   (update-in [:node]
                              #(nodes (keyword %)))
                   (assoc-in [:node :id] node-id)
                   (update-in [:relocating_node]
                              #(nodes (keyword %)))))))
         (filter identity)))))

(defn count
  ([http]
     (count http "*:*"))
  ([http query]
     (http (str "/_count?q=" (uri/encode query)))))

(defn flaggable-nodes [http path flags]
  (let [path (str path "?" (uri/query-flags flags))]
    (http path)))

(defn stats [http & flags]
  (flaggable-nodes http "/_nodes/stats" flags))

(defn info [http & flags]
  (flaggable-nodes http "/_nodes" flags))

(defn nodes [http & flags]
  (util/merge-transpose
   {:stats (:nodes (apply stats http flags))}
   {:info (:nodes (apply info http flags))}))

(defn mem [http]
  (->> (for [[id node] (nodes http :jvm)]
         (let [stat (get-in node [:stats :jvm :mem])
               info (get-in node [:info :jvm :mem])]
           [id (merge
                (nodes/mem stat info)
                (select-keys (:info node) [:name :transport_address]))]))
       (into {})))
