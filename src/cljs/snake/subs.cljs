(ns snake.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as rf]
            [snake.utils :as utils]))

(rf/reg-sub
  :name
  (fn [db]
    (:name db)))


(rf/reg-sub
  :grid-rows
  (fn [db _]
    (get-in db [:grid 0])))


(rf/reg-sub
  :grid-cols
  (fn [db _]
    (get-in db [:grid 1])))

(rf/reg-sub
  :game-state
  (fn [db _]
    (get db :game-state)))

(rf/reg-sub
  :snake-body
  (fn [db _]
    (get-in db [:snake :body])))

(rf/reg-sub
  :food-coords
  (fn [db _]
    (get db :food-coords)))

(rf/reg-sub
  :time-elapsed
  (fn [db _]
    (utils/calc-elapsed-time (get db :time-started))))

(rf/reg-sub
  :score-current
  (fn [db _]
    (get-in db [:score :current])))

(rf/reg-sub
  :score-best
  (fn [db _]
    (get-in db [:score :best])))