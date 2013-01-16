(ns es.format.uri
  (:require [clj-http.util :refer [url-encode]]))

(defn encode
  ([s]
     (url-encode s)))
