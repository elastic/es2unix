(ns es.format.uri
  (:require [clj-http.util :refer [url-encode]]))

(defn encode
  ([s]
     (url-encode s)))

(defn query-string [params]
  (if (seq params)
    (->> params
         (map str)
         (interpose "&")
         (apply str))
    ""))

(defn query-flags [flags]
  (if (seq flags)
    (->> flags
         (map name)
         (map #(str % "=1"))
         query-string)))
