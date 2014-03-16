(ns turtles.server
  (:require [ring.adapter.jetty :as jetty]
            [clojure.java.io :as io]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.http-response :refer [ok content-type] :as resp]
            [ring.middleware.http-response :refer [catch-response]]            
            [ring.middleware.format :refer [wrap-restful-format]]))

(def app-routes
  [(GET  "/" [] (-> "turtles.html"
                    io/resource
                    io/input-stream
                    ok
                    (content-type "text/html")))
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
