(ns snake.subs
  (:require [re-frame.core :as rf]
            [snake.utils :as utils]))


(rf/reg-sub
  :grid/rows
  (fn [db _]
    (get-in db [:grid/size 0])))


(rf/reg-sub
  :grid/cols
  (fn [db _]
    (get-in db [:grid/size 1])))

(rf/reg-sub
  :app/state
  (fn [db _]
    (get db :app/state)))

(rf/reg-sub
  :snake/body
  (fn [db _]
    (get-in db [:snake/body])))

(rf/reg-sub
  :food/coords
  (fn [db _]
    (get db :food/coords)))

(rf/reg-sub
  :app/time-elapsed
  (fn [db _]
    (get db :app/time-elapsed)))

(rf/reg-sub
  :score/current
  (fn [db _]
    (get-in db [:score/current])))

(rf/reg-sub
  :score/best
  (fn [db _]
    (get-in db [:score/best])))