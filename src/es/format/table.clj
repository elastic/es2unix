(ns es.format.table)

(defn type-of [cell]
  (condp #(= (type %2) %1) cell
    String :s
    Integer :d
    Long :d
    clojure.lang.BigInt :d
    (throw
     (Exception.
      (format "what type is this? %s"
              cell)))))

(defn sizes [row]
  (apply merge
         (map-indexed (fn [i cell]
                        (sorted-map
                         i {:type (type-of cell)
                            :size (count (str cell))}))
                      row)))

(defn fmtmeta [data]
  (apply
   merge-with
   (fn [a b]
     (if (>= (:size a) (:size b)) a b))
   (map sizes data)))

(defn fmt [sep data]
  (let [f (fn [xs [_ x]]
            (let [neg (if (= :s (:type x))
                        "-"
                        "")]
              (conj xs (str "%" neg (:size x) (name (:type x))))))]
    (interpose sep (reduce f [] (fmtmeta data)))))

(defn strings [sep data]
  (let [fmt (apply str (fmt sep data))]
    (map (partial apply format fmt) data)))

(defn dispatch [{:keys [output] :as opts} data]
  (keyword output))

(defmulti tabler
  "Return "
  dispatch)

(defmethod tabler :default [opts data]
  "no output format")

(defmethod tabler :raw [opts data]
  (doseq [l (strings " " data)]
    (println l)))
