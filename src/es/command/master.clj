(ns es.command.master
  (:require [es.data.nodes :as nodes]
            [es.format.network :refer [ip]]))

(defn master [http args opts]
  (let [res (nodes/master http)]
    (or
     (:http-error res)
     (let [id (:master_node res)
           m (get (:nodes res) (keyword id))]
       [[id (ip (:transport_address m)) (:name m)]]))))
