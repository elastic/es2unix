(ns es.command.shards
  (:require [es.data.cluster :as cluster]
            [es.data.indices :as indices]
            [es.data.nodes :as nodes]
            [es.data.replica :as replica]
            [es.format.network :refer [ip]]))

(def cols
  ['index
   'shard
   'pri/rep
   'state
   'size
   'size-bytes
   'docs
   'ip
   'node])

(defn go [args {:keys [url verbose]}]
  (concat
   (if verbose
     [(map str cols)])
   (for [[k sh] (indices/shards url args)]
     (let [node (nodes/node url (-> sh :routing :node))]
       [(-> sh :routing :index)
        (-> sh :routing :shard)
        (if (replica/primary? sh) "p" "r")
        (:state sh)
        (-> sh :index :size)
        (-> sh :index :size_in_bytes)
        (or (-> sh :docs :num_docs) "-")
        (ip (:transport_address node))
        (:name node)
        ]))
   (for [sh (cluster/unassigned-shards url args)]
     [(:index sh)
      (:shard sh)
      (if (replica/primary? sh) "p" "r")
      (:state sh)
      " "
      " "
      " "
      " "
      " "
      ])))
