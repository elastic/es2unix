(ns es.data.replica
  (:require [es.util :as util]
            [slingshot.slingshot :refer [throw+]]))

(defn maybe
  "Look for matches in indices for the :index in the replica.  It
  could be in :routing depending on the context."
  ([rep indices]
     (let [s (or (get-in rep [:routing :index])
                 (:index rep)
                 (throw+
                  {:type ::error
                   :msg (with-out-str
                          (print "replica ")
                          (prn rep)
                          (print " didn't have any routing when matching ")
                          (prn indices))}))]
       (if (util/match-any? s indices)
         rep))))

(defn primary? [replica]
  (if (contains? replica :primary)
    (:primary replica)
    (if-let [routing (-> replica :routing)]
      (-> routing :primary)
      (throw+
       {:type ::error
        :msg (with-out-str
               (println "replica doesn't have routing info")
               (prn replica))}))))

(defn make-key [routing]
  (let [kw (fn [x]
             (cond
               (keyword? x) x
               (string? x) (keyword x)
               (number? x) (keyword (str x))
               :else x))
        gets (juxt :index :shard :primary :node)]
    (->> routing gets (map kw) vec)))
