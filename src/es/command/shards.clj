(ns es.command.shards
  (:require [es.data.indices :as indices]
            [es.data.nodes :as nodes]))

(defn go [args {:keys [url]}]
  (let [status (if (seq args)
                 (apply indices/status url args)
                 (indices/status url))
        nodes (nodes/nodes url)]
    (for [[idx-id index] (:indices status)
          [sh-id shards] (:shards index)
          shard shards]
      (let [pri? (if (indices/primary-shard? shard) "p" "r")
            node-name (nodes/node-name nodes (-> shard :routing :node))]
        [(name idx-id)
         (name sh-id)
         pri?
         (-> shard :state)
         (-> shard :index :size_in_bytes)
         (-> shard :index :size)
         (-> shard :docs :num_docs)
         node-name]))))
