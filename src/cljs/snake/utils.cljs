(ns snake.utils)

(def key-code->move                                         ;przekształcenie kodu klawisza na wektor []
  {37 [0 -1]                                                ;strzałka v
   38 [-1 0]                                                ;strzałka <
   39 [0 1]                                                 ;strzałka ^
   40 [1 0]})                                               ;strzałka >

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
