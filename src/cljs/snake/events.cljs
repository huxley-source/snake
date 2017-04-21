(ns snake.events
  (:require [re-frame.core :as rf]
            [snake.db :as db]
            [snake.utils :as utils]))


(rf/reg-event-db
  :initialize-db
  (fn [_ _]
    db/default-db))


(rf/reg-event-db
  :time/calc-elapsed                                        ; zapisywanie czasu, który upłynął.
  (fn [db _]
    (assoc db :app/time-elapsed (utils/calc-elapsed-time (:app/time-started db)))))


(rf/reg-event-fx
  :game/start
  (fn [{db :db} _]
    (let [interval (js/setInterval (fn []
                                     (rf/dispatch [:snake/move])
                                     (rf/dispatch [:time/calc-elapsed])) 150)] ; tworzymy ticker, który co 150ms przesówa węża, oraz liczy czas, który upłynął.
      {:db         (-> db (assoc :app/interval-id interval
                                 :app/state :started
                                 :app/time-started (utils/get-time))
                       (assoc-in [:score/current] 0))       ; zmieniamy stan aplikacji, zerujemy wynik oraz czas rozpoczęcia
       :dispatch-n [[:snake/spawn] [:food/spawn]]})))       ; uruchamiając grę, tworzymy węża i jedzenie.


(rf/reg-event-fx
  :game/stop
  (fn [db _]
    (js/clearInterval (get db :app/interval-id))
    (-> db
        (assoc :app/interval-id nil)
        (assoc-in [:snake/direction] [0 0])
        (update-in [:score/best] (fn [best] (max best (get-in db [:score/current])))))))


(rf/reg-event-fx
  :snake/change-direction                                   ; zmieniamy kierunek pełzania
  (fn [{:keys [db]} [_ new-direction]]                      ; event przyjmuje nowy kierunek jako argument
    (let [old-direction (get-in db [:snake/direction])
          game-state (get db :app/state)]
      {:db         (cond-> db
                           (and old-direction
                                (not= [0 0] (map + old-direction new-direction)))
                           (assoc :snake/direction new-direction)) ; jeżeli kierunek jest inny, niż poprzedni kierunek i nie jest staniem w miejscu, to zapisujemy w db
       :dispatch-n (if (#{:paused :game-over} game-state) [[:game/start]] [])}))) ; jeżeli gra była wcześniej zatrzymana, to po określenniu kierunku startujemy węża


(rf/reg-event-fx
  :snake/move
  (fn [{:keys [db]} [_]]
    (let [[rows cols] (:grid/size db)
          direction (:snake/direction db)
          snake-body (:snake/body db)
          new-head (map + (first snake-body) direction)]    ; nowa głowa = poprzednia głowa + kierunek
      {:db         (assoc-in db [:snake/body]
                             (into [new-head] (pop snake-body))) ; ciało = nowa głowe + (ciało - ogon)
       :dispatch-n [[:food/take] [:colisions/check]]})))    ; po wykonaniu ruchu sprawdzamy czy jest jedzenie/ściana na polu


(rf/reg-event-fx
  :food/take
  (fn [{db :db} _]
    (let [snake-body (:snake/body db)
          food-coords (:food/coords db)]
      (when (= (first snake-body) food-coords)              ; sprawdzamy czy współrzędne głowy = współrzędne jedzenia
        {:db       (update db :score/current inc)           ; zwiększamy wynik
         :dispatch [:snake/grow]}))))                       ; rośniemy węża


(rf/reg-event-fx
  :snake/grow
  (fn [{:keys [db]} _]
    (let [[t1 t2] (take-last 2 (get-in db [:snake/body]))   ; bierzemy [dwa ostatnie elementy ciała/ogona]
          direction (map - t1 t2)                           ; określamy kierunek w którym podąża ogon
          new-tail (map - t2 direction)]                    ; dodajemy element do ogona w przeciwnym kierunku niż kierunek.
      {:db       (update-in db [:snake/body] conj new-tail)
       :dispatch [:food/spawn]})))                          ; dodajemy jedzenie na planszy


(rf/reg-event-db
  :snake/spawn
  (fn [db _]
    (assoc-in db [:snake/body] [[16 15] [16 16]])))         ; początkwa pozycja węża


(rf/reg-event-db
  :food/spawn
  (fn [db _]
    (let [grid (:grid/size db)
          snake-body (:snake/body db)
          food-coords (->> (repeatedly #(utils/random-coords grid)) (remove #((set snake-body) %)) (first))] ; potęga leniwych struktur. pobieramy pierwsze współrzędne, które nie pokrywają się z współrzędnymi ciała węża
      (assoc db :food/coords food-coords))))


(rf/reg-event-fx
  :colisions/check
  (fn [{db :db} _]
    (let [[rows cols] (:grid/size db)
          snake-body (:snake/body db)
          [head-row head-col] (first snake-body)]
      (when (or (< head-row 0) (= head-row rows)
                (< head-col 0) (= head-col cols)
                (not (= (distinct snake-body) snake-body))) ; sprawdzamy czy głowa nie wyszła poza planszę, oraz czy nie pokryła się z ciałem.
        {:db       (assoc db :app/state :game-over)
         :dispatch [:game/stop]}))))
