(ns tag-game-fw.diff-test
  (:require [clojure.test :refer :all]
            [tag-game-fw.diff :as diff]))

(def log (atom []))

(deftype LogRenderer []
  diff/Renderer
  (remove-attr [_ ctx k]
    (swap! log (fn [l] (conj l (str "ra(#" (:id ctx) " " k ")")))))
  (set-attr [_ ctx k v]
    (swap! log (fn [l] (conj l (str "sa(#" (:id ctx) " " k "=" v ")")))))
  (remove-node [_ ctx i]
    (swap! log (fn [l] (conj l (str "rn(#" (:id ctx) " at " i ")")))))
  (create-node [_ name child-ctx]
    (swap! log (fn [l] (conj l (str "cn(" name "#" (:id child-ctx) ")"))))
    (str name "#" (:id child-ctx)))
  (attach-node [_ ctx i node]
    (swap! log (fn [l] (conj l (str "an(" node " to #" (:id ctx) ")"))))))

(defn diff-nodes [a b] (diff/diff-nodes (LogRenderer.) a b {:id "root"} 0))

(comment

  (run-log
   (diff-nodes
    [:div {:attr "hello"}]
    [:div {}]))

  (run-log
   (diff-nodes
    [:div {} [:span {} [:a {:href "https://g.com"}]]]
    [:div {} [:span {} [:a {:href "https://y.ru"}]]]))

  (run-log
   (diff-nodes
    [:div {}]
    [:div {:attr "hello"}]))

  (run-log
   (diff-nodes
    [:div {:attr "hello"}]
    [:div {:attr "hello"}]))

  (run-log
   (diff-nodes
    [:div {:attr "hello"}]
    [:div {:attr "world"}]))

  (run-log
   (diff-nodes
    nil
    [:div {}]))

  (run-log
   (diff-nodes
    [:div {}]
    [:div {}]))

  (run-log
   (diff-nodes
    nil
    [:div {:attr "hello"}]))

  (run-log
   (diff-nodes
    [:div {:attr "hello"}]
    nil))

  (run-log
   (diff-nodes
    nil
    [:div {} [:h1 {}]]))

  (run-log
   (diff-nodes
    [:div {}]
    [:div {} [:h1 {}]]))

  (run-log
   (diff-nodes
    [:div {} [:h1 "h1.text"]]
    [:div {}]))

  (run-log
   (diff-nodes
    [:div {}]
    [:div {:attr "hello"}]))

  (run-log
   (diff-nodes
    [:div {:attr "hello"}]
    [:div {}]))

  (run-log
   (diff-nodes
    [:div {:attr "hello"}]
    [:div {:attr "world"}]))

  (run-log
   (diff-nodes
    [:div {:attr "hello"}]
    [:div {:attr "hello"}]))

  (run-log
   (diff-nodes
    [:div {:attr "value"} [:h1 "h1.text"] [:h2 "h2.text"]]
    [:div {:text "hello"} [:h1 "h1.text"]]))

  (run-log
   (diff-nodes
    [:div {:text "hello"} [:h1 "h1.text"]]
    [:div {:attr "value"} [:h1 "h1.text"] [:h2 "h2.text"]]))

  (run-log
   (diff-nodes
    [:div {:text "hello"} [:h1 "h1.text"] [:h2 "h2.text"]]
    [:div {:attr "value"} [:h2 "h2.text"] [:h3 "h3.text"]]))

  (defmacro run-log [& body]
    `(do
       (reset! log [])
       ~@body
       @log)))

(comment
  (deftest a-test
    (testing "FIXME, I fail."
      (is (= 0 0)))))
