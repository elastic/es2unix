(ns es.format.table)

(defn dispatch [{:keys [output]} data]
  (keyword output))

(defmulti tabler dispatch)

(defmethod tabler :default [{:as opts} data]
  "no output format")

(defmethod tabler :raw [{:as opts} data]
  (str data))
