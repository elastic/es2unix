(ns es.command.lifecycle
  (:require [clojure.java.io :as io]))

(def pat-pre
  (str "^\\[([^]]+)\\]" ;; timestamp
       "\\[[^]]+\\]\\[[^]]+\\] " ;; log level & package
       "\\[([^]]+)\\] " ;; node name
       ))

(def pat-post
  "")

(def op-pats
  [["\\{([^}]+)\\}\\[[^]]+\\]: initializing "
    'INIT
    [:timestamp :me :version]
    [:timestamp :op :me :version]]
   ["\\{([^}]+)\\}\\[[^]]+\\]: started"
    'START
    [:timestamp :me :version]
    [:timestamp :op :me :_]]
   ["\\{([^}]+)\\}\\[[^]]+\\]: stopped"
    'STOP
    [:timestamp :me :version]
    [:timestamp :op :me :_]]
   ["(new|detected)_master \\[([^]]+)\\]\\[[^]]+\\]\\[inet\\[.*?/([^]]+)\\]"
    'MASTER
    [:timestamp :me :_ :her-name :her-ip]
    [:timestamp :op :me :her-name]]
   ["added \\{\\[([^]]+)\\]\\[[^]]+\\]\\[inet\\[.*?/([^]]+)\\]"
    'ADD
    [:timestamp :me :her-name :her-ip]
    [:timestamp :op :me :her-name]]
   ["removed \\{\\[([^]]+)\\]\\[[^]]+\\]\\[inet\\[.*?/([^]]+)\\]"
    'REMOVE
    [:timestamp :me :her-name :her-ip]
    [:timestamp :op :me :her-name]]])

(defn match [pats line]
  (let [f (fn [[pat op ks output]]
            (if-let [matched (re-find (re-pattern
                                       (str pat-pre pat pat-post)) line)]
              (merge {:op op
                      :output output}
                     (zipmap ks (rest matched)))))]
    (->> pats
         (map f)
         (some identity))))

(defn ops [rdr]
  (->> (line-seq rdr)
       (map #(match op-pats %))
       (filter identity)))

(defn lifecycle [_ files opts]
  (->> (for [f files
             op (ops (io/reader f))]
         ((apply juxt (:output op)) op))
       sort))
