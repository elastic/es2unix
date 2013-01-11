(ns es.format.network)

(defn parse-addr [addr]
  (let [[_ proto ip port]
        (re-find #"(.*?)\[/([0-9a-f.:]+):([0-9]+)\]" addr)]
    {:proto proto
     :ip ip
     :port port}))

(defn proto [addr]
  (:proto (parse-addr addr)))

(defn ip [addr]
  (:ip (parse-addr addr)))

(defn port [addr]
  (:port (parse-addr addr)))
