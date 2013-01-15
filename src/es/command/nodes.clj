(ns es.command.nodes
  (:require [es.data.nodes :as nodes]
            [es.format.network :refer [parse-addr]]))

(def cols
  ['id
   'http-ip
   'port
   'tran-ip
   'tran-port
   'master?
   'data/client
   'name])

(defn go [args {:keys [url verbose]}]
  (concat
   (if verbose
     [(map str cols)])
   (let [nodes (nodes/nodes url)
         mast (nodes/master-id url)]
     (for [[id node] nodes]
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
          (:name node)])))))
