(ns es.local
  (:refer-clojure :exclude [get])
  (:require [clojure.java.io :as io]))

(defn localize-path [path]
  (str "output" path ".json"))

(defn get [uri]
  (if-let [json (-> uri
                    localize-path
                    io/resource slurp)]
    {:status 200
     :headers {"content-type" "application/json; charset=UTF-8"}
     :body json}))
