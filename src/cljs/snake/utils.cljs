(ns snake.utils)

(def key-code->move
  {37 [0 -1]
   38 [-1 0]
   39 [0 1]
   40 [1 0]})

(defn random-coords
  [[rows cols]]
  [(rand-int rows) (rand-int cols)])

(defn get-time
  []
  (let [millis (.getTime (js/Date.))]
    (/ millis 1000)))

(defn calc-elapsed-time
  [time-started]
  (int (- (get-time) time-started)))
