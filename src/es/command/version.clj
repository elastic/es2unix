(ns es.command.version
  (:require [clojure.java.io :as io]))

(defn version* []
  (-> "version.txt" io/resource slurp .trim))

(defn esver [http]
  (get-in (http) [:version :number]))

(defn version [http args opts]
  (let [ver (version*)
        esver (or (esver http)
                  (format "not running at %s" (:url opts)))]
    [["es" ver]
     ["elasticsearch" esver]]))
