(ns djljr.fare.core.test
  (:require-macros [cemerick.cljs.test :refer (deftest is)])
  (:require [cemerick.cljs.test :as t]
            [djljr.fare.core :refer [is-weekday?]]))

(deftest trivial
	(is (= 1 1)))