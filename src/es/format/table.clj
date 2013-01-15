(ns es.format.table)

(defn type-of [cell]
  (condp #(= (type %2) %1) cell
    Integer :num
    Long :num
    clojure.lang.BigInt :num
    :str))

(defn widths [row]
  (apply merge
         (map-indexed (fn [i cell]
                        (sorted-map
                         i {:type (type-of cell)
                            :size (count (str (or cell " ")))}))
                      row)))

(defn fmtmeta [data]
  (apply
   merge-with
   (fn [a b]
     {:size (if (>= (:size a) (:size b))
              (:size a)
              (:size b))
      :type (:type b)})
   (map widths data)))

(defn fmt [sep data]
  (let [f (fn [xs [_ x]]
            (let [neg (if (= :str (:type x))
                        "-"
                        "")]
              (conj xs (str "%" neg (:size x) "s"))))]
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
