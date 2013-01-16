(ns es.format.error
  (:import (com.google.common.base Throwables)))

(def sep (apply str (repeat 72 "-")))

(defn stack-trace
  ([thrown]
     (with-out-str
       (println sep)
       (print (Throwables/getStackTraceAsString thrown))
       (print sep))))
