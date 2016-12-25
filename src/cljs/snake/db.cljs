(ns snake.db)

(def default-db
  {:name             "snake"
   :move-interval-id nil
   :game-state       :paused
   :snake            {:block     false
                      :direction []
                      :body      []}
   :grid             [32 32]
   :food-coords      nil
   :score            {:best    0
                      :current 0}
   :time-started     0})
