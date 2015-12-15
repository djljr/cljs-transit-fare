(ns fare.calc
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [fare.state :as s]
            [fare.date :as d]
            [goog.date :as date]))

(def trip-days-thirty-day
  (reaction
   (let [thirty-days (->> (:start-date @s/app-state)
                          d/date-obj
                          (iterate d/add-one)
                          (take 30)
                          last)
         take-fn (fn [d] (>= 0 (.compare date/Date d thirty-days)))]
     (->> (:start-date @s/app-state)
          d/date-obj
          d/days
          d/trip-days
          (take-while take-fn)))))

(def last-day-thirty-day
  (reaction   
   (-> @trip-days-thirty-day
       last
       d/date-string)))

(def trips-thirty-day
  (reaction (* 2 (count @trip-days-thirty-day))))

(def trip-days-forty-trip
  (reaction
   (let [minus-40 (fn [x] (- 40 x))]
     (-> (:extra-trips @s/app-state)
         (js/parseInt)
         minus-40
         (/ 2)))))

(def last-day-forty-trip
  (reaction
   (-> @trip-days-forty-trip
       (take (d/trip-days (d/days (d/date-obj (:start-date @s/app-state)))))
       last
       d/date-string)))

(def thirty-day-result
  (reaction
   (-> (:thirty-day-price @s/app-state)
       (/ 30)
       (.toFixed 2))))

(def forty-trip-result
  (reaction         
   (-> (:forty-trip-price @s/app-state)
       (/ (d/days-between (:start-date @s/app-state) @last-day-forty-trip))
       (.toFixed 2))))

(def thirty-day-result-trip
  (reaction
   (-> (:thirty-day-price @s/app-state)
       (/ @trips-thirty-day)
       (.toFixed 2))))

(def forty-trip-result-trip
  (reaction
   (-> (:forty-trip-price @s/app-state)
       (/ 40)
       (.toFixed 2))))
