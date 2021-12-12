(ns tag-game-fw.core
  (:require [tag-game-fw.domain :as d]
            [tag-game-fw.diff-js :as diffjs]
            [tag-game-fw.diff :as diff]))

(defonce app-state (atom {:field (d/gen-valid-tag-game)}))

(defn handleclick [i]
  (swap!
   app-state
   (fn [db]
     (let [field (:field db)
           new-field (->>
                      field
                      (d/try-swap i -1 0)
                      (d/try-swap i 1 0)
                      (d/try-swap i 0 -1)
                      (d/try-swap i 0 1))]
       (assoc db :field new-field :from (.indexOf field 0) :to (.indexOf new-field 0))))))

(defn view-item [i x from to]
  [:button
   {:className
    (cond
      (and (= i from) (= 1 (- from to))) "game-field__item from-left"
      (and (= i from) (= -1 (- from to))) "game-field__item from-right"
      (and (= i from) (= 4 (- from to))) "game-field__item from-top"
      (and (= i from) (= -4 (- from to))) "game-field__item from-bottom"
      (= i to) "game-field__item empty"
      (and (= 0 x) (nil? from)) "game-field__item empty"
      :else "game-field__item")
    :innerText (if (= x 0) "" (str x))
    :onclick (diffjs/with-tag (fn [] (handleclick i)) {::click i})}])

(defn view [db]
  (into
   [:div {:className "game-field"}]
   (map-indexed (fn [i x] (view-item i x (:from db) (:to db))) (:field db))))

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
