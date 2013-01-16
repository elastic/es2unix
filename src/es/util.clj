(ns es.util)

(defn match-any?
  "If any of the string frags match string thing or there are no frags,
  return the thing. Otherwise nil."
  ([thing frags]
     (if (seq frags)
       (if (some #(.contains thing %) frags)
         thing)
       thing)))

(defn comma-list [idxs]
  (if (seq idxs)
    (->> idxs
         (map #(str "*" % "*"))
         (interpose ",")
         (apply str))
    ""))

