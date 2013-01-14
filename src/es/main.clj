(ns es.main
  (:gen-class)
  (:require [clojure.tools.cli :refer [cli]]
            [es.format.table :refer [tabler]]
            [es.command.health]
            [es.command.master]
            [es.command.nodes]
            [es.command.shards]
            [es.command.version]
            ))

(def opts
  [["-u" "--url" "ES instance locator" :default "http://localhost:9200"]
   ["-o" "--output" "Output format [raw, csv, org]" :default :raw]
   ["-v" "--[no-]verbose" :default true]])

(defn find-command [ns var]
  (let [var (symbol (format "%s/%s" ns var))]
    (try
      (find-var var)
      (catch Exception _))))

(defn error [fmt & args]
  (binding [*out* *err*]
    (apply printf (str (.trim fmt) "\n") args)
    (flush))
  (System/exit 99))

(defn help [bann]
  (println "Usage: es COMMAND [OPTS]")
  (println)
  (println (.replace bann "Usage:\n\n" "")))

(defn main [cmd args opts]
  (let [cmd (find-command
             (symbol (format "es.command.%s" cmd))
             'go)]
    (if cmd
      (cmd args opts)
      :fail)))

(defn -main [& args]
  (let [[opts args banner] (apply cli args opts)]
    (let [[cmd & args] args
          res (main cmd args opts)]
      (condp = res
        :fail (if cmd
                (error "no command %s" cmd)
                (help banner))
        (tabler opts res)))))
