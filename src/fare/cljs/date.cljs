(ns fare.date
  (:require [goog.date :as date]
            [goog.i18n.DateTimeParse]
            [goog.i18n.DateTimeFormat]))

(defn date-string [d]
  (if d (.format (goog.i18n.DateTimeFormat. "yyyy-MM-dd") d)))

(defn now-string [] (date-string (date/Date.)))

(defn date-obj [date-string]
  (if date-string
    (let [d (date/Date.)]
      (.strictParse (goog.i18n.DateTimeParse. "yyyy-MM-dd") date-string d)
      d)))


(defn is-weekday? [d]
  (println "is-weekday" d)
  (not (contains? #{5 6} (.getIsoWeekday d))))

(defn is-not-holiday? [d state]
  (not (contains? (:holiday-list state) (date-string d))))

(defn is-not-vacation? [d state]
  (not (contains? (:vacation-list state) (date-string d))))

(defn is-workday? [d state]
  (and (is-weekday? d)
       (is-not-holiday? d state)
       (is-not-vacation? d state)))

(defn is-trip-day-filter? [state]
  (fn [day] (is-workday? day state)))

(defn trip-days [state days]
  (filter (is-trip-day-filter? state) days))

(def one-day (date/Interval. date/Interval.DAYS 1))

(defn add-one [d]
  (let [d-clone (.clone d)]
    (.add d-clone one-day)
    d-clone))

(defn days [start]
  (iterate add-one start))

(defn days-between [start end]
  (if (and start end)
    (let [start-date (date-obj start) 
          end-date (date-obj end)
          before (fn [d] (< (date/Date.compare d end-date) 0))
          days-bet (take-while before (days start-date))]
      (count days-bet))
    0))
