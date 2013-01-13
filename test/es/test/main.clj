(ns es.test.main
  (:require [clojure.test :refer :all]
            [es.main :refer :all])
  (:require [es.command.version :as version]))

(def defaults
  {:url "http://localhost:99"})

(deftest dispatch
  (is (= :fail (main "foo" nil nil)))
  (is (= [["es" "0.9.0"]
          ["elasticsearch" "not running at http://localhost:99"]]
         (main "version" [] defaults))))
