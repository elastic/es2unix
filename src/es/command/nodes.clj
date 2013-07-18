(ns es.command.nodes
  (:require [es.data.cluster :as cluster]
            [es.data.nodes :as nodes]
            [es.format.network :refer [parse-addr]]))

(def cols
  ['id
   'http-ip
   'port
   'tran-ip
   'tran-port
   'jdk
   'heap
   'uptime
   'data/client
   'master?
   'name])

(defn nodes [http args {:keys [verbose]}]
  (concat
   (if verbose
     [(map str cols)])
   (let [nodes (nodes/nodes http)
         mem (cluster/mem http)
         mast (nodes/master-id http)]
     (for [[id node] nodes]
       (let [httpaddr (-> node :http_address parse-addr)
             tranaddr (-> node :transport_address parse-addr)
             uptime (int
                     (/
                      (- (System/currentTimeMillis)
                         (or (-> node :jvm :start_time)
                             (System/currentTimeMillis)))
                      1000))
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
          (-> node :jvm :version)
          {:val (-> mem id :heap_used_percent) :just :->}
          uptime
          data?
          master?
          (:name node)])))))
