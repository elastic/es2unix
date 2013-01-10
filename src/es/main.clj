(ns es.main
  (:gen-class)
  (:require [clojure.tools.cli :refer [cli]]))

(def opts
  [["-h" "--host" "ES instance hostname" :default "localhost"]
   ["-p" "--port" "ES instance port" :parse-fn #(Integer. %) :default "9200"]
   ["-v" "--[no-]verbose" :default true]])

(defn find-command [ns var]
  (let [var (symbol (format "%s/%s" ns var))]
    (try
      (require ns)
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
    (let [[cmd args] args
          res (main cmd args opts)]
      (condp = res
        :fail (if cmd
                (error "no command %s" cmd)
                (help banner))
        (println (.trim res))))))
