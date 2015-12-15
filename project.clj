(defproject com.djljr/fare "1.0-SNAPSHOT"
  :description "Web page to figure out monthly vs. trip fares"
  :url "http://transit-fare.djljr.com"

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.170"]
                 [org.clojure/core.async "0.2.374"]
                 [reagent "0.5.1"]]

  :plugins [[lein-cljsbuild "1.1.1"]
            [lein-figwheel "0.5.0-2"]]

  :source-paths ["src"]
  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]
  :test-paths ["test"]

  :profiles {:dev
             {:dependencies [[com.cemerick/piggieback "0.2.1"]
                             [figwheel-sidecar "0.5.0-2"]]}
             :repl {:plugins [[cider/cider-nrepl "0.9.1"]]}}
  
  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
  :cljsbuild { 
    :builds [{:id "dev"
              :source-paths ["src"]
              :figwheel {:on-jsload "fare.core/mount-root"}
              :compiler {:main fare.core
                         :asset-path "js/compiled/out"
                         :output-to "resources/public/js/compiled/fare.js"
                         :outpur-dir "resources/public/js/compiled/out"
                         :source-map-timestamp true}}
             {:id "min"
              :source-paths ["src"]
              :compiler {:output-to "resources/public/js/compiled/fare.js"
                         :main fare.core
                         :optimizations :advanced
                         :pretty-print false}}]}
  :figwheel {:css-dirs ["resources/public/css"]})
