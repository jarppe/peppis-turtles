(defproject peppis-turtles "0.1.0-SNAPSHOT"
  :description "Turtles for Peppi"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2173"]]
  :source-paths ["src/cljs"]
  :profiles {:dev {:plugins [[com.cemerick/austin "0.1.4"]
                             [lein-cljsbuild "1.0.1"]]}
             :prod {:cljsbui {:builds {:client {:compiler {:optimizations :advanced
                                                           :elide-asserts true
                                                           :pretty-print false}}}}}
             :srcmap {:cljsbuild {:builds {:client {:compiler {:source-map "target/cljs-client.js.map"
                                                               :source-map-path "client"}}}}}}
  :cljsbuild {:builds {:client {:source-paths ["src/cljs"]
                                :notify-command ["growlnotify" "-n" "cljsbuild" "-m"]
                                :compiler {:output-dir "target/client"
                                           :output-to "resources/turtles.js"
                                           :pretty-print true}}}})
