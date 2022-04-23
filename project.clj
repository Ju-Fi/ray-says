(defproject ray-says "0.1.0-SNAPSHOT"
  :description "Markov-Chain sentence generator that uses RaysWorks subtitles"
  :dependencies [[org.clojure/clojure "1.10.1"]]
  :main ^:skip-aot ray-says.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
