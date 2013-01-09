(ns es.command.version
  (:require [clojure.java.io :as io]
            [es.http :as http]))

(defn version []
  (-> "version.txt" io/resource slurp .trim))

(defn esver [host port]
  (get-in (http/get host port "/") [:version :number]))

(defn go [args {:keys [host port]}]
  (let [ver (version)
        esver (or (esver host port)
                  (format "not running at %s:%s" host port))]
    (with-out-str
      (println "es" ver)
      (println "elasticsearch" esver))))
