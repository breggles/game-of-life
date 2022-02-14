(ns game-of-life.quil
  (:require [quil.core :as q]
            [game-of-life.core :as gol]))

(def grid-width 60)

(def grid-height 60)

(def frame-rate 4)

(def initial-grid-1 [[1 5]
    [2 5] [1 6] [2 6] [11 5] [11 6] [11 7] [12 4] [12 8]
    [13 3] [13 9] [14 3] [14 9] [15 6] [16 4] [16 8] [17 5]
    [17 6] [17 7] [18 6] [21 3] [21 4] [21 5] [22 3] [22 4]
    [22 5] [23 2] [23 6] [25 1] [25 2] [25 6] [25 7] [35 4]
    [35 5] [36 4] [36 5] ])

(def initial-grid-rand
  (->> (gol/all-coords grid-width grid-height)
       (map #(vector (rand-int 2) %))
       (filter #(= 1 (first %)))
       (map second)))

(defn setup []
  (q/frame-rate frame-rate)
  (q/color-mode :hsb)
  (q/fill 50)
  (q/set-state! :grid (atom (gol/init-grid
                              grid-width
                              grid-height
                              initial-grid-rand))))

(defn draw []
  (q/background 240)
  (let [grid (q/state :grid)]
    (doseq [[x y] (gol/live-cell-coords @grid)]
      (q/rect (* 10 x) (* 10 y) 10 10))
    (swap! grid gol/next-generation)))

(defn create-sketch []
  (q/sketch
    :title "Game of Life"
    :size [(* 10 grid-width) (* 10 grid-height)]
    :setup #'setup
    :draw #'draw
    :features [:keep-on-top]))

(def sketch (create-sketch))
