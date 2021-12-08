(ns core-test
  (:require [clojure.test :refer :all]
            [clojure.data :as data]))

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
  (swap! log (fn [l] (conj l (str "remove-attr(#" (:id ctx) " " k ")")))))
(defn set-attr [ctx k v]
  (swap! log (fn [l] (conj l (str "set-attr(#" (:id ctx) " " k "=" v ")")))))
(defn remove-node [ctx i]
  (swap! log (fn [l] (conj l (str "remove-node(#" (:id ctx) " at " i ")")))))
(defn create-node [name child-ctx]
  (swap! log (fn [l] (conj l (str "create-node(" name "#" (:id child-ctx) ")"))))
  (str name "#" (:id child-ctx)))
(defn attach-node [ctx i node]
  (swap! log (fn [l] (conj l (str "attach-node(" node " to #" (:id ctx) ")")))))

(defn get-tag-name [n] (get n 0))
(defn create-child-ctx [parent-ctx i] {:id (str (:id parent-ctx) i)})

(defn diff-nodes-inner [a b ctx i]
  (if (= (get-tag-name a) (get-tag-name b))
    (let [a-attr (try-get-attrs a)
          b-attr (try-get-attrs b)]
      (if (not (= a-attr b-attr))
        (let [[rem-attrs add-attrs] (data/diff a-attr b-attr)
              child-ctx (create-child-ctx ctx i)]
          (doseq [k (keys rem-attrs)]
            (remove-attr child-ctx k))
          (doseq [k (keys add-attrs)]
            (set-attr child-ctx k (get add-attrs k))))))
    (if (nil? b)
      (remove-node ctx i)
      (do
        (if (not (nil? a)) (remove-node ctx i))
        (let [child-ctx (create-child-ctx ctx i)
              node (create-node (get-tag-name b) child-ctx)]
          (attach-node ctx i node)
          (let [add-attrs (try-get-attrs b)]
            (doseq [k (keys add-attrs)]
              (set-attr child-ctx k (get add-attrs k))))))))
  (doseq [child-i (range 2 (max (count a) (count b)))]
    (diff-nodes-inner
     (get a child-i)
     (get b child-i)
     (create-child-ctx ctx i)
     (- child-i 2))))

(defn diff-nodes [a b] (diff-nodes-inner a b {:id "root"} 0))

(comment

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
    [:div {}]))

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
