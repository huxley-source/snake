(ns snake.playground
  (:require [re-frame.core :as rf]))

(rf/dispatch [:spawn-food])
(rf/subscribe [:food-coords])

@re-frame.db/app-db
