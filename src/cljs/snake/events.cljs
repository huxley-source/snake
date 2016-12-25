(ns snake.events
  (:require [re-frame.core :as rf]
            [snake.db :as db]
            [snake.utils :as utils]))


(rf/reg-event-db
  :initialize-db
  (fn [_ _]
    db/default-db))



(rf/reg-event-fx
  :start-game
  (fn [{:keys [db]} _]
    {:db         (-> db
                     (assoc :move-interval-id (js/setInterval #(rf/dispatch [:move-snake]) 150)
                            :game-state :started
                            :time-started (utils/get-time))
                     (assoc-in [:score :current] 0))
     :dispatch-n [[:spawn-snake] [:spawn-food]]}))

(rf/reg-event-fx
  :stop-game
  (fn [{:keys [db]} _]
    (let [score (get-in db [:score :current])]
      (js/clearInterval (get db :move-interval-id))
      {:db (-> db
               (assoc :interval-id nil)
               (assoc-in [:snake :direction] [0 0])
               (update-in [:score :best] (fn [best] (max best score))))})))

(rf/reg-event-db
  :block-direction-change
  (fn [db _]
    (assoc-in db [:snake :block] true)))

(rf/reg-event-db
  :unblock-direction-change
  (fn [db _]
    (assoc-in db [:snake :block] false)))

(rf/reg-event-fx
  :change-direction
  (fn [{:keys [db]} [_ new-direction]]
    (let [old-direction (get-in db [:snake :direction])
          block? (get-in db [:snake :block])
          game-state (get db :game-state)
          base-cofx {:db         db
                     :dispatch-n [[:block-direction-change]]}]
      (cond-> base-cofx
              (and old-direction
                   (not block?)
                   (not= [0 0] (map + old-direction new-direction)))
              (assoc-in [:db :snake :direction] new-direction)
              (#{:paused :game-over} game-state)
              (update :dispatch-n conj [:start-game])))))

(rf/reg-event-fx
  :move-snake
  (fn [{:keys [db]} [_]]
    (let [[rows cols] (get db :grid)
          direction (get-in db [:snake :direction])
          snake-body (get-in db [:snake :body])
          new-head (map + (first snake-body) direction)
          game-state (get db :game-state)]
      {:db         (assoc-in db [:snake :body]
                             (into [new-head] (pop snake-body)))
       :dispatch-n [[:unblock-direction-change] [:take-food] [:check-collisions]]})))

(rf/reg-event-fx
  :take-food
  (fn [{:keys [db]} _]
    (let [snake-body (get-in db [:snake :body])
          food-coords (get db :food-coords)]
      (if (= (first snake-body) food-coords)
        {:db       (update-in db [:score :current] inc)
         :dispatch [:grow-snake]}
        {:db db}))))

(rf/reg-event-fx
  :grow-snake
  (fn [{:keys [db]} _]
    (let [[t1 t2] (take-last 2 (get-in db [:snake :body]))
          direction (map - t1 t2)
          new-tail (map - t2 direction)]
      {:db       (update-in db [:snake :body] conj new-tail)
       :dispatch [:spawn-food]})))

(rf/reg-event-fx
  :spawn-snake
  (fn [{:keys [db]} _]
    {:db (assoc-in db [:snake :body] [[16 15] [16 16]])}))

(rf/reg-event-db
  :spawn-food
  (fn [db _]
    (let [grid (get db :grid)
          snake-body (get-in db [:snake :body])
          food-coords (->> (repeatedly #(utils/random-coords grid))
                           (remove #((set snake-body) %))
                           (first))]
      (assoc db :food-coords food-coords))))

(rf/reg-event-fx
  :check-collisions
  (fn [{:keys [db]} _]
    (let [[rows cols] (get db :grid)
          snake-body (get-in db [:snake :body])
          [head-row head-col] (first snake-body)]
      ;(println :snake-body snake-body
      ;         :distinct (distinct? snake-body))
      (if (or (< head-row 0) (>= head-row rows)
              (< head-col 0) (>= head-col cols)
              (not (= (distinct snake-body) snake-body)))
        {:db       (assoc db :game-state :game-over)
         :dispatch [:stop-game]}
        {:db db}))))
