(ns snake.core
    (:require [reagent.core :as reagent]
              [re-frame.core :as rf]
              [snake.events]
              [snake.subs]
              [snake.views :as views]
              [snake.config :as config]))


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
                     (rf/dispatch [:change-direction (.-keyCode e)])))



(defn ^:export init []
  (rf/dispatch-sync [:initialize-db])
  (dev-setup)
  (mount-root))
