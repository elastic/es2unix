(ns es.command
  (:require [es.command.health]
            [es.command.indices]
            [es.command.master]
            [es.command.nodes]
            [es.command.shards]
            [es.command.version]))

(def available
  ['health
   'indices
   'master
   'nodes
   'shards
   'version])
