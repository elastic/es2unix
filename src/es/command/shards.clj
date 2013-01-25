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
   'ip
   'node])

(defn name-maybe-relocating [sh]
  (if (:relocating_node sh)
    (format "%s -> %s %s"
            (get-in sh [:node :name])
            (ip (get-in sh [:relocating_node :transport_address]))
            (get-in sh [:relocating_node :name]))
    (get-in sh [:node :name])))

(defn run [state indices]
  (for [sh (cluster/shards state indices)]
    [(sh :index)
     (sh :shard)
     (if (replica/primary? sh) "p" "r")
     (sh :state)
     (ip (get-in sh [:node :transport_address]))
     (name-maybe-relocating sh)]))

(defn shards [http args {:keys [verbose]}]
  (run (http "/_cluster/state?filter_metadata=1") args))
