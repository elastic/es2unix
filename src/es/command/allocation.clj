(ns es.command.allocation
  (:require [es.data.cluster :as data]
            [es.data.nodes :as nodes]
            [es.format.network :refer [parse-addr]]
            [clojure.set :refer [rename-keys]]))

(def cols
  ['count
   'ip
   'name])

(defn allocation [http args {:keys [verbose]}]
  (concat
   (if verbose
     [(map str cols)])
   (let [shards (for [[name idx] (data/routing http)
                      [shardnum shard] (:shards idx)
                      s shard]
                  (:node s))
         names-to-ips (into {} (map (fn [[k v]]
                                      [(:name v)
                                       (-> v
                                           :transport_address
                                           parse-addr
                                           :ip)])
                                    (nodes/nodes http)))
         nodes-to-names (into {} (map (fn [[k v]] [(name k) (:name v)])
                                      (nodes/nodes http)))
         freq (frequencies shards)
         allocations (rename-keys freq (merge nodes-to-names
                                              {nil "UNASSIGNED"}))
         allocations (doall (for [[node shardnum] allocations]
                         [shardnum
                          (get names-to-ips node "x.x.x.x")
                          node]))]
     allocations)))
