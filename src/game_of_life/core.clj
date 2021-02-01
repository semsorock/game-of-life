(ns game-of-life.core

  ;;  ,--.,------. ,--. ,---.   ,--------.,--.   ,--.
  ;;  |  ||  .--. '|  |'   .-'  '--.  .--' \  `.'  /
  ;;  |  ||  '--'.'|  |`.  `-.     |  |     \     /
  ;;  |  ||  |\  \ |  |.-'    |.--.|  |      \   /
  ;;  `--'`--' '--'`--'`-----' '--'`--'       `-'

  ;; In case you aren't familiar, this is a cellular automaton in a 2d grid of cells.
  ;; https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life

  (:require [quil.core :as q]
            quil.middleware))

(def grid-size 250)
(def pixel-size 1000)
(def cell-size (/ pixel-size grid-size))

(def adjacent
  [[-1 +1]  [+0 +1]  [+1 +1]
   [-1 +0]           [+1 +0]
   [-1 -1]  [+0 -1]  [+1 -1]])

(defn adjacent-alive [state x y]
  (->>
   (for [[i j] adjacent]
     (-> state (get (+ y j)) (get (+ x i))))
   (filter identity)
   count))

(defn setup []
  (q/frame-rate 30)
  (q/color-mode :hsb)
  (into []
        (for [_ (range grid-size)]
          (into []
                (for [_ (range grid-size)]
                  (if (< (rand) 0.2)
                    true false))))))

(defn step [state]
  (time

   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; 
   ;; Please make this part faster, by parallelizing it.
   ;; Feel free to rearrange other things to make it faster as well, but keep the code as
   ;; readable as you can. Try to strike a balance between clarity and performance.
   ;; Describe your solution and why you think it strikes this balance.
   ;; Don't worry about the speed of drawing, just the state update.
   ;; You are also welcome to describe further ways of improving it (beyond what you code)
   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; 
   
   (into []
         (for [j (range grid-size)]
           (into []
                 (for [i (range grid-size)
                       :let [n (adjacent-alive state i j)]]
                   (if (-> state (get j) (get i))
                     (<= 2 n 3)     ;; to remain alive...
                     (= 3 n)))))))) ;; to come to life...

(defn draw [state]
  (q/background 240)
  (q/fill 10 255 255)
  (doseq [i (range grid-size)
          j (range grid-size)]
    (when (-> state (get j) (get i))
      (q/ellipse
       (* i cell-size)
       (* j cell-size)
       cell-size
       cell-size))))

(q/defsketch game-of-life
  :size [pixel-size pixel-size]
  :setup setup
  :update step
  :draw draw
  :features [:keep-on-top]
  :middleware [quil.middleware/fun-mode])
