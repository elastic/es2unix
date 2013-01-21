(ns es.main
  (:require [cljs.nodejs :as node]))

(defn js-parse [json]
  (js->clj (JSON/parse json)))

(defn error [response]
  (println "error:" response))

(defn handler [response]
  (.on response "data"
       (fn [chunk]
         (let [m (js-parse (.toString chunk "UTF-8"))]
           (println
            (get-in m ["version" "number"])
            (get-in m ["name"]))))))

(defn start [& args]
  (let [[cmd opts] args
        http (node/require "http")
        get (.get http "http://localhost:9200" handler)]
    (.on get "error" error)))

(set! *main-cli-fn* start)
