(ns tag-game-fw.diff-js
  (:require [clojure.data :as data]
            [tag-game-fw.diff :as diff]))

(defn with-tag [f tag]
  (set! (.-tag f) tag)
  f)

(deftype JsRenderer []
  diff/Renderer
  (remove-attr [_ ctx k]
    (let [node (.getElementById js/document (:id ctx))]
      (aset node (clj->js k) nil)))
  (set-attr [_ ctx k v]
    (let [node (.getElementById js/document (:id ctx))]
      (aset node (clj->js k) v)))
  (remove-node [_ ctx i]
    (let [root (.getElementById js/document (:id ctx))
          child (get (vec (.-children root)) i)]
      (.remove child)))
  (create-node [_ name child-ctx]
    (let [node (.createElement js/document (clj->js name))]
      (set! (.-id node) (:id child-ctx))
      node))
  (attach-node [_ ctx i node]
    (let [root (.getElementById js/document (:id ctx))
          old-node (get (vec (.-children root)) i)]
      (if (nil? old-node)
        (.appendChild root node)
        (.replaceChild root node old-node)))))
