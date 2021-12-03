(ns tag-game-fw.core
  (:refer-clojure :exclude [atom])
  (:require [freactive.core :as r]
            [freactive.dom :as dom])
  (:require-macros [freactive.macros :refer [rx]]))

(defn check-valid-tags [xs]
  (->>
   (for [x (range (count xs))
         y (range (inc x) (count xs))]
     (> (get xs x) (get xs y)))
   (reduce (fn [x c] (if c (inc x) x)) 0)
   (+ 1 (.indexOf xs 0))
   (even?)))

(defn gen-valid-tag-game []
  (->>
   (repeatedly (fn [] (shuffle (range 16))))
   (filter check-valid-tags)
   (first)))

(defn try-swap [i x y db]
  (let [target-pos (+ i x (* 4 y))
        target (get db target-pos)]
    (if (= 0 target)
      (->
       db
       (assoc target-pos (get db i))
       (assoc i target))
      db)))

(defonce app-state (r/atom (gen-valid-tag-game)))

(defn handleclick [i]
  (swap!
   app-state
   #(->>
     %
     (try-swap i -1 0)
     (try-swap i 1 0)
     (try-swap i 0 -1)
     (try-swap i 0 1))))

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
