(ns es.util.math)

(defn ratio [a b]
  (if (pos? b)
    (float (/ a b))
    0))

(defn percent [a b & [precision]]
  (let [fmt (format "%%.%sf%%%%" (or precision 1))]
    (format fmt (* 100 (ratio a b)))))
