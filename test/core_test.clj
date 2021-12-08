(ns core-test
  (:require [clojure.test :refer :all]))

(def a1 [:div
         [:input "2"]])
(def a2 [:div
         [:span "1"]
         [:input "2"]])

(defn try-get-attrs [node]
  (let [attr (get node 1)]
    (if (map? attr) attr {})))

(def log (atom []))

(defn remove-attr [ctx k]
  (swap! log (fn [l] (conj l (str "remove-attr[" ctx "][" k "]")))))
(defn set-attr [ctx k v]
  (swap! log (fn [l] (conj l (str "set-attr[" ctx "][" k "=" v "]")))))
(defn remove-node [ctx i]
  (swap! log (fn [l] (conj l (str "remove-node[" ctx "][index=" i "]")))))
(defn create-node []
  (swap! log (fn [l] (conj l "create-node"))))

(defn diff-nodes [a b ctx]
  (if (= (get a 0) (get b 0))
    (let [a-attr (try-get-attrs a)
          b-attr (try-get-attrs b)]
      (if (= a-attr b-attr)
        (comment FIXME)
        (let [[rem-attrs add-attrs] (diff a-attr b-attr)]
          (doseq [k (keys rem-attrs)]
            (remove-attr ctx k))
          (doseq [k (keys add-attrs)]
            (set-attr ctx k (get add-attrs k)))
          (comment FIXME))))
    (do
      (remove-node ctx)
      (create-node ctx (get a 0))
      (comment FIXME)))

  (doseq [i (range 2 (count a))]
    (comment "FIXME"))
  (doseq [i (range (count b) (count a))]
    (remove-node ctx (- i 2)))

  (comment FIXME))

(comment
  @log

  (reset! log [])
  (diff-nodes
   [:div {:attr "value"} [:h1 "h1.text"] [:h2 "h2.text"]]
   [:div {:text "hello"} [:h1 "h1.text"]]
   {})

  (reset! log [])
  (diff-nodes
   [:div {:text "hello"} [:h1 "h1.text"]]
   [:div {:attr "value"} [:h1 "h1.text"] [:h2 "h2.text"]]
   {})

  (diff-nodes
   [:div {:text "hello"} [:h1 "h1.text"] [:h2 "h2.text"]]
   [:div {:attr "value"} [:h2 "h2.text"] [:h3 "h3.text"]]
   {})
;; -- end
  )

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 0 0))))
