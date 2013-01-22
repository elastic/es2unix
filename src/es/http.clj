(ns es.http
  (:refer-clojure :exclude [get])
  (:require [clj-http.client :as http]
            [cheshire.core :as json]
            [es.local :as local]
            [slingshot.slingshot :refer [throw+]])
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
      (catch java.lang.IllegalArgumentException e
        (throw+ {:type ::error
                 :msg (format "bad url: %s" (.getMessage e))}))
      (catch org.apache.http.NoHttpResponseException e
        (throw+ {:type ::error
                 :msg (format "no response from %s" url)}))
      (catch java.net.UnknownHostException e
        (throw+ {:type ::error
                 :msg (format "unknown host %s" (.getHost uri))}))
      (catch java.net.ConnectException e
        (throw+ {:type ::error
                 :msg (format "can't connect to %s" url)}))
      (catch clojure.lang.ExceptionInfo e
        (throw+ {:type ::error
                 :msg (format
                       "%s: %s"
                       url (-> e .getData :object :status))}))
      (catch java.net.MalformedURLException e
        (throw+ {:type ::error
                 :msg (format
                       "need complete url (with scheme!): %s"
                       url)})))))

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

(def get
  (memoize-cond
   get*
   (fn [args]
     (let [[arg] args]
       (if (not (local? arg)) true)))))
