(ns es.command.nodes
  (:require [es.http :as http]
            [es.command.master :refer [master-id]]
            [es.format.network :refer [parse-addr]]))

(defn nodes [url]
  (http/get (str url "/_cluster/nodes")))

(defn go [args {:keys [url]}]
  (let [res (nodes url)
        mast (master-id url)]
    (for [[id node] (:nodes res)]
      (let [httpaddr (-> node :http_address parse-addr)
            tranaddr (-> node :transport_address parse-addr)]
        [(name id)
         (:ip httpaddr)
         (:port httpaddr)
         (:ip tranaddr)
         (:port tranaddr)
         (if (= (name id) mast)
           "*"
           " ")
         (:name node)]))))
