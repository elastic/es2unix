(ns es.command
  (:require [es.command.count]
            [es.command.election]
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
   'election
   'health
   'heap
   'indices
   'master
   'nodes
   'search
   'shards
   'version])
