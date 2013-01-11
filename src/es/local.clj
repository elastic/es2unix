(ns es.local
  (:refer-clojure :exclude [get])
  (:require [clojure.java.io :as io]))

(defn localize-path [path]
  (str "output" path ".json"))

(defn get [uri]
  (if-let [f (-> uri
                 localize-path
                 io/resource)]
    {:status 200
     :headers {"content-type" "application/json; charset=UTF-8"}
     :body (-> f slurp)}
    (throw
     (clojure.lang.ExceptionInfo.
      (format "not found: %s" uri)
      {:object
       {:status 404}}))))
