(ns es.command.shards
  (:require [es.data.cluster :as cluster]
            [es.data.indices :as indices]
            [es.data.nodes :as nodes]
            [es.util :as util]
            [es.format.network :refer [ip]]))

(defn go [args {:keys [url]}]
  (concat
   (for [[k sh] (indices/shards url)]
     (let [node (nodes/node url (-> sh :routing :node))]
       [(-> sh :routing :index)
        (-> sh :routing :shard)
        (if (-> sh :routing util/primary?) "p" "r")
        (:state sh)
        (-> sh :index :size)
        (-> sh :index :size_in_bytes)
        (-> sh :docs :num_docs)
        (ip (:transport_address node))
        (:name node)
        ]))
   (for [sh (cluster/unassigned-shards url)]
     [(:index sh)
      (:shard sh)
      (if (util/primary? sh) "p" "r")
      (:state sh)
      " "
      0
      0
      " "
      " "
      ])))
