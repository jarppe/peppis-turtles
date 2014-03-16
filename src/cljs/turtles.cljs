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
  (if (and (< 0 x 500) (< 0 y 500))
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
    (doseq [turtle turtles]
      (paint-turtle ctx img turtle))
    (swap! app assoc
           :turtles (map (partial move-turtle width height) turtles))))

(defn main []
  (let [canvas (.getElementById js/document "c")
        ctx    (.getContext canvas "2d")
        img    (doto (js/Image.)
                 (aset "src" "turtle.png"))]
    (reset! app {:canvas  canvas
                 :ctx     ctx
                 :img     img
                 :turtles [{:x 50  :y 50  :d 0.0 :v 1.0}
                           {:x 150 :y 250 :d 1.0 :v 1.5}]}))
  (set-display "loading" "none")
  (set-display "c" "table-cell")
  (.setInterval js/window run 16))

(-> js/window .-onload (set! main))
