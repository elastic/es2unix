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
   'docs
   'size
   'bytes
   'ip
   'node])

(defn name-maybe-relocating [sh]
  (if (:relocating_node sh)
    (format "%s -> %s %s"
            (get-in sh [:node :name])
            (ip (get-in sh [:relocating_node :transport_address]))
            (get-in sh [:relocating_node :name]))
    (get-in sh [:node :name])))

(defn run [state stats indices]
  (for [sh (cluster/shards state indices)]
    (let [routing (merge sh {:node (get-in sh [:node :id])})
          k (replica/make-key routing)
          shstat (stats k)]
      [(sh :index)
       (sh :shard)
       (if (replica/primary? sh) "p" "r")
       (sh :state)
       (get-in shstat [:docs :count])
       (get-in shstat [:store :size])
       (get-in shstat [:store :size_in_bytes])
       (ip (get-in sh [:node :transport_address]))
       (name-maybe-relocating sh)])))

(defn shards [http args {:keys [verbose]}]
  (concat
   (if verbose
     [cols])
   (run (cluster/get-state http)
        (-> http
            cluster/get-shard-stats
            cluster/shard-stats)
        args)))
