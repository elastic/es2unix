(ns es.http
  (:refer-clojure :exclude [get])
  (:require [clj-http.client :as http]
            [cheshire.core :as json]
            [es.local :as local])
  (:import (java.net URI)))

(defn local? [url]
  (and (string? url) (.startsWith url "local:")))

(defn get* [url]
  (let [uri (URI. url)
        [path query] [(.getPath uri) (.getQuery uri)]
        getfn (if (= "local" (.getScheme uri))
                #(local/get (str path
                                 (if query
                                   (str "?" query))))
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

(defn memoize-cond
  "Only return memoized value if (pred args) is true."
  [f pred]
  (let [mem (atom {})]
    (fn [& args]
      (if (pred args)
        (if-let [e (find @mem args)]
          (val e)
          (let [ret (apply f args)]
            (swap! mem assoc args ret)
            ret))
        (apply f args)))))

(def get (memoize-cond
          get*
          (fn [args]
            (let [[arg] args]
              (if (not (local? arg)) true)))))
