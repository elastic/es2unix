(ns es.command.ids
  (:require [es.data.search :as search]))

;; Intentionally does not use tabler, since that causes OOMEs for
;; large result sets
(defn ids [http args {:keys [verbose]}]
  (when verbose
    (println "index type id"))
  (if-let [idx (first args)]
    (let [estype (second args)]
      (doall (map #(println (:_index %) (:_type %) (:_id %))
                  (search/scroll http idx estype "*:*" [] 100)))
      ;; Return something that won't cause the output formatter to
      ;; blow up
      [])
    (println "usage: es ids INDEX [TYPE]")))
