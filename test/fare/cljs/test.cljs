(ns fare.core.test
  (:require-macros [cemerick.cljs.test :refer (deftest are is testing)])
  (:require [cemerick.cljs.test :as t]
            [goog.date :as date]
            [fare.core :refer [is-weekday?]]))

(deftest is-weekday-test
  (testing "djljr.fare.core.is-weekday?"
    (testing "well known weekdays"
      (are [expected actual] (= expected actual)
           true (is-weekday? (date/Date. 2014 0 6))
           true (is-weekday? (date/Date. 2014 0 3))))
    (testing "some weekends"
      (are [expected actual] (= expected actual)
           false (is-weekday? (date/Date. 2014 0 4))
           false (is-weekday? (date/Date. 2014 0 5))))))

(deftest trivial
	(is (= 1 1)))
