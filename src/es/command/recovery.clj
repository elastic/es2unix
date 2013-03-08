(ns es.command.recovery
  (:require [es.data.cluster :as cluster]))

(def cols
  ['shard
   'replica-bytes
   'primary-bytes
   'percent])

(defn recovery [http args {:keys [verbose]}]
  (concat
   (when verbose
     [(map str cols)])
   (let [cdata (cluster/shard-stats (cluster/get-shard-stats http))
         primary-bytes (atom {})
         replica-recover-bytes (atom {})]
     (doseq [[key shardsinfo] cdata]
       (let [[indexname shardnum primary? node] key
             state (-> shardsinfo :routing :state)
             store (:store shardsinfo)
             fullname (str (name indexname) "[" (name shardnum) "]")]
         (if primary?
           (swap! primary-bytes assoc fullname (:size_in_bytes store))
           (if (= "RECOVERING" state)
             (swap! replica-recover-bytes assoc
                    fullname (:size_in_bytes store))))))
     (for [[shard r-bytes] @replica-recover-bytes
           :let [p-bytes (get @primary-bytes shard "UNKNOWN")]]
       [shard
        r-bytes
        p-bytes
        (try (format "%.1f%%" (float (/ (* 100 r-bytes) p-bytes)))
             (catch Exception _ "___"))]))))
