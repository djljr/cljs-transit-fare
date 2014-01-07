(defproject transit-calc "1.0-SNAPSHOT"
  :description "Web page to figure out monthly vs. trip fares"
  :url "http://transit-calc.djljr.com"

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2138"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]]

  :plugins [[lein-cljsbuild "1.0.1"]]

  :source-paths ["src"]

  :cljsbuild { 
    :builds [{:id "transit-calc"
              :source-paths ["src"]
              :compiler {
                :output-to "transit_calc.js"
                :output-dir "out"
                :optimizations :none
                :source-map true}}]})
