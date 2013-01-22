(ns es.data.search
  (:require [es.format.uri :as uri]
            [es.util :refer [commafy]]))

(defn field-list [fields]
  (->> fields
       (map str)
       (map uri/encode)
       (interpose ",")
       (apply str)))

(defn search
  ([http]
     (search http "*:*" []))
  ([http query fields]
     (http (str "/_search?q=" (uri/encode query)
                "&fields=" (field-list fields)))))
