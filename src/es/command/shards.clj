(ns es.command.shards
  (:require [es.data.cluster :as cluster]
            [es.data.indices :as indices]
            [es.data.nodes :as nodes]
            [es.data.replica :as replica]
            [es.format.network :refer [ip]]
            [es.format.table :refer [make-cell]]))

(def cols
  ['index
   'shard
   'pri/rep
   'state
   'size
   'bytes
   'docs
   'ip
   'node])

(defn shards [http args {:keys [verbose]}]
  (concat
   (if verbose
     [(map str cols)])
   (for [[k sh] (indices/shards http args)]
     (let [node (nodes/node http (-> sh :routing :node))]
       [(-> sh :routing :index)
        (-> sh :routing :shard)
        (if (replica/primary? sh) "p" "r")
        (:state sh)
        (make-cell
         {:val (-> sh :index :size)
          :just :->})
        (-> sh :index :size_in_bytes)
        (or (-> sh :docs :num_docs) "-")
        (ip (:transport_address node))
        (:name node)]))
   (for [sh (cluster/unassigned-shards http args)]
     [(:index sh)
      (:shard sh)
      (if (replica/primary? sh) "p" "r")
      (:state sh)
      (make-cell
       {:val " "
        :just :->})
      " "
      " "
      " "
      " "
      ])))
