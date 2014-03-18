(ns turtles)

(defn log [& args]
  (.log js/console (apply str args)))

(def app (atom nil))

(def deg180 Math/PI)
(def deg90 (/ Math/PI 2.0))
(def deg45 (/ Math/PI 4.0))

(defn rnd [lo hi] (+ lo (rand (- hi lo))))

(defn rand-turtle [w h]
  (let [d (rnd (- deg45) deg45)]
    (assoc (condp = (rand-int 4)
             0  {:x -44
                 :y (rnd -44 (+ h 44))
                 :d (+ d deg90)}
             1  {:x (+ w 44)
                 :y (rnd -44 (+ h 44))
                 :d (- d deg90)}
             2  {:x (rnd -44 (+ w 44))
                 :y -44
                 :d (+ d deg180)}
             3  {:x (rnd -44 (+ w 44))
                 :y (+ h 44)
                 :d d})
           :v (rnd 1 3))))

(defn gonez? [w h {:keys [x y]}]
  (not (and (< -44 x (+ w 44))
            (< -44 y (+ h 44)))))

(defn paint-turtle [ctx img {:keys [x y d] :as turtle}]
  (.save ctx)
  (.translate ctx x y)
  (.rotate ctx d)
  (.drawImage ctx img -42 -42)
  (.restore ctx)
  turtle)

(defn move-turtle [w h {:keys [x y d v] :as turtle}]
  (let [dx (Math/sin d)
        dy (- (Math/cos d))]
    (assoc turtle
           :x (+ x (* dx v))
           :y (+ y (* dy v)))))

(defn add-turtles [w h turtles]
  (if (< (count turtles) 10)
    (cons (rand-turtle w h) turtles)
    turtles))

(defn run []
  (let [{:keys [ctx img turtles]} @app
        win     js/window
        width   (.-innerWidth win)
        height  (.-innerHeight win)]
    (let [canvas (.-canvas ctx)]
      (set! (.-width canvas) width) 
      (set! (.-height canvas) height))
    (set! (.-fillStyle ctx) "rgb(32,32,32)")
    (.fillRect ctx 0 0 width height)
    (set! (.-textAlign ctx) "center")
    (set! (.-textBaseline ctx) "bottom")
    (set! (.-font ctx) "18px sans-serif")
    (set! (.-fillStyle ctx) "rgb(32,255,32)")
    (.fillText ctx "Pepin kilpikonnat" (/ width 2) height)
    (swap! app assoc :turtles (->> turtles
                                   (remove (partial gonez? width height))
                                   (add-turtles width height)
                                   (map (partial paint-turtle ctx img))
                                   (map (partial move-turtle width height))
                                   (doall)))))

(defn set-display [id value]
  (-> js/document
      (.getElementById id)
      (.-style)
      (.-display)
      (set! value)))

(defn main []
  (let [canvas (.getElementById js/document "c")
        ctx    (.getContext canvas "2d")
        img    (doto (js/Image.)
                 (aset "src" "turtle.png"))
        win    js/window
        width  (.-innerWidth win)
        height (.-innerHeight win)]
    (reset! app {:canvas  canvas
                 :ctx     ctx
                 :img     img
                 :turtles []}))
  (set-display "loading" "none")
  (set-display "c" "table-cell")
  (.setInterval js/window run 16))

(-> js/window .-onload (set! main))
