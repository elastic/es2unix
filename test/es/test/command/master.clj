(ns es.test.command.master
  (:require [clojure.test :refer :all])
  (:require [es.command.master :as master]))

(deftest master
  (is (= [["BbGA6DgrQ4ypROuLuxjSBA" "192.168.20.115" "Klaw"]]
         (master/go [] {:url "local:"}))))
