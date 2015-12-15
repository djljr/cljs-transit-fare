(defproject com.djljr/fare "1.0-SNAPSHOT"
  :description "Web page to figure out monthly vs. trip fares"
  :url "http://transit-fare.djljr.com"

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.170"]
                 [org.clojure/core.async "0.2.374"]]

  :plugins [[lein-figwheel "0.5.0-2"]]

  :source-paths ["src"]
  :clean-targets ^{:protect false} ["resources/public/js" :target]
  :test-paths ["test"]

  :profiles {:dev
             {:dependencies [[com.cemerick/piggieback "0.2.1"]
                             [figwheel-sidecar "0.5.0-2"]]}
             :repl {:plugins [[cider/cider-nrepl "0.9.1"]]}}
  
  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
  :cljsbuild { 
    :builds [{:id "dev"
              :source-paths ["src/"]
              :figwheel {:on-jsload "fare.core/mount-root"}
              :compiler {:main "fare.core"
                         :asset-path "js/out"
                         :output-to "resources/public/js/fare.js"
                         :outpur-dir "resources/public/js/out"}}]})
