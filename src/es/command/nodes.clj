(ns es.command.nodes
  (:require [es.data.nodes :as nodes]
            [es.format.network :refer [parse-addr]]))

(defn go [args {:keys [url]}]
  (let [res (nodes/nodes url)
        mast (nodes/master-id url)]
    (for [[id node] (:nodes res)]
      (let [httpaddr (-> node :http_address parse-addr)
            tranaddr (-> node :transport_address parse-addr)
            I-am-master? (= (name id) mast)
            master? (if (nodes/master-eligible? node)
                      (if I-am-master?
                        "*"
                        " ")
                      "-")
            data? (if (nodes/client? node)
                    "c"
                    (if (nodes/store-data? node)
                      "d"
                      "-"))]
        [(name id)
         (:ip httpaddr)
         (:port httpaddr)
         (:ip tranaddr)
         (:port tranaddr)
         master?
         data?
         (:name node)]))))
