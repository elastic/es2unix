(ns es.format.table)

(defrecord Cell [val just width])

(defrecord Row [widths cells])

(defrecord Table [widths rows])

(def default-cell-value
  " ")

(defn cell? [x]
  (= Cell (type x)))

(defn justify [cell]
  (condp #(= (type %2) %1) cell
    Integer :->
    Long :->
    clojure.lang.BigInt :->
    :<-))

(defn getn [m k not-found]
  (if-let [v (get m k)]
    v
    not-found))

(defn make-cell [x]
  (let [count* #(count (str (or % " ")))
        width (if (map? x)
                (:width x (count* (:val x)))
                (count* x))]
    (if (map? x)
      (Cell. (getn x :val default-cell-value)
             (:just x (justify (:val x)))
             width)
      (Cell. (or x default-cell-value) (justify x) width))))

(defn make-row [r]
  (let [cells (map make-cell r)
        widths (->> cells
                    (map-indexed
                     (fn [i cell] (sorted-map i (:width cell))))
                    (apply merge))]
    (Row. widths cells)))

(defn make-table [rows]
  (let [rows (map make-row rows)
        widths (apply merge-with max (map :widths rows))]
    (Table. widths rows)))

(defn fmt [sep table]
  (let [widths (-> table :widths vals)
        aligns (->> (-> table :rows last)
                    :cells
                    (map :just)
                    (map {:<- "-"
                          :-> ""}))]
    (->> [(repeat (count widths) "%")
          aligns
          widths
          (repeat (count widths) "s")]
         (apply map str)
         (interpose sep))))

(defn strings [sep data]
  (let [table (make-table data)
        fmt (apply str (fmt sep table))
        values (for [row (:rows table)]
                 (map :val (:cells row)))
        _ (when (not (= (count (first values))
                        (count (.split fmt " "))))
            (throw (Exception. (with-out-str
                                 (println "fmt string")
                                 (prn fmt)
                                 (println "and values seqs")
                                 (prn values)
                                 (println "are different lengths")))))]
    (map (partial apply format fmt) values)))

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
