(ns tag-game-fw.core
  (:require [tag-game-fw.domain :as d]
            [tag-game-fw.diff-js :as diffjs]
            [tag-game-fw.diff :as diff]))

(defonce app-state (atom (d/gen-valid-tag-game)))

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
   {:className (str "game-field__item " (if (= x 0) "disabled" "enable"))
    :innerText (str x)
    :onclick (diffjs/with-tag (fn [] (handleclick i)) {::click i})}])

(defn view [db]
  (into
   [:div {:className "game-field"}]
   (map-indexed view-item db)))

(def current-vdom (atom nil))

(defn render-view [db]
  (reset! current-vdom (view db)))

(add-watch app-state :key (fn [_ _ _ db] (render-view db)))

(add-watch
 current-vdom
 :renderer
 (fn [_ _ o n]
   (if (not (= o n))
     (diff/diff (diffjs/JsRenderer.) o n))))

(render-view @app-state)
