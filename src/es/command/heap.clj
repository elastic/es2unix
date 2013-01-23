(ns es.command.heap
  (:require [es.data.cluster :as cluster]
            [es.format.network :refer [ip]]
            [es.util :refer [substr]]))

(def cols
  ['id
   'heap
   'heap
   'max
   'max
   'ratio
   'ip
   'name])

(defn heap [http args opts]
  (concat
   (if (:verbose opts)
     [cols])
   (for [[id node] (cluster/mem http)]
     [(substr (name id) 8)
      {:val (:heap_used node) :just :->}
      (:heap_used_in_bytes node)
      {:val (:heap_max node) :just :->}
      (:heap_max_in_bytes node)
      {:val (:heap_used_percent node) :just :->}
      {:val (-> node :transport_address ip) :just :->}
      (-> node :name)])))
