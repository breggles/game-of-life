(ns game-of-life.core)

(defn- empty-grid [width height]
  (->> (repeat height 0)
       vec
       (repeat width)
       vec))

(defn- set-cell [grid pos value]
  (assoc-in grid pos value))

; (defn- kill-cell [grid pos]
;   (set-cell grid pos 0))

(defn- birth-cell [grid pos]
  (set-cell grid pos 1))

(def unit-vectors
  (for [x (range -1 2)
        y (range -1 2)
        :when (not= x y 0)]
    [x y]))

(defn- vector+ [v1 v2]
  (mapv + v1 v2))

(defn- neighbour-coords [pos]
  (mapv vector+
        unit-vectors
        (repeat pos)))

(defn- on-grid? [width height [x y]]
  (and (< -1 x width)
       (< -1 y height)))

(defn- on-grid-neighbour-coords [width height pos]
  (filter (partial on-grid? width height)
          (neighbour-coords pos)))

(defn- neighbour-values [grid pos]
  (map (partial get-in grid)
       (on-grid-neighbour-coords
              (grid-width grid)
              (grid-height grid)
              pos)))

(defn- neighbour-live-cell-count [grid pos]
  (apply + (neighbour-values grid pos)))

(defn- next-cell-state [grid pos]
  (let [current-state (get-in grid pos)]
    (case [current-state
           (neighbour-live-cell-count grid pos)]
       [0 3] 1
       [1 2] 1
       [1 3] 1
       0)))

(defn- all-coords [width height]
  (for [x (range 0 width)
        y (range 0 height)]
    [x y]))

(defn- set-new-cell-state [old-grid new-grid pos]
  (set-cell new-grid
            pos
            (next-cell-state old-grid pos)))

(defn- pivot [grid]
  (apply mapv vector grid))

(defn- alive? [grid pos]
  (= 1 (get-in grid pos)))

(defn- grid-width [grid]
  (count grid))

(defn- grid-height [grid]
  (count (grid 0)))

(defn init-grid [width height live-cell-positions]
  (reduce birth-cell
          (empty-grid width height)
          live-cell-positions))

(defn next-generation [grid]
  (let [width  (grid-width grid)
        height (grid-height grid)]
    (reduce (partial set-new-cell-state grid)
            (empty-grid width height)
            (all-coords width height))))

(defn live-cell-coords [grid]
  (filterv (partial alive? grid)
           (all-coords (grid-width grid) (grid-height grid))))

(defn print-grid [grid]
  (doseq [row (pivot grid)]
    (apply println row)))

(comment
  (empty-grid 3 4)
  (set-cell (empty-grid 3 4) [1 2] 1)
  (vector+ [1 2] [3 4])
  (neighbour-coords [2 3])
  (neighbour-coords [0 0])
  (on-grid? 3 5 [1 2])
  (on-grid-neighbour-coords 3 4 [0 0])
  (on-grid-neighbour-coords 3 4 [2 3])
  (neighbour-values (birth-cell (empty-grid 3 4) [1 0]) [0 0])
  (neighbour-live-cell-count
    (birth-cell (birth-cell (empty-grid 3 4) [1 0]) [0 1]) 
    [0 0])
  (init-grid 3 4 [[0 0] [1 1] [2 2]])
  (next-cell-state (init-grid 3 4 [[0 0] [0 1] [1 1] [2 2]]) [0 1])
  (next-generation (init-grid 3 4 [[0 0] [0 1] [1 1] [2 2]]))
  (pivot (init-grid 3 4 [[0 0] [1 1] [2 2]]))
  (print-grid (init-grid 3 4 [[0 0] [0 1] [1 1] [2 2]]))
  (print-grid (next-generation (init-grid 3 4 [[0 0] [0 1] [1 1] [2 2]])))
  (doseq [generation (take 6 (iterate next-generation
                                       (init-grid 3 4 [[0 0]
                                                       [0 1]
                                                       [1 1]
                                                       [2 2]])))]
    (print-grid generation)
    (println))
  (live-cell-coords (init-grid 3 4 [[0 0] [1 1] [2 2]]))
  )
