(ns es.main
  (:gen-class)
  (:require [clojure.tools.cli :refer [cli]]))

(def opts
  [["-h" "--host" "ES instance hostname" :default "localhost"]
   ["-p" "--port" "ES instance port" :parse-fn #(Integer. %) :default "9200"]
   ["-v" "--[no-]verbose" :default true]
   ["-l" "--log-directory" :default "/some/path"]]  )

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

(defn main [cmd args opts]
  (let [cmd (find-command
             (symbol (format "es.command.%s" cmd))
             'go)]
    (if cmd
      (cmd args opts)
      :fail)))

(defn -main [& args]
  (let [[opts args help] (apply cli args opts)]
    (let [[cmd args] args
          res (main cmd args opts)]
      (condp = res
        :fail (error "no command %s" cmd)
        (println (.trim res))))))
