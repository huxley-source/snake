(ns snake.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as rf]
            [snake.events]
            [snake.subs]
            [snake.views :as views]
            [snake.config :as config]
            [snake.utils :as utils]))


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))


(defn mount-root []
  (rf/clear-subscription-cache!)
  (reagent/render [views/main-page]
                  (.getElementById js/document "app")))


(.addEventListener js/document "keydown"
                   (fn [e]
                     (.preventDefault e)
                     (when-let [direction (get utils/key-code->move (.-keyCode e))]
                       (rf/dispatch [:change-direction direction]))))


(defn ^:export init []
  (rf/dispatch-sync [:initialize-db])
  (dev-setup)
  (mount-root))
