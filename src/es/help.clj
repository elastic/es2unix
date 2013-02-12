(ns es.help
  (:require [clojure.tools.cli :refer [cli]]
            [es.command :as comm]
            [es.command.version :as version]))

(defn help-commands []
  (println "Available commands:")
  (println)
  (print "  ")
  (println (->> comm/available
                (interpose "\n  " )
                (apply str))))

(defn help [specs]
  (println)
  (println "es2unix" (version/version*))
  (println "Copyright 2013 Elasticsearch")
  (println)
  (println "Opts:")
  (println)
  (doseq [spec (sort-by second specs)]
    (let [[short switch desc & {:keys [default]}] spec]
      (println (format "%s %-15s %s (default: %s)"
                       short switch desc default))))
  (println)
  (help-commands))
