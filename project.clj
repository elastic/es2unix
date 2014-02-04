(defproject org.elasticsearch/es
  (try (-> "etc/version.txt" slurp .trim)
       (catch java.io.FileNotFoundException _ "0.0.1"))
  :description "es2unix"
  :resource-paths ["etc" "resources"]
  :url "http://elasticsearch.org/"
  :license {:name "Apache License, Version 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/tools.cli "0.2.2"]
                 [cheshire "5.0.2"]
                 [clj-http "0.6.4"]
                 [log4j/log4j "1.2.17"]
                 [bultitude "0.2.0"]
                 [slingshot "0.10.3"]
                 [com.google.guava/guava "14.0-rc1"]]
  :plugins [[lein-bin "0.3.0"]]
  :main es.main
  :uberjar-name "es.jar"
  :jvm-opts ["-XX:TieredStopAtLevel=1"
             "-XX:+TieredCompilation"]
  :bin {:bootclasspath :yes!})
