(ns es.http
  (:refer-clojure :exclude [get])
  (:require [clj-http.client :as http]
            [cheshire.core :as json]
            [es.local :as local])
  (:import (java.net URI)))

(defn get [url]
  (let [uri (URI. url)
        getfn (if (= "local" (.getScheme uri))
                #(local/get (.getPath uri))
                #(http/get url))]
    (try
      (-> (getfn) :body (#(json/decode % true)))
      (catch java.net.ConnectException e
        nil))))
