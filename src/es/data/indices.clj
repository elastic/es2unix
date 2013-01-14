(ns es.data.indices
  (:require [es.http :as http]))

(defn status
  ([url]
     (http/get (str url "/_status")))
  ([url & indices]
     (let [idxs (apply str (interpose "," indices))]
       (http/get (str url "/" idxs "/_status")))))

(defn primary-shard? [shard]
  (if-let [routing (-> shard :routing)]
    (-> routing :primary)))
