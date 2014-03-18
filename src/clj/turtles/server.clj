(ns turtles.server
  (:require [clojure.java.io :as io]
            [clojure.string :as s]
            [ring.adapter.jetty :as jetty]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.http-response :refer [ok content-type] :as resp]
            [ring.middleware.http-response :refer [catch-response]]            
            [ring.middleware.format :refer [wrap-restful-format]]
            [hiccup.page :refer [html5 include-css include-js]]))

(def index-response (-> (html5 [:head
                                [:meta {:charset "utf-8"}]
                                [:title "Peppi's turtlez"]
                                [:style {:type "text/css"}
                                 (-> "turtles.css"
                                     io/resource
                                     io/reader
                                     slurp
                                     (s/replace #"\s+" " "))]]
                               [:body
                                [:div#loading "Loading..."]
                                [:canvas#c {:style "display: none;"}]]
                               (include-js "turtles.js"))
                        (ok)
                        (content-type "text/html")))

(def app-routes
  [(GET  "/" [] index-response)
   (GET  "/dev/ping" [] (ok "pongs\n"))
   (GET  "/dev/echo" request (ok (dissoc request :body)))
   (route/resources "/")
   (route/not-found "Not found")])

(def app
  (-> (apply routes app-routes)
      (wrap-restful-format :formats [:edn :json])
      (catch-response)))

(defn -main [& [port]]
  (jetty/run-jetty (var app) {:port (Integer/parseInt (or port "8080") 10)
                              :join? false}))

