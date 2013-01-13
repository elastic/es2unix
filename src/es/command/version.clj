(ns es.command.version
  (:require [clojure.java.io :as io]
            [es.http :as http]))

(defn version []
  (-> "version.txt" io/resource slurp .trim))

(defn esver [url]
  (get-in (http/get url) [:version :number]))

(defn go [args {:keys [url]}]
  (let [ver (version)
        esver (or (esver url)
                  (format "not running at %s" url))]
    [["es" ver]
     ["elasticsearch" esver]]))
