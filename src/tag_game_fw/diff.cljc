(ns tag-game-fw.diff
  (:require [clojure.data :as data]))

(defprotocol Renderer
  (remove-attr [_ ctx k])
  (set-attr [_ ctx k v])
  (remove-node [_ ctx i])
  (create-node [_ name child-ctx])
  (attach-node [_ ctx i node]))

(defn- try-get-attrs [node]
  (let [attr (get node 1)]
    (if (map? attr) attr {})))
(defn- get-tag-name [n] (get n 0))
(defn- create-child-ctx [parent-ctx i] {:id (str (:id parent-ctx) i)})
(defn- equals-ext [a b]
  #?(:clj  (= a b)
     :cljs (if (and (some? a) (some? (.-tag b)))
             (= (.-tag a) (.-tag b))
             (= a b))))

(defn- diff-nodes-inner [render a b ctx i]
  (if (= (get-tag-name a) (get-tag-name b))
    (let [a-attr (try-get-attrs a)
          b-attr (try-get-attrs b)]
      (if (not (= a-attr b-attr))
        (let [child-ctx (create-child-ctx ctx i)]
          (doseq [[key value] b-attr]
            (if (not (equals-ext (key a-attr) value))
              (set-attr render child-ctx key value)))
          (doseq [[key _] a-attr]
            (if (not (contains? b-attr key))
              (remove-attr render child-ctx key))))))
    (if (nil? b)
      (remove-node render ctx i)
      (do
        (if (not (nil? a)) (remove-node render ctx i))
        (let [child-ctx (create-child-ctx ctx i)
              node (create-node render (get-tag-name b) child-ctx)]
          (attach-node render ctx i node)
          (let [add-attrs (try-get-attrs b)]
            (doseq [k (keys add-attrs)]
              (set-attr render child-ctx k (get add-attrs k))))))))
  (doseq [child-i (range 2 (max (count a) (count b)))]
    (diff-nodes-inner render (get a child-i) (get b child-i) (create-child-ctx ctx i) (- child-i 2))))

(defn diff [render a b]
  (println "LOG: === START DIFF ===")
  (diff-nodes-inner render a b {:id "root"} 0)
  (println "LOG: === END DIFF ==="))
