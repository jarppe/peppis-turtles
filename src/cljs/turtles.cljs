(ns turtles)

(defn log [& args]
  (.log js/console (apply str args)))

(def app (atom nil))

(defn set-display [id value]
  (-> js/document
      (.getElementById id)
      (.-style)
      (.-display)
      (set! value)))

(defn paint-turtle [ctx img {:keys [x y d]}]
  (.save ctx)
  (.translate ctx x y)
  (.rotate ctx d)
  (.drawImage ctx img -42 -42)
  (.restore ctx))

(defn direction [d x y w h]
  (if (and (< 0 x w) (< 0 y h))
   d
   (+ d (/ Math/PI -4.0))))

(defn move-turtle [w h {:keys [x y d v] :as t}]
  (let [dx (Math/sin d)
        dy (- (Math/cos d))
        x  (+ x (* dx v))
        y  (+ y (* dy v))]
    (assoc t
           :x x
           :y y
           :d (direction d x y w h))))

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
    (doseq [turtle turtles]
      (paint-turtle ctx img turtle))
    (swap! app assoc
           :turtles (map (partial move-turtle width height) turtles))))

(defn rand-turtle [w h]
  {:x (* (rand) w) 
   :y (* (rand) h)
   :d (* (rand) (* 2 Math/PI))
   :v (* (rand) 3)})

(defn rand-turtles [w h]
  (cons (rand-turtle w h)
        (lazy-seq (rand-turtles w h))))

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
                 :turtles (take (+ 3 (rand-int 7)) (rand-turtles width height))}))
  (set-display "loading" "none")
  (set-display "c" "table-cell")
  (.setInterval js/window run 16))

(-> js/window .-onload (set! main))
