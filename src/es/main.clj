(ns es.main
  (:gen-class)
  (:require [clojure.tools.cli :refer [cli]]
            [bultitude.core :refer [namespaces-on-classpath]]
            [es.format.table :refer [tabler]]
            ;; Requiring these here so they get compiled early:
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

(defn available-commands []
  (let [pref "es.command."]
    ;; :prefix doesn't currently work with -Xbootclasspath ...at least
    ;; I think that's what's wrong.  Will filter manually for now.
    (->> (namespaces-on-classpath)
         (map str)
         (filter #(.startsWith % pref))
         (map #(.replace % pref ""))
         sort)))

(defn help-commands []
  (println "Available commands:")
  (println)
  (print "  ")
  (println (->> (available-commands)
                (interpose "\n  " )
                (apply str))))

(defn error [fmt & args]
  (binding [*out* *err*]
    (apply printf (str (.trim fmt) "\n") args)
    (println)
    (help-commands)
    (flush))
  (System/exit 99))

(defn help [bann]
  (println "Usage: es COMMAND [OPTS]")
  (println)
  (println (.replace bann "Usage:\n\n" ""))
  (println)
  (help-commands))

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
