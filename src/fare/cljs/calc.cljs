(ns fare.calc
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [fare.state :as s]
            [fare.date :as d]
            [goog.date :as date]
            [re-frame.core :as re]))

(def trip-days-thirty-day
  (let [app-state (re/subscribe [:field-values])]
    (reaction
     (let [thirty-days (->> (:start-date @app-state)
                            d/date-obj
                            (iterate d/add-one)
                            (take 30)
                            last)
           take-fn (fn [d] (>= 0 (.compare date/Date d thirty-days)))]
       (->> (:start-date @s/app-state)
            d/date-obj
            d/days
            d/trip-days
            (take-while take-fn))))))

(def last-day-thirty-day
  (reaction   
   (-> @trip-days-thirty-day
       last
       d/date-string)))

(def trips-thirty-day
  (reaction (* 2 (count @trip-days-thirty-day))))

(def trip-days-forty-trip
  (let [app-state (re/subscribe [:field-values])]
    (reaction
     (let [minus-40 (fn [x] (- 40 x))]
       (-> (:extra-trips @app-state)
           (js/parseInt)
           minus-40
           (/ 2))))))

(def last-day-forty-trip
  (let [app-state (re/subscribe [:field-values])]
    (reaction
     (-> @trip-days-forty-trip
         (take (d/trip-days (d/days (d/date-obj (:start-date @app-state)))))
         last
         d/date-string))))

(def thirty-day-result
  (let [app-state (re/subscribe [:field-values])]
    (reaction
     (-> (:thirty-day-price @app-state)
         (/ 30)
         (.toFixed 2)))))

(def forty-trip-result
  (let [app-state (re/subscribe [:field-values])]
    (reaction         
     (-> (:forty-trip-price @app-state)
         (/ (d/days-between (:start-date @app-state) @last-day-forty-trip))
         (.toFixed 2)))))

(def thirty-day-result-trip
  (let [app-state (re/subscribe [:field-values])]
    (reaction
     (-> (:thirty-day-price @app-state)
         (/ @trips-thirty-day)
         (.toFixed 2)))))

(def forty-trip-result-trip
  (let [app-state (re/subscribe [:field-values])]
    (reaction
     (-> (:forty-trip-price @app-state)
         (/ 40)
         (.toFixed 2)))))
