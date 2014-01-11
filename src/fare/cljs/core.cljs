(ns djljr.fare.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:refer-clojure :exclude [format])
  (:require [goog.dom :as dom]
            [goog.events :as events]
            [goog.date :as date]
            [goog.net.cookies :as cks]
            [goog.i18n.DateTimeFormat]
            [goog.i18n.DateTimeParse]
            [cljs.core.async :refer [put! chan <!]]
            [cljs.reader :as reader]
            [clojure.string :refer [join]]))

(defn date-string [d]
  (.format (goog.i18n.DateTimeFormat. "yyyy-MM-dd") d))

(defn now-string [] (date-string (date/Date.)))

(defn date-obj [date-string]
  (let [d (date/Date.)]
    (.strictParse (goog.i18n.DateTimeParse. "yyyy-MM-dd") date-string, d)
    d))

(def state (atom {:forty-trip-price 76.00 
                  :thirty-day-price 80.00 
                  :start-date (now-string) 
                  :holiday #{"2014-01-01" "2014-01-20" "2014-05-26" "2014-07-04" "2014-09-01" "2014-11-27" "2014-11-28" "2014-12-24" "2014-12-25" "2014-12-26"} 
                  :vacation #{} 
                  :travel-days #{}}))

(defn load []
  (let [state-string (.get goog.net.cookies "state")]
  	(if (undefined? state-string) 
     (reset! state @state)
     (reset! state (reader/read-string state-string)))))

(defn save [st]
  (let [state-string (pr-str st)]
    (.set goog.net.cookies "state" state-string)))

(defn is-weekday? [d]
  (not (contains? #{5 6} (.getIsoWeekday d))))

(defn is-not-holiday? [d]
  (not (contains? (:holiday @state) (date-string d))))

(defn is-not-vacation? [d]
  (not (contains? (:vacation @state) (date-string d))))

(defn is-workday? [d]
  (and (is-weekday? d) (is-not-holiday? d) (is-not-vacation? d)))

(defn is-travel-day? [d]
  (contains? (:travel-days @state) (date-string d)))

(defn is-trip-day? [d]
  (or (is-workday? d) (is-travel-day? d)))

(defn trip-days [days]
  (filter is-trip-day? days))

(def one-day (date/Interval. date/Interval.DAYS 1))

(defn add-one [d]
  (let [d-clone (.clone d)]
    (.add d-clone one-day)
    d-clone))

(defn days [start]
  (iterate add-one start))

(defn days-between [start end]
  (let [start-date (date-obj start) 
        end-date (date-obj end)
        before (fn [d] (< (date/Date.compare d end-date) 0))
        days-bet (take-while before (days start-date))]
    (count days-bet)))

(declare update)

(defn listen [el type]
  (let [out (chan)]
    (events/listen el type
                   (fn [e] (put! out e)))
    out))

(defn listen-assoc [el key]
  (let [text (listen el "change")]
    (go 
      (while true
        (let [event (<! text)]
          (swap! state assoc key (-> event .-target .-value))
          (update))))))

(defn append [st key value]
  (assoc st key (conj (get st key) value)))

(defn disjunct [st key value]
  (assoc st key (disj (get st key) value)))

(defn listen-append [btn el key]
  (let [add-btn (listen btn "click")]
    (go
      (while true
        (let [event (<! add-btn)]
          (swap! state append key (-> el .-value))
          (update))))))

(defn set-remove [li key]
  (let [chan (listen li "click")]
    (go
      (while true
      	(let [event (<! chan)]
         (swap! state disjunct key (-> li .-innerHTML))
         (update))))))

(defn make-li [value]
  (let [item (dom/createDom "span" (clj->js {"class" "label label-default"}))]
    (set! (.-innerHTML item) value)
    item))

(defn display-set [key s display]
  (let [lis (into-array (map make-li (sort s)))]
  	(doseq [li lis]
  		(set-remove li key))
  	(dom/removeChildren display)
  	(dom/replaceNode (dom/createDom "div" (clj->js {"class" "input-group col-sm-7" "id" (str key "-days")}) lis) display)))

(defn update []
  (let [current @state
        last-day-forty-trip (date-string (last (take 20 (trip-days (days (date-obj (:start-date current)))))))]
  	(set! (.-innerHTML (dom/getElement "thirty-day-result")) (.toFixed (/ (:thirty-day-price current) 30) 2))
  	(set! (.-innerHTML (dom/getElement "forty-trip-result")) (.toFixed (/ (:forty-trip-price current) (days-between (:start-date current) last-day-forty-trip)) 2))
    (display-set :vacation (get current :vacation) (dom/getElement ":vacation-days"))
    (display-set :holiday (get current :holiday) (dom/getElement ":holiday-days"))
    (save current)))

(defn init []
  (let [current @state]
    (set! (.-value (dom/getElement "start-date")) (:start-date current))
    (set! (.-value (dom/getElement "thirty-day-price")) (:thirty-day-price current))
    (set! (.-value (dom/getElement "forty-trip-price")) (:forty-trip-price current))
    (update)))



(listen-assoc (dom/getElement "start-date") :start-date)
(listen-assoc (dom/getElement "thirty-day-price") :thirty-day-price)
(listen-assoc (dom/getElement "forty-trip-price") :forty-trip-price)
(listen-append (dom/getElement "add-vacation-day") (dom/getElement "vacation-day") :vacation)
(listen-append (dom/getElement "add-holiday-day") (dom/getElement "holiday-day") :holiday)
(load)
(init)
