(ns es.data.search
  (:require [cheshire.core :as json]
            [es.format.uri :as uri]
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

(defn- scroll-fn
  "Private scroll function meant for interation through scrolling queries"
  [http sid fields]
  (lazy-seq
   (let [resp (http (str "/_search/scroll?search_type=scan&"
                         "scroll=5m&scroll_id=" sid))
         new-sid (:_scroll_id resp)]
     (when-let [hits (seq (-> resp :hits :hits))]
       (concat hits (scroll-fn http new-sid fields))))))

(defn scroll
  "General purpose scrolling method"
  ([http]
     (scroll nil nil "*:*" [] 100))
  ([http idx type query fields size]
     (let [resp (http (str (when idx (str "/" idx))
                           (when type (str "/" type))
                           "/_search?search_type=scan&fields="
                           (field-list fields)
                           "&scroll=5m&size=" size))
           sid (:_scroll_id resp)]
       (concat (-> resp :hits :hits)
               (scroll-fn http sid fields)))))
