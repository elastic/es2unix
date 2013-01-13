(ns es.test.command.health
  (:require [clojure.test :refer :all])
  (:require [es.command.health :as health]))

(deftest health
  (is (= [["elasticsearch" "green" 3 3 3 3 0 0 0]]
         (health/go [] {:url "local:"}))))
