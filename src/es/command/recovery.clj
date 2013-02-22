(ns es.command.recovery
  (:require [es.data.cluster :as cluster]
            [clojure.pprint :refer [pprint]]))

(def cols
  ['shard
   'replica-bytes
   'primary-bytes
   'percent])

(defn update-shard-bytes [key primary replica details]
  (doseq [shard details]
    (if (-> shard :routing :primary)
           (swap! primary assoc key (-> shard :index :size_in_bytes))
           (when (= (:state shard) "RECOVERING")
             (swap! replica assoc key (-> shard :index :size_in_bytes))))))

(defn has-recovering-shards? [idx details]
  (some #{"RECOVERING"} (map :state details)))

(defn recovery [http args {:keys [verbose]}]
  (concat
   (when verbose
     [(map str cols)])
   (let [cdata (cluster/shard-status http)
         primary-bytes (atom {})
         replica-recover-bytes (atom {})]
     (doall (for [[index shardsinfo] cdata
                  [shardnum sharddetails] shardsinfo
                  :when (has-recovering-shards? index sharddetails)]
              (let [fullname (str (name index) "[" (name shardnum) "]")]
                (update-shard-bytes fullname primary-bytes
                                    replica-recover-bytes sharddetails))))
     (for [[shard r-bytes] @replica-recover-bytes
           :let [p-bytes (get @primary-bytes shard "UNKNOWN")]]
       [shard
        r-bytes
        p-bytes
        (format "%.1f%%" (float (/ (* 100 r-bytes) p-bytes)))]))))
