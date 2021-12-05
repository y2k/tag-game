(ns tag-game-fw.core
  (:refer-clojure :exclude [atom])
  (:require [freactive.core :as r]
            [freactive.dom :as dom]
            [tag-game-fw.domain :as d])
  (:require-macros [freactive.macros :refer [rx]]))

(defonce app-state (r/atom (d/gen-valid-tag-game)))

(defn handleclick [i]
  (swap!
   app-state
   #(->>
     %
     (d/try-swap i -1 0)
     (d/try-swap i 1 0)
     (d/try-swap i 0 -1)
     (d/try-swap i 0 1))))

(defn view-item [i x]
  [:button
   {:class (if (= x 0) "disabled" "enable")
    :on-click (fn [] (handleclick i))} (str x)])

(defn view [db]
  (into
   [:div {:id "game-field"}]
   (map-indexed view-item db)))

(dom/mount!
 (.getElementById js/document "root")
 (let [db (r/cursor app-state identity)]
   (rx (view @db))))
