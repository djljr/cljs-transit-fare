(ns djljr.fare.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [goog.dom :as dom]
            [goog.events :as events]
            [goog.date :as date]
            [cljs.core.async :refer [put! chan <!]]))

(defn is-weekday? [d]
  (not (contains? #{5 6} (.getIsoWeekday d))))

