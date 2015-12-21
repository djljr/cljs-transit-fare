(defproject com.djljr/fare "1.0-SNAPSHOT"
  :description "Web page to figure out monthly vs. trip fares"
  :url "http://transit-fare.djljr.com"

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.170"]
                 [org.clojure/core.async "0.2.374"]
                 [reagent "0.5.1"]
                 [re-frame "0.6.0"]]

  :plugins [[lein-cljsbuild "1.1.2"]
            [lein-figwheel "0.5.0-2"]]

  :source-paths ["src"]
  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]
  :test-paths ["test"]

  :profiles {:dev
             {:dependencies [[com.cemerick/piggieback "0.2.1"]
                             [figwheel-sidecar "0.5.0-2"]]}
             :repl {:plugins [[cider/cider-nrepl "0.9.1"]
                              [refactor-nrepl "1.1.0"]]}}

  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

  :cljsbuild {
              :builds {:dev
                       {:source-paths ["src"]
                        :figwheel {:on-jsload "fare.core/mount-root"}
                        :compiler {:main fare.core
                                   :asset-path "js/compiled/out"
                                   :output-to "resources/public/js/compiled/fare.js"
                                   :outpur-dir "resources/public/js/compiled/out"
                                   :source-map-timestamp true}}
                       :min
                       {:source-paths ["src"]
                        :compiler {:main fare.core
                                   :output-to "resources/public/js/compiled/fare.js"
                                   :optimizations :advanced
                                   :pretty-print false}}}}

  :figwheel {:css-dirs ["resources/public/css"]})
