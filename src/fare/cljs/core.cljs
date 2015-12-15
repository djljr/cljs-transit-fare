(ns fare.core
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [reagent.ratom :refer [reaction]])
  (:refer-clojure :exclude [format])
  (:require [reagent.core :as reagent :refer [atom]]
            [goog.dom :as dom]
            [goog.events :as events]
            [goog.date :as date]
            [goog.net.cookies :as cks]
            [goog.i18n.DateTimeFormat]
            [goog.i18n.DateTimeParse]
            [cljs.core.async :refer [put! chan <!]]
            [cljs.reader :as reader]
            [clojure.string :refer [join]]))

(def app-state (atom {:start-date "2015-12-01"
                      :thirty-day-price "83"
                      :forty-trip-price "90"}))

(def thirty-day-result (reaction (:thirty-day-price @app-state)))
(def forty-trip-result (reaction (:forty-trip-price @app-state)))
(def thirty-day-result-trip (reaction (:thirty-day-price @app-state)))
(def forty-trip-result-trip (reaction (:forty-trip-price @app-state)))
(def last-day-thirty-day (reaction (:start-date @app-state)))
(def last-day-forty-trip (reaction (:start-date @app-state)))

(defn root []
  [:div.col-md-6 {:role "main"}
   [:h1 "Transit Pass Calculator"]
   [:div.inputs
    [:div.form-horizontal
     [:div.form-group
      [:label.col-sm-4.control-label {:for "start-date"} "Start Date:"]
      [:div.input-group.col-sm-7
       [:input.form-control {:type "date" :value (:start-date @app-state)}]]]
     [:div.form-group
      [:label.col-sm-4.control-label {:for "thirty-day-price"} "30 Day Price:"]
      [:div.input-group.col-sm-7
       [:span.input-group-addon "$"]
       [:input.form-control {:type "number" :value (:thirty-day-price @app-state)}]]]
     [:div.form-group
      [:label.col-sm-4.control-label {:for "forty-trip-price"} "40 Trip Price:"]
      [:div.input-group.col-sm-7
       [:span.input-group-addon "$"]
       [:input.form-control {:type "number" :value (:forty-trip-price @app-state)}]]]
     [:div.form-group
      [:label.col-sm-4.control-label {:for "vacation"} "Vacation:"]
      [:div.input-group.col-sm-7
       [:input.form-control {:type "date"}]
       [:span.input-group-addon
        [:button {:type "button"} "Add"]]]
      [:div.input-group.col-sm-7]]
     [:div.form-group
      [:label.col-sm-4.control-label {:for "holiday"} "Holidays:"]
      [:div.input-group.col-sm-7
       [:input.form-control {:type "date"}]
       [:span.input-group-addon
        [:button {:type "button"} "Add"]]]
      [:div.input-group.col-sm-7
       [:span.label.label-default "2014-01-01"]]]
     [:div.form-group
      [:label.col-sm-4.control-label {:for "extra-trips"} "Additional Trips:"]
      [:div.input-group.col-sm-7
       [:input.form-control {:type "number" :value (:additional-trips @app-state)}]]]]]
   [:div.output
    [:div.form-horizontal
     [:div.form-group
      [:label.col-sm-4.control-label "$/Day 30 Day"]
      [:div.input-group.col-sm-7
       [:span.input-group-addon "$"]
       [:p.form-control-static @thirty-day-result]]]
     [:div.form-group
      [:label.col-sm-4.control-label "$/Day 40 Trip"]
      [:div.input-group.col-sm-7
       [:span.input-group-addon "$"]
       [:p.form-control-static @forty-trip-result]]]
     [:div.form-group
      [:label.col-sm-4.control-label "$/Trip 30 Day"]
      [:div.input-group.col-sm-7
       [:span.input-group-addon "$"]
       [:p.form-control-static @thirty-day-result-trip]]]
     [:div.form-group
      [:label.col-sm-4.control-label "$/Trip 40 Trip"]
      [:div.input-group.col-sm-7
       [:span.input-group-addon "$"]
       [:p.form-control-static @forty-trip-result-trip]]]
     [:div.form-group
      [:label.col-sm-4.control-label "Last Day 30 Day"]
      [:div.input-group.col-sm-7
       [:p.form-control-static @last-day-thirty-day]]]
     [:div.form-group
      [:label.col-sm-4.control-label "Last Day 40 Trip"]
      [:div.input-group.col-sm-7
       [:p.form-control-static @last-day-forty-trip]]]]]])

(defn mount-root []
  (reagent/render-component
   [root]
   (. js/document (getElementById "app")))  )

(mount-root)


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
                  :travel-days #{}
                  :extra-trips 0}))

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

(defn update-fn []
  (let [current @state
        trip-days-forty-trip (/ (- 40 (js/parseInt (:extra-trips current))) 2)
        last-day-forty-trip (date-string (last (take trip-days-forty-trip (trip-days (days (date-obj (:start-date current)))))))
        thirty-days (last (take 30 (iterate add-one (date-obj (:start-date current)))))
        trip-days-thirty-day (take-while #(>= 0 (.compare date/Date % thirty-days)) (trip-days (days (date-obj (:start-date current)))))
        trips-thirty-day (* 2 (count trip-days-thirty-day))]
    {:thirty-day-result (.toFixed (/ (:thirty-day-price current) 30) 2)
     :forty-trip-result (.toFixed (/ (:forty-trip-price current) (days-between (:start-date current) last-day-forty-trip)) 2)
     :thirty-day-result-trip (.toFixed (/ (:thirty-day-price current) trips-thirty-day) 2)
     :forty-trip-result-trip (.toFixed (/ (:forty-trip-price current) 40) 2)
     :last-day-forty-trip last-day-forty-trip
     :last-day-thirty-trip (date-string (last trip-days-thirty-day))}
    (save current)))
