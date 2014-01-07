(defproject com.djljr/fare "1.0-SNAPSHOT"
  :description "Web page to figure out monthly vs. trip fares"
  :url "http://transit-fare.djljr.com"

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2138"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]]

  :plugins [[lein-cljsbuild "1.0.1"]
            [com.cemerick/clojurescript.test "0.2.1"]]

  :source-paths ["src"]
  :test-paths ["test"]

  :cljsbuild { 
    :builds [{:id "fare"
              :source-paths ["src"]
              :compiler {
                :output-to "fare.js"
                :output-dir "out"
                :optimizations :none
                :source-map true}}
             {:id "fare-test"
              :source-paths ["src", "test"]
              :compiler {
                :output-to "out/test/fare.js"
                :optimizations :whitespace
                :pretty-print false}}]
    :test-commands {"unit-tests" ["phantomjs" :runner
                                  "out/test/fare.js"]}})