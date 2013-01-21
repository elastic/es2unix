(ns es.util)

(defn maybe-get-in
  ([m & ks]
     (or (get-in m ks) " ")))

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

(defn commafy [n]
  (->> (str n)
       reverse
       (partition-all 3)
       (interpose ",")
       (apply concat)
       reverse
       (apply str)))

(defn deep-merge-with
  "Like merge-with, but merges maps recursively, applying the given fn
  only when there's a non-map at a particular level.

  (deep-merge-with + {:a {:b {:c 1 :d {:x 1 :y 2}} :e 3} :f 4}
               {:a {:b {:c 2 :d {:z 9} :z 3} :e 100}})
  -> {:a {:b {:z 3, :c 3, :d {:z 9, :x 1, :y 2}}, :e 103}, :f 4}"
  [f & maps]
  (apply
   (fn m [& maps]
     (if (every? map? maps)
       (apply merge-with m maps)
       (apply f maps)))
   maps))

(defn merge-transpose
  " (foo {:a {:k {:d 1}}}
         {:b {:k {:d 2}}}
         {:c {:k {:d 3}
              :j {:d 4}}})

       => {:k {:a {:d 1}
               :b {:d 2}
               :c {:d 3}}
           :j {:c {:d 4}}}
  "
  ([& a]
     (->> (for [m a
                [k1 v1] m
                [k2 v2] v1]
            [k2 {k1 v2}])
          (map (partial apply hash-map))
          (apply merge-with merge))))
