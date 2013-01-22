(ns es.command.count
  (:refer-clojure :exclude [count])
  (:require [es.data.cluster :as cluster]
            [es.util :as util]))

(defn count [http args {:keys [verbose]}]
  (let [[q] args
        res (if q
              (cluster/count http q)
              (cluster/count http))
        failed (or (get-in res [:_shards :failed]) 99999)
        out (util/commafy (:count res))
        out (if q (format "%s \"%s\"" out q) out)
        out (if (pos? failed)
              (format "%s ...with %d failed shards!" out failed)
              out)]
    [[out]]))
