(ns game-of-life.next

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

(defn setup []
  (q/frame-rate 30)
  (q/color-mode :hsb)
  (into []
        (for [_ (range grid-size)]
          (into []
                (for [_ (range grid-size)]
                  (if (< (rand) 0.2)
                    true false))))))

(defn adjacent-alive [state x y]
  (->> adjacent
       (filter (fn [[i j]] (-> state (get (+ y j)) (get (+ x i)))))
       count))

(defn compute-cell [state i j]
  (let [n (adjacent-alive state i j)]
    (if (-> state (get j) (get i))
      (<= 2 n 3)
      (= 3 n))))

(defn compute-row [state i]
  (->> (range grid-size)
       (map #(compute-cell state i %))
       (into [])))

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

   ;; The 'step' function execution was splitted into two steps: 'compute-row' and 'compute-cell'.
   ;; It was done to gane some readability but mostly to be able to run it in parallel.
   ;; Parallelyzation was applied only to `compute-row`. It is possible to parallize more 
   ;; (on 'compute-cell' and even 'adjacent-alive'). However, the overhead of runnig such a big number 
   ;; of tasks in parallel was too big and the preformance increase was't significant.
   ;; 
   ;; Future improvements:
   ;; If the 'gird-size' will grow significatly, at some point it might be reasonable to scale this computation.
   ;; Instead of parallelization of 'compute-row' we could send messages to Kafka and distribute work between 
   ;; multiple workers/machines. And on each worker/machine we could apply parallelization on 'compute-cell' level.
   ;; However it will require shared data store (db or chache), so workers could acces representation of 'state' there.
   ;;    
   (->> (range grid-size)
        (pmap #(compute-row state %)) ;; parallel mapping is done here
        (into []))))

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
