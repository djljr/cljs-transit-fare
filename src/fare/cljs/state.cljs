(ns fare.state
  (:require [goog.net.cookies :as cks]
            [cljs.reader :as reader]
            [reagent.core :as reagent :refer [atom]]))

(def app-state (atom {:start-date "2015-12-01"
                      :thirty-day-price 83.00
                      :forty-trip-price 90.00
                      :vacation #{}
                      :holidays #{}
                      :extra-trips 0}))

(def initial-state @app-state)

(defn load [state]
  (let [state-string (.get goog.net.cookies "state")]
  	(if (undefined? state-string) 
     (reset! state @state)
     (reset! state (reader/read-string state-string)))))

(defn save [st]
  (let [state-string (pr-str st)]
    (.set goog.net.cookies "state" state-string)))
