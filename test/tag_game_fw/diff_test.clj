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
    [:div {}] [:div {}] []
    [:div {:attr "hello"}] [:div {:attr "hello"}] []
    [:div {:attr "hello"}] [:div {}] ["ra(#root0 :attr)"]
    [:div {} [:span {} [:a {:href "https://g.com"}]]] [:div {} [:span {} [:a {:href "https://y.ru"}]]] ["sa(#root000 :href=https://y.ru)"]
    [:div {}] [:div {:attr "hello"}] ["sa(#root0 :attr=hello)"]
    [:div {:attr "hello"}] [:div {:attr "world"}] ["sa(#root0 :attr=world)"]
    nil [:div {}] ["cn(:div#root0)" "an(:div#root0 to #root)"]
    nil [:div {:attr "hello"}] ["cn(:div#root0)" "an(:div#root0 to #root)" "sa(#root0 :attr=hello)"]
    [:div {:attr "hello"}] nil ["rn(#root at 0)"]
    nil [:div {} [:h1 {}]] ["cn(:div#root0)" "an(:div#root0 to #root)" "cn(:h1#root00)" "an(:h1#root00 to #root0)"]
    [:div {}] [:div {} [:h1 {}]] ["cn(:h1#root00)" "an(:h1#root00 to #root0)"]
    [:div {} [:h1 "h1.text"]] [:div {}] ["rn(#root0 at 0)"]
    [:div {}] [:div {:attr "hello"}] ["sa(#root0 :attr=hello)"]
    [:div {:attr "hello"}] [:div {}] ["ra(#root0 :attr)"]
    [:div {:attr "hello"}] [:div {:attr "world"}] ["sa(#root0 :attr=world)"]
    [:div {:attr "hello"}] [:div {:attr "hello"}] []
    [:div {:attr "value"} [:h1 "h1.text"] [:h2 "h2.text"]] [:div {:text "hello"} [:h1 "h1.text"]] ["sa(#root0 :text=hello)" "ra(#root0 :attr)" "rn(#root0 at 1)"]
    [:div {:text "hello"} [:h1 "h1.text"]] [:div {:attr "value"} [:h1 "h1.text"] [:h2 "h2.text"]] ["sa(#root0 :attr=value)" "ra(#root0 :text)" "cn(:h2#root01)" "an(:h2#root01 to #root0)"]
    [:div {:text "hello"} [:h1 "h1.text"] [:h2 "h2.text"]] [:div {:attr "value"} [:h2 "h2.text"] [:h3 "h3.text"]] ["sa(#root0 :attr=value)" "ra(#root0 :text)" "rn(#root0 at 0)" "cn(:h2#root00)" "an(:h2#root00 to #root0)" "rn(#root0 at 1)" "cn(:h3#root01)" "an(:h3#root01 to #root0)"]))
