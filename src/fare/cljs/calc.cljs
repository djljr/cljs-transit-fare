(ns fare.calc
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [fare.state :as s]
            [fare.date :as d]
            [goog.date :as date]
            [re-frame.core :as re]))

(defn trip-days-thirty-day [app-state]
  (let [thirty-days (->> (:start-date app-state)
                         d/date-obj
                         (iterate d/add-one)
                         (take 30)
                         last)
        take-fn (fn [d] (>= 0 (.compare date/Date d thirty-days)))]
    (->> (:start-date app-state)
         d/date-obj
         d/days
         d/trip-days
         (take-while take-fn))))

(defn last-day-thirty-day [app-state]
  (-> (trip-days-thirty-day app-state)
      last
      d/date-string))

(defn trips-thirty-day [app-state]
  (* 2 (count (trip-days-thirty-day app-state))))

(defn trip-days-forty-trip [app-state]
  (let [minus-40 (fn [x] (- 40 x))]
    (-> (:extra-trips app-state)
        (js/parseInt)
        minus-40
        (/ 2))))

(defn last-day-forty-trip [app-state]
  (-> (trip-days-forty-trip app-state)
        (take (d/trip-days (d/days (d/date-obj (:start-date app-state)))))
        last
        d/date-string))

(re/register-sub
 :last-day-forty-trip
 (fn [app-state _]
   (reaction (last-day-forty-trip @app-state))))

(re/register-sub
 :last-day-thirty-day
 (fn [app-state _]
   (reaction (last-day-thirty-day @app-state))))

(re/register-sub
 :thirty-day-result
 (fn [app-state _]
   (reaction
    (-> (:thirty-day-price @app-state)
        (/ 30)
        (.toFixed 2)))))

(re/register-sub
 :forty-trip-result
 (fn [app-state _]
   (reaction         
    (-> (:forty-trip-price @app-state)
        (/ (d/days-between
            (:start-date @app-state)
            (last-day-forty-trip @app-state)))
        (.toFixed 2)))))

(re/register-sub
 :thirty-day-result-trip
 (fn [app-state _]
   (reaction
    (-> (:thirty-day-price @app-state)
        (/ (trips-thirty-day @app-state))
        (.toFixed 2)))))

(re/register-sub
 :forty-trip-result-trip
 (fn [app-state _]
   (reaction
    (-> (:forty-trip-price @app-state)
        (/ 40)
        (.toFixed 2)))))
