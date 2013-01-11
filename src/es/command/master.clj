(ns es.command.master
  (:require [es.http :as http]
            [es.format.network :refer [ip]]))

(defn master [url]
  (http/get (format "%s%s" url
                    (str "/_cluster/state?"
                         "filter_metadata=1&"
                         "filter_routing_table=1&"
                         "filter_indices=1"))))

(defn master-id [url]
  (:master_node (master url)))

(defn go [args {:keys [url]}]
  (let [res (master url)]
    (or
     (:http-error res)
     (let [id (:master_node res)
           m (get (:nodes res) (keyword id))]
       (format "%s %s %s" id (ip (:transport_address m))
          (:name m))))))
