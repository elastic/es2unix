(ns es.main
  (:require [cljs.nodejs :as node]
            [es.common.cluster :as cluster]
            #_[es.format.table :as table]))

(defn js-parse [json]
  (js->clj (JSON/parse json)))

(defn httpget [url]
  (let [http (node/require "http")
        err (fn [r]
              (println "error:" r))
        handler (fn [resp]
                  (let [body (atom [])]
                    (.on resp "data"
                         (fn [chunk]
                           (swap! body conj
                                  (-> chunk (.toString "UTF-8") js-parse))))
                    (.on resp "end"
                         (fn [& args]
                           @body))))
        get (.get http url handler)]
    (.end get)))

(defmulti run (fn [& xs] (first xs)))

(defmethod run "version" [_ url args]
  (let [m (httpget url)]
    (println
     (get-in m ["version" "number"])
     (get-in m ["name"]))))

(defmethod run :default [& args]
  (println "default:" args))

(defn start [& args]
  (let [[cmd & opts] args]
    (println (run cmd "http://localhost:9200" opts))))

(set! *main-cli-fn* start)
