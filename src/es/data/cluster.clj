(ns es.data.cluster
  (:require [es.http :as http]))

(defn health [url]
  (http/get (str url "/_cluster/health")))

