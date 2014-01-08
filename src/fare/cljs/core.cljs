(ns djljr.fare.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [goog.dom :as dom]
            [goog.events :as events]
            [goog.date :as date]
            [goog.i18n.DateTimeFormat]
            [goog.i18n.DateTimeParse]
            [cljs.core.async :refer [put! chan <!]]))

(defn now-string []
  (.format (goog.i18n.DateTimeFormat. "yyyy-MM-dd") (date/Date.)))

(defn date [date-string]
  (.strictParse (goog.i18n.DateTimeParse. "yyyy-MM-dd") date-string))

(defn is-weekday? [d]
  (not (contains? #{5 6} (.getIsoWeekday d))))
  
(def state (atom {:monthly-price 80.00 :thirty-day-price 76.00 :start-date (now-string)}))

(defn listen [el type]
  (let [out (chan)]
    (events/listen el type
                   (fn [e] (put! out e)))
    out))

(defn init [state]
  (set! (.-value (dom/getElement "start-date")) (:start-date state))
  (set! (.-value (dom/getElement "monthly-price")) (:monthly-price state))
  (set! (.-value (dom/getElement "thirty-day-price")) (:thirty-day-price state)))

(defn update []
  (let [current @state]
  	(set! (.-innerHTML (dom/getElement "monthly-result")) (:monthly-price current))
  	(set! (.-innerHTML (dom/getElement "thirty-day-result")) (:thirty-day-price current))))

(defn listen-assoc [el key]
  (let [text (listen el "change")]
    (go 
      (while true
        (let [event (<! text)]
          (swap! state assoc key (-> event .-target .-value))
          (update))))))

(listen-assoc (dom/getElement "start-date") :start-date)
(listen-assoc (dom/getElement "thirty-day-price") :thirty-day-price)
(listen-assoc (dom/getElement "monthly-price") :monthly-price)
(init @state)
(update)