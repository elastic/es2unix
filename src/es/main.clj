(ns es.main
  (:gen-class)
  (:require [clojure.tools.cli :refer [cli]]
            [bultitude.core :refer [namespaces-on-classpath]]
            [es.format.error :refer [stack-trace]]
            [es.format.table :refer [tabler]]
            [es.command :as comm]
            [slingshot.slingshot :refer [try+]]))

(def opts
  [["-u" "--url" "ES instance locator" :default "http://localhost:9200"]
   ["-o" "--output" "Output format [only raw right now]" :default :raw]
   ["-v" "--[no-]verbose" :default false]])

(defn find-command [ns var]
  (let [var (symbol (format "%s/%s" ns var))]
    (try
      (find-var var)
      (catch Exception _))))

(defn help-commands []
  (println "Available commands:")
  (println)
  (print "  ")
  (println (->> comm/available
                (interpose "\n  " )
                (apply str))))

(defn error [fmt & args]
  (binding [*out* *err*]
    (apply printf (str (.trim fmt) "\n") args)
    (flush)))

(defn help [bann]
  (println "Usage: es COMMAND [OPTS]")
  (println)
  (println (.replace bann "Usage:\n\n" ""))
  (println)
  (help-commands))

(defn die [fmt & args]
  (apply error fmt args)
  (flush)
  (System/exit 1))

(defn main [cmd args opts]
  (let [cmd (find-command
             (symbol (format "es.command.%s" cmd))
             (symbol cmd))]
    (if cmd
      (cmd args opts)
      :fail)))

(defn -main [& args]
  (let [[opts args banner] (apply cli args opts)]
    (try+
      (let [[cmd & args] args
            _ (when (= cmd "help")
                (help banner)
                (die ""))
            res (main cmd args opts)]
        (condp = res
          :fail (if cmd
                  (die "no command %s" cmd))
          (tabler opts res)))
      (catch [:type :es.http/error] {:keys [msg]}
        (die msg))
      (catch Object _
        (die "unexpected: %s\n%s" &throw-context
               (-> &throw-context :throwable stack-trace))))))
