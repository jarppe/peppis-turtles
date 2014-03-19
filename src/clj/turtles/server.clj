(ns turtles.server
  (:require [clojure.java.io :as io]
            [clojure.string :as s]
            [ring.adapter.jetty :as jetty]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :refer [header]]
            [ring.util.http-response :refer [ok]]
            [hiccup.page :refer [html5]]
            [clojure.data.codec.base64 :as b64])
  (:import [org.apache.commons.io IOUtils]
           [java.io ByteArrayOutputStream ByteArrayInputStream]
           [java.util.zip GZIPInputStream GZIPOutputStream]))

(def dev?  (nil? (System/getenv "DYNO")))
(def prod? (not dev?))

(defn image->data-url [resource-name]
  (let [buf (ByteArrayOutputStream. 8096)]
    (.write buf (.getBytes "data:image/png;base64,"))
    (with-open [in (-> resource-name io/resource io/input-stream)]
      (b64/encoding-transfer in buf))
    (String. (.toByteArray buf))))

(defn gzip [data]
  (let [buf (ByteArrayOutputStream. 8096)
        out (GZIPOutputStream. buf)
        in (ByteArrayInputStream. (.getBytes data))]
    (IOUtils/copy in out)
    (.flush out)
    (.finish out)
    (.toByteArray buf)))

(defn index-content []
  (html5 [:head
          [:meta {:charset "utf-8"}]
          [:title (if prod? "Peppi's turtles" "DEV: turtlesz")]
          [:style {:type "text/css"}
           (with-open [in (-> "turtles.css" io/resource io/reader)]
             (-> in slurp (s/replace #"\s+" " ")))]]
         [:body
          [:div#loading
           [:img#turtle {:src (image->data-url "turtle.png")}]
           [:span "Loading..."]]
          [:canvas#c {:style "display: none;"}]]
         [:script {:type "text/javascript"}
          (with-open [in (-> "turtles.js" io/resource io/reader)]
            (slurp in))]))

(defn index-data []
  (gzip (index-content)))

(if prod? (def index-data (memoize index-data)))

(defn index-response []
  (-> (index-data)
    (ByteArrayInputStream.)
    (ok)
    (header "Content-Type" "text/html")
    (header "Content-Encoding" "gzip")))

(def app
  (routes (GET  "/" [] (index-response))
          (GET  "/dev/ping" [] (ok "pongs\n"))
          (route/not-found "Not found")))

(defn -main [& [port]]
  (jetty/run-jetty (if dev? (var app) app)
                   {:port (Integer/parseInt (or port "8080") 10)
                    :join? false}))
