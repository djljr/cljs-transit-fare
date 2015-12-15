(ns fare.core
  (:require-macros [reagent.ratom :refer [reaction]])
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

(re/register-handler
 :field-change
 re/debug
 (fn [db [_ field value]]
   (assoc db field value)))

(re/register-handler
 :add-day
 (fn [db [_ field day]]
   (update db field conj day)))

(re/register-sub
 :field-values
 (fn [db _]
   (reaction @db)))

(defn- get-event-value
  "Given a DOM event, return the value it yields. This abstracts over
  the needless inconsistencies of the DOM."
  [event]
  (let [target (.-target event)
        type (.-type target)]
    (condp contains? type
      #{"checkbox"}
      (.-checked target)

      #{"text" "email" "password" "number" "radio" "textarea" "select-one" "select-multiple" "date"}
      (.-value target))))

(defn input [{:keys [pre label id type value post read-only?] :as opts}]
  [:div.form-group
   [:label.col-sm-4.control-label {:for id} (str label ":")]
   [:div.input-group.col-sm-7
    (if pre [:span.input-group-addon pre])
    (if read-only?
      [:p.form-control-static value]
      [:input.form-control {:id id :type type :value value
                            :on-change (fn [e]
                                         (re/dispatch
                                          [:field-change
                                           (keyword id)
                                           (get-event-value e)]))}])
    (if post [:span.input-group-addon post])]])

(defn root []
  (let [app-state (re/subscribe [:field-values])]
    (fn []
      [:div.col-md-6 {:role "main"}
       [:h1 "Transit Pass Calculator"]
       [:div.inputs
        [:div.form-horizontal
         (input {:label "Start Date" :id "start-date"
                 :type "date" :value (:start-date @app-state)})
         (input {:label "30 Day Price" :id "thirty-day-price" :pre "$"
                 :type "number" :value (:thirty-day-price @app-state)})
         (input {:label "40 Trip Price" :id "forty-trip-price" :pre "$"
                 :type "number" :value (:forty-trip-price @app-state)})
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
                 :value @c/last-day-forty-trip :read-only? true})]]])))

(defn mount-root []
  (re/dispatch-sync [:initialize])
  (reagent/render-component
   [root]
   (. js/document (getElementById "app"))))

(mount-root)
