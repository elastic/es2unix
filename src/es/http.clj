(ns es.http
  (:refer-clojure :exclude [get])
  (:require [clj-http.client :as http]
            [cheshire.core :as json]))

(defn url [host port uri]
  (format "http://%s:%s%s" host port uri))

(defn get [host port uri]
  (try
    (-> (url host port uri)
        http/get
        :body
        (#(json/decode % true)))
    (catch java.net.ConnectException e
      nil)))
