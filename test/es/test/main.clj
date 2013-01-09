(ns es.test.main
  (:require [clojure.test :refer :all]
            [es.main :refer :all])
  (:require [es.command.version :as version]))

(def defaults
  {:host "localhost"
   :port 9200})

(deftest dispatch
  (is (= :fail (main "foo" nil nil)))
  (is (= (format "es %s\nelasticsearch not running at localhost:99\n"
                 (version/version))
         (main "version" [] {:host "localhost" :port 99}))))

