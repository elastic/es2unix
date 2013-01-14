(ns es.test.command.nodes
  (:require [clojure.test :refer :all]
            [es.command.nodes :as nodes]))

(deftest nodes
  (is (= [["Uv1Iy8FvR0y6_RzPXKBolg" "127.0.0.1" "9201"
           "127.0.0.1" "9300" " " "d" "Cannonball I"]
          ["j27iagsmQQaeIpl6yU6mCg" "127.0.0.1" "9203"
           "127.0.0.1" "9303" "-" "c" "Georgianna Castleberry"]
          ["Q4qgMlrETs67mUL-iGHC4A" "127.0.0.1" "9202"
           "127.0.0.1" "9302" " " "d" "Luichow, Chan"]
          ["AvYWeugwQjqs0cB1vr3D-w" "127.0.0.1" "9204"
           "127.0.0.1" "9304" "-" "d" "Eric the Red"]
          ["J-erllamTOiW5WoGVUd04A" "127.0.0.1" "9200"
           "127.0.0.1" "9301" " " "d" "Slade, Frederick"]]
         (nodes/go [] {:url "local:"}))))
