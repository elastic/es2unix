(ns es.data.indices
  (:require [es.data.cluster :as cluster]
            [es.util :as util]))

(defn index-slice
  ([http endpoint indices]
     (let [lst (util/comma-list indices)
           lst (if (pos? (count lst)) (str "/" lst) "")]
       (http (str lst endpoint)))))

(defn stats
  ([http]
     (stats http []))
  ([http indices]
     (index-slice http "/_stats" indices)))

(defn indices
  ([http]
     (indices http []))
  ([http indices]
     (util/merge-transpose
      {:health (cluster/health http indices)}
      {:stats (get-in (stats http indices) [:indices])})))



