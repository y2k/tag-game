(ns tag-game-fw.core)

(defn check-valid-tags [xs]
  (->>
   (for [x (range (count xs))
         y (range (inc x) (count xs))]
     (> (get xs x) (get xs y)))
   (reduce (fn [x c] (if c (inc x) x)) 0)
   (even?)))

(defn gen-valid-tag-game []
  (->>
   (repeatedly (fn [] (shuffle (range 16))))
   (filter check-valid-tags)
   (first)))

(defonce app-state (atom (gen-valid-tag-game)))

(defn try-swap [i x y]
  (let [target-pos (+ i x (* 4 y))
        target (get @app-state target-pos)]
    (if (= 0 target)
      (swap!
       app-state
       (fn [state]
         (->
          state
          (assoc target-pos (get state i))
          (assoc i target))))
      nil)))

(defn handleclick [i callback]
  (try-swap i -1 0)
  (try-swap i 1 0)
  (try-swap i 0 -1)
  (try-swap i 0 1)
  (callback))

(defn render []
  (doseq [i (range (count @app-state))]
    (let [x (get @app-state i)
          b (.getElementById js/document (str "b" i))]
      (set! (.-onclick b) (fn [] (handleclick i render)))
      (set! (.-innerText b) (str x))
      (set! (.-visibility (.-style b)) (if (= x 0) "hidden" "visible")))))

(render)
