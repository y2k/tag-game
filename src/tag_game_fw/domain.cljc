(ns tag-game-fw.domain
  (:refer-clojure :exclude [atom]))

(defn check-valid-tags [xs]
  (->>
   (for [x (range (count xs))
         y (range (inc x) (count xs))]
     (and
      (> (get xs x) 0)
      (> (get xs y) 0)
      (> (get xs x) (get xs y))))
   (reduce (fn [x c] (if c (inc x) x)) 0)
   (+ 1 (quot (.indexOf xs 0) 4))
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
