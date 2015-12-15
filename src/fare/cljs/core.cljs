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
 (fn [db _]
   (merge db s/initial-state)))

(re/register-handler
 :field-change
 (fn [db [_ field value]]
   (assoc db field value)))

(re/register-handler
 :add-day
 (fn [db [_ field day]]
   (update db field conj day)))

(re/register-handler
 :remove-day
 (fn [db [_ field day]]
   (update db field disj day)))

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

(defn input [{:keys [pre label id type value post read-only? placeholder] :as opts}]
  [:div.form-group
   [:label.col-sm-4.control-label {:for id} (str label ":")]
   [:div.input-group.col-sm-7
    (if pre [:span.input-group-addon pre])
    (if read-only?
      [:p.form-control-static value]
      [:input.form-control {:id id :type type :value value :placeholder placeholder
                            :on-change (fn [e]
                                         (re/dispatch
                                          [:field-change
                                           (keyword id)
                                           (get-event-value e)]))}])
    (if post [:span.input-group-addon post])]])

(defn date-pill [field]
  (fn [day]
    ^{:key [field day]}
    [:span.label.label-default
     {:on-click (fn [e] (re/dispatch [:remove-day field day]))}
     day]))

(defn root []
  (let [app-state (re/subscribe [:field-values])
        thirty-day-result (re/subscribe [:thirty-day-result])
        forty-trip-result (re/subscribe [:forty-trip-result])
        thirty-day-result-trip (re/subscribe [:thirty-day-result-trip])
        forty-trip-result-trip (re/subscribe [:forty-trip-result-trip])
        last-day-thirty-day (re/subscribe [:last-day-thirty-day])
        last-day-forty-trip (re/subscribe [:last-day-forty-trip])]
    (fn []
      [:div.col-md-6 {:role "main"}
       [:h1 "Transit Pass Calculator"]
       [:div.inputs
        [:div.form-horizontal
         (input {:label "Start Date" :id "start-date" :placeholder "yyyy-mm-dd"
                 :value (:start-date @app-state)})
         (input {:label "30 Day Price" :id "thirty-day-price" :pre "$"
                 :type "number" :value (:thirty-day-price @app-state)})
         (input {:label "40 Trip Price" :id "forty-trip-price" :pre "$"
                 :type "number" :value (:forty-trip-price @app-state)})
         (input {:label "Vacation" :id "vacation" :placeholder "yyyy-mm-dd"
                 :value (:vacation @app-state)
                 :post [:button {:on-click
                                 (fn [e]
                                   (re/dispatch
                                    [:add-day :vacation-list (:vacation @app-state)]))
                                 :type "button"} "Add"]})
         [:div.form-group
          [:div.col-sm-4]
          [:div.input-group.col-sm-7
           (map (date-pill :vacation-list)
                (sort (:vacation-list @app-state)))]]
         (input {:label "Holidays" :id "holiday" :placeholder "yyyy-mm-dd"
                 :value (:holiday @app-state)
                 :post [:button {:on-click
                                 (fn [e]
                                   (re/dispatch
                                    [:add-day :holiday-list (:holiday @app-state)]))
                                 :type "button"} "Add"]})
         [:div.form-group
          [:div.col-sm-4]
          [:div.input-group.col-sm-7
           (map (date-pill :holiday-list)
                (sort (:holiday-list @app-state)))]]
         (input {:label "Additional Trips" :id "extra-trips"
                 :type "number" :value (:extra-trips @app-state)})]]
       [:div.output
        [:div.form-horizontal
         (input {:label "$/Day 30 Day" :pre "$"
                 :value @thirty-day-result :read-only? true})
         (input {:label "$/Day 40 Trip" :pre "$"
                 :value @forty-trip-result :read-only? true})
         (input {:label "$/Trip 30 Day" :pre "$"
                 :value @thirty-day-result-trip :read-only? true})
         (input {:label "$/Trip 40 Trip" :pre "$"
                 :value @forty-trip-result-trip :read-only? true})
         (input {:label "Last Day 30 Day"
                 :value @last-day-thirty-day :read-only? true})
         (input {:label "Last Day 40 Trip"
                 :value @last-day-forty-trip :read-only? true})]]])))

(defn mount-root []
  (re/dispatch-sync [:initialize])
  (reagent/render-component
   [root]
   (. js/document (getElementById "app"))))

(mount-root)
