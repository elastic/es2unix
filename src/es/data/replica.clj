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
       (util/match-any? rep indices))))

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
