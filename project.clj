(defproject peppis-turtles "0.1.0-SNAPSHOT"
  :description "Turtles for Peppi"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2173"]
                 [ring "1.2.1"]
                 [compojure "1.1.6"]
                 [metosin/ring-http-response "0.3.0"]
                 [ring-middleware-format "0.3.2"]]
  :source-paths ["src/clj" "src/cljs"]
  :plugins [[lein-cljsbuild "1.0.1"]]
  :profiles {:dev {:source-paths ^:replace ["src/clj"]
                   :cljsbuild {:builds {:client {:notify-command ["growlnotify" "-n" "cljsbuild" "-m"]}}}}
             :srcmap {:cljsbuild {:builds {:client {:compiler {:source-map "target/cljs-client.js.map"
                                                               :source-map-path "client"}}}}}
             :uberjar {:main turtles.server
                       :aot :all
                       :cljsbuild {:builds {:client {:compiler {:optimizations :advanced
                                                                :elide-asserts true
                                                                :pretty-print false}}}}}}
  :cljsbuild {:builds {:client {:source-paths ["src/cljs"]
                                :compiler {:output-dir "target/client"
                                           :output-to "resources/public/turtles.js"
                                           :pretty-print true}}}}
  :hooks [leiningen.cljsbuild]
  :uberjar-name "turtles.jar"
  :min-lein-version "2.0.0")
