(ns tag-game-fw.diff-js
  (:require [clojure.data :as data]
            [tag-game-fw.diff :as diff]))

;; ===

(comment

  (let [root (.getElementById js/document "root")
        child (get (vec (.-children root)) 0)]
    (.remove child))

  (defn replace-child [i]
    (let [root (.getElementById js/document "root")
          child (.createElement js/document "span")
          old-node (get (vec (.-children root)) i)]
      (if (nil? old-node)
        (.appendChild root child)
        (.replaceChild root child old-node))))
  (replace-child 1)

  (comment))

;; ===

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
