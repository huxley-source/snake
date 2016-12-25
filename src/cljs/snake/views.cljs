(ns snake.views
  (:require [goog.string :as gstring]
            [goog.string.format]
            [re-frame.core :as rf]
            [reagent.ratom :refer [reaction]]))

(defn title
  []
  (let [name (rf/subscribe [:name])]
    (fn []
      [:h1 @name])))


(defn cell-snake-body
  []
  (fn []
    [:div.grid-cell.snake-body]))


(defn cell-food
  []
  (fn []
    [:div.grid-cell.food]))


(defn grid-cell
  []
  (fn []
    [:div.grid-cell]))


(defn grid []
  (let [rows (rf/subscribe [:grid-rows])
        cols (rf/subscribe [:grid-cols])
        game-state (rf/subscribe [:game-state])
        grid-class (reaction (case @game-state
                               :paused "grid-game-paused"
                               :game-over "grid-game-game-over"
                               :victory "grid-game-victory"
                               :started "grid-game-started"))
        snake-body (rf/subscribe [:snake-body])
        food-coords (rf/subscribe [:food-coords])]
    (fn []
      [:div {:class @grid-class}
       [:div.col-xs-12
        (doall
          (for [row (range @rows)]
            ^{:key (str "row_" (inc row))}
            [:div.row
             (doall
               (for [col (range @cols)]
                 (cond
                   (contains? (set @snake-body) [row col])
                   ^{:key (str "cell_" (* (inc row) (inc col)))}
                   [cell-snake-body]
                   (= @food-coords [row col])
                   ^{:key (str "cell_" (* (inc row) (inc col)))}
                   [cell-food]
                   :else
                   ^{:key (str "cell_" (* (inc row) (inc col)))}
                   [grid-cell])))]))]])))


(defn main-page []
  (let [time (rf/subscribe [:time-elapsed])
        best (rf/subscribe [:score-best])
        score (rf/subscribe [:score-current])]
    (fn []
      [:div.container-fluid {:onKeyPress      #(.preventDefault %)
                             :on-context-menu #(.preventDefault %)}
       [:div.row.center-xs
        [:div.col-xs-12
         [title]]]
       [:div.row.center-xs
        [:div.col-xs-2
         (str "time: " (gstring/format "%03d" (or @time 0)))]
        [:div.col-xs-2
         (str "best: " (gstring/format "%03d" (or @best 0)))]
        [:div.col-xs-2
         (str "score: " (gstring/format "%03d" (or @score 0)))]]
       [:div.row.center-xs {:style {:margin-top "25px"}}
        [grid]]])))
