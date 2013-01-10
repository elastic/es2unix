(defproject org.elasticsearch/es
  (-> "etc/version.txt" slurp .trim)
  :description "es2unix"
  :resource-paths ["etc" "resources"]
  :url "http://elasticsearch.org/"
  :license {:name "Apache License, Version 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/tools.cli "0.2.2"]
                 [cheshire "5.0.1"]
                 [clj-http "0.6.3"]
                 [log4j/log4j "1.2.16"]]
  :main es.main
  :uberjar-name "es.jar")
