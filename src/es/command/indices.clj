(ns es.command.indices
  (:require [es.data.indices :as idx]))

(def cols
  ['status
   'name
   'pri
   'rep
   'docs
   'size])

(defn go [args {:keys [url verbose]}]
  (concat
   (if verbose
     [(map str cols)])
   (for [[nam data] (idx/indices url args)]
     (let [pri (- (-> data :health :number_of_shards)
                  (-> data :health :number_of_replicas))]
       [(-> data :health :status)
        (name nam)
        pri
        (-> data :health :number_of_replicas)
        (-> data :total :docs)
        (-> data :total :bytes)]))))
