(ns fare.core
  (:require [fare.date :as d]
            [fare.state :as s]
            [fare.calc :as c]
            [reagent.core :as reagent]
            [re-frame.core :as re]
            [goog.date :as date]))

(enable-console-print!)

(re/register-handler
 :initialize
 re/debug
 (fn [db _]
   (merge db s/initial-state)))

(defn input [{:keys [pre label id type value post read-only?] :as opts}]
  [:div.form-group
   [:label.col-sm-4.control-label {:for id} (str label ":")]
   [:div.input-group.col-sm-7
    (if pre [:span.input-group-addon pre])
    (if read-only?
      [:p.form-control-static value]
      [:input.form-control {:id id :type type :value value}])
    (if post [:span.input-group-addon post])]])

(defn root []
  [:div.col-md-6 {:role "main"}
   [:h1 "Transit Pass Calculator"]
   [:div.inputs
    [:div.form-horizontal
     (input {:label "Start Date" :id "start-date"
             :type "date" :value (:start-date @s/app-state)})
     (input {:label "30 Day Price" :id "thirty-day-price" :pre "$"
             :type "number" :value (:thirty-day-price @s/app-state)})
     (input {:label "40 Trip Price" :id "forty-trip-price" :pre "$"
             :type "number" :value (:forty-trip-price @s/app-state)})
     (input {:label "Vacation" :id "vacation"
             :type "date" :post [:button {:type "button"} "Add"]})
     [:div.input-group.col-sm-7
      [:span.label.label-default "2014-01-01"]]
     (input {:label "Holidays" :id "holiday"
             :type "date" :post [:button {:type "button"} "Add"]})
     [:div.input-group.col-sm-7
      [:span.label.label-default "2014-01-01"]]
     (input {:label "Additional Trips" :id "extra-trips"
             :type "number" :value (:additional-trips @s/app-state)})]]
   [:div.output
    [:div.form-horizontal
     (input {:label "$/Day 30 Day" :pre "$"
             :value @c/thirty-day-result :read-only? true})
     (input {:label "$/Day 40 Trip" :pre "$"
             :value @c/forty-trip-result :read-only? true})
     (input {:label "$/Trip 30 Day" :pre "$"
             :value @c/thirty-day-result-trip :read-only? true})
     (input {:label "$/Trip 40 Trip" :pre "$"
             :value @c/forty-trip-result-trip :read-only? true})
     (input {:label "Last Day 30 Day"
             :value @c/last-day-thirty-day :read-only? true})
     (input {:label "Last Day 40 Trip"
             :value @c/last-day-forty-trip :read-only? true})]]])

(defn mount-root []
  (re/dispatch-sync [:initialize])
  (reagent/render-component
   [root]
   (. js/document (getElementById "app"))))

(mount-root)
