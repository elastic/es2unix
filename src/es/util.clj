(ns es.util)

(defn maybe-rep
  "Look for matches in indices for the :index in the replica.  It
  could be in :routing depending on the context."
  ([rep indices]
     (if (seq indices)
       (if (some #(.contains (or (get-in rep [:routing :index])
                                 (:index rep)
                                 "!@#$%^") %) indices)
         rep)
       rep)))

(defn comma-list [idxs]
  (if (seq idxs)
    (->> idxs
         (map #(str "*" % "*"))
         (interpose ",")
         (apply str))
    ""))

(defn primary? [replica]
  (if (contains? replica :primary)
    (:primary replica)
    (if-let [routing (-> replica :routing)]
      (-> routing :primary)
      (throw (Exception.
              (with-out-str
                (println "replica doesn't have routing info")
                (prn replica)))))))
