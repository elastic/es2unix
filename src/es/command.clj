(ns es.command
  (:require [es.command.count]
            [es.command.health]
            [es.command.heap]
            [es.command.indices]
            [es.command.master]
            [es.command.nodes]
            [es.command.shards]
            [es.command.search]
            [es.command.version]))

(def available
  ['count
   'health
   'heap
   'indices
   'master
   'nodes
   'search
   'shards
   'version])
