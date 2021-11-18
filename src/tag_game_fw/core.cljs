(ns tag-game-fw.core)

(defonce app-state (atom (shuffle [1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 0])))

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
