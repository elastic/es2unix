(ns es.util.time
  (:import (java.text SimpleDateFormat)))

(defn now []
  (java.util.Date.))

(defn hm
  ([]
     (hm (now)))
  ([date]
      (.format (SimpleDateFormat. "HH:mm:ss") date)))
