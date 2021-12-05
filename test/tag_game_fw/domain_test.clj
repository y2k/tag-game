(ns tag-game-fw.domain-test
  (:require [clojure.test :refer :all]
            [tag-game-fw.domain :as d]))

(deftest test-check-valid-tags
  (are [f c] (= c (d/check-valid-tags f))
    [1 2 3 4 5 6 7 9 10 11 12 13 14 15 0] true
    [1 2 3 4 5 6 7 9 10 11 12 13 14 0 15] true
    [1 2 3 4 5 6 7 9 10 11 0 13 14 15 12] true
    [1 2 3 4 5 6 7 9 10 11 12 13 15 14 0] false
    [1 2 3 4 5 6 7 9 10 12 11 13 14 15 0] false))