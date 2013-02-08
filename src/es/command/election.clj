(ns es.command.election
  (:require [clojure.java.io :as io]))

(def pat-pre
  (str "^\\[([^]]+)\\]" ;; timestamp
       "\\[[^]]+\\]\\[[^]]+\\] " ;; log level & package
       "\\[([^]]+)\\] " ;; node name
       ))

(def pat-post
  "")

(def op-pats
  [["added \\{\\[([^]]+)\\]\\[[^]]+\\]\\[inet\\[([^]]+)\\]"
    'ADD [:timestamp :me :her-name :her-ip]]
   ["removed \\{\\[([^]]+)\\]\\[[^]]+\\]\\[inet\\[([^]]+)\\]"
    'REMOVE [:timestamp :me :her-name :her-ip]]])

(defn match [pats line]
  (let [f (fn [[pat op ks]]
            (if-let [matched (re-find (re-pattern
                                       (str pat-pre pat pat-post)) line)]
              (merge {:op op} (zipmap ks (rest matched)))))]
    (->> pats
         (map f)
         (some identity))))

(defn ops [rdr]
  (->> (line-seq rdr)
       (map #(match op-pats %))
       (filter identity)))

(defn election [_ files opts]
  (->> (for [f files
             op (ops (io/reader f))]
         (condp = (:op op)
           'ADD ((juxt :timestamp :op :me :her-name :her-ip) op)
           'REMOVE ((juxt :timestamp :op :me :her-name :her-ip) op)))
       sort))
