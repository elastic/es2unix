(ns es.command
  (:require [es.command.health]
            [es.command.master]
            [es.command.nodes]
            [es.command.shards]
            [es.command.version]))

(def available
  ['health
   'master
   'nodes
   'shards
   'version])
