(ns tag-game-fw.diff-test
  (:require [clojure.test :refer :all]
            [tag-game-fw.diff :as diff]))

(def log (atom []))

(deftype LogRenderer []
  diff/Renderer
  (remove-attr [_ ctx k] (swap! log (fn [l] (conj l (str "ra(#" (:id ctx) " " k ")")))))
  (set-attr [_ ctx k v] (swap! log (fn [l] (conj l (str "sa(#" (:id ctx) " " k "=" v ")")))))
  (remove-node [_ ctx i] (swap! log (fn [l] (conj l (str "rn(#" (:id ctx) " at " i ")")))))
  (create-node [_ name child-ctx]
    (swap! log (fn [l] (conj l (str "cn(" name "#" (:id child-ctx) ")"))))
    (str name "#" (:id child-ctx)))
  (attach-node [_ ctx i node] (swap! log (fn [l] (conj l (str "an(" node " to #" (:id ctx) ")"))))))

(defn run-diff-asset [a b]
  (reset! log [])
  (diff/diff (LogRenderer.) a b)
  @log)

(deftest diff-tests
  (are [a b expected] (= (run-diff-asset a b) expected)
    nil [:div {} [:div {} [:div {}]] [:div {} [:div {}]]] ["cn(:div#A)" "an(:div#A to #root)" "cn(:div#AA)" "an(:div#AA to #A)" "cn(:div#AAA)" "an(:div#AAA to #AA)" "cn(:div#AB)" "an(:div#AB to #A)" "cn(:div#ABA)" "an(:div#ABA to #AB)"]
    [:div {}] [:div {}] []
    [:div {:attr "hello"}] [:div {:attr "hello"}] []
    [:div {:attr "hello"}] [:div {}] ["ra(#A :attr)"]
    [:div {} [:span {} [:a {:href "https://g.com"}]]] [:div {} [:span {} [:a {:href "https://y.ru"}]]] ["sa(#AAA :href=https://y.ru)"]
    [:div {}] [:div {:attr "hello"}] ["sa(#A :attr=hello)"]
    [:div {:attr "hello"}] [:div {:attr "world"}] ["sa(#A :attr=world)"]
    nil [:div {}] ["cn(:div#A)" "an(:div#A to #root)"]
    nil [:div {:attr "hello"}] ["cn(:div#A)" "an(:div#A to #root)" "sa(#A :attr=hello)"]
    [:div {:attr "hello"}] nil ["rn(#root at 0)"]
    nil [:div {} [:h1 {}]] ["cn(:div#A)" "an(:div#A to #root)" "cn(:h1#AA)" "an(:h1#AA to #A)"]
    [:div {}] [:div {} [:h1 {}]] ["cn(:h1#AA)" "an(:h1#AA to #A)"]
    [:div {} [:h1 "h1.text"]] [:div {}] ["rn(#A at 0)"]
    [:div {}] [:div {:attr "hello"}] ["sa(#A :attr=hello)"]
    [:div {:attr "hello"}] [:div {}] ["ra(#A :attr)"]
    [:div {:attr "hello"}] [:div {:attr "world"}] ["sa(#A :attr=world)"]
    [:div {:attr "hello"}] [:div {:attr "hello"}] []
    [:div {:attr "value"} [:h1 "h1.text"] [:h2 "h2.text"]] [:div {:text "hello"} [:h1 "h1.text"]] ["sa(#A :text=hello)" "ra(#A :attr)" "rn(#A at 1)"]
    [:div {:text "hello"} [:h1 "h1.text"]] [:div {:attr "value"} [:h1 "h1.text"] [:h2 "h2.text"]] ["sa(#A :attr=value)" "ra(#A :text)" "cn(:h2#AB)" "an(:h2#AB to #A)"]
    [:div {:text "hello"} [:h1 "h1.text"] [:h2 "h2.text"]] [:div {:attr "value"} [:h2 "h2.text"] [:h3 "h3.text"]] ["sa(#A :attr=value)" "ra(#A :text)" "rn(#A at 0)" "cn(:h2#AA)" "an(:h2#AA to #A)" "rn(#A at 1)" "cn(:h3#AB)" "an(:h3#AB to #A)"]))
