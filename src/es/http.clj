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
      (catch org.apache.http.NoHttpResponseException _
        {:http-error (format "no response from %s" url)})
      (catch java.net.UnknownHostException _
        {:http-error (format "unknown host %s" (.getHost uri))})
      (catch java.net.ConnectException _
        {:http-error (format "can't connect to %s" url)})
      (catch clojure.lang.ExceptionInfo e
        {:http-error (format "%s: %s"
                             url (-> e .getData :object :status))}))))
