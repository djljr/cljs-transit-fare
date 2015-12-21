(ns fare.state
  (:require [goog.net.cookies :as cks]
            [cljs.reader :as reader]
            [reagent.core :as reagent :refer [atom]]
            [re-frame.core :as re]))

(def initial-state
  {:start-date "2015-12-01"
   :thirty-day-price "89.00"
   :forty-trip-price "84.00"
   :vacation ""
   :holiday ""
   :vacation-list #{}
   :holiday-list #{"2015-12-24" "2015-12-25" "2016-01-04" "2016-01-18"}
   :extra-trips 0})

(re/register-handler
 :initialize
 (fn [db _]
   (merge db initial-state)))

(defn load [state]
  (let [state-string (.get goog.net.cookies "state")]
    (if (undefined? state-string)
      (reset! state @state)
      (reset! state (reader/read-string state-string)))))

(defn save [st]
  (let [state-string (pr-str st)]
    (.set goog.net.cookies "state" state-string)))
