(ns es.test.command.nodes
  (:require [clojure.test :refer :all])
  (:require [es.command.nodes :as nodes]))

(deftest nodes
  (is (= (.trim "
j27iagsmQQaeIpl6yU6mCg 127.0.0.1 9203 127.0.0.1 9303   Georgianna Castleberry
Q4qgMlrETs67mUL-iGHC4A 127.0.0.1 9202 127.0.0.1 9302   Luichow, Chan
J-erllamTOiW5WoGVUd04A 127.0.0.1 9200 127.0.0.1 9301   Slade, Frederick
Uv1Iy8FvR0y6_RzPXKBolg 127.0.0.1 9201 127.0.0.1 9300   Cannonball I
")
         (.trim
          (nodes/go [] {:url "local:"})))))
