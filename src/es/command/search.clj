(ns es.command.search
  (:require [es.data.search :as search]
            [es.util :refer [substr]]))

(def max-width
  20)

(defn search [http args opts]
  (let [[q & fields] args
        resp (if q
               (search/search http q fields)
               (search/search http))
        total (get-in resp [:hits :total])
        header [(concat
                 ['score
                  'index
                  'type
                  'id]
                 (if (seq fields)
                   fields))]]
    (concat
     (if (:verbose opts) header)
     (for [hit (:hits (:hits resp))]
       (concat
        [(-> (:_score hit) (substr 7))
         (:_index hit)
         (:_type hit)
         (:_id hit)]
        (if (:fields hit)
          (for [f fields]
            (-> hit
                (get-in [:fields (keyword f)])
                (substr max-width)
                (.replaceAll "\n" "")
                .trim)))))
     [(concat
       [" Total:" {:val total :just :<-}]
       (repeat (+ 2 (count fields)) " "))])))
