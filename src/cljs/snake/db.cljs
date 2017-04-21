(ns snake.db)

(def default-db
  {:app/interval-id  nil
   :app/state        :paused
   :app/time-started nil
   :app/time-elapsed nil
   :grid/size        [32 32]
   :food/coords      nil
   :score/best       0
   :score/current    0
   :snake/direction  []
   :snake/boody      []})
