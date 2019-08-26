(defproject hanoi "0.1.0-SNAPSHOT"
  :description "Towers of Hanoi recursive solution"
  :url "https://github.com/poverholt/hanoi"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [com.novemberain/monger "3.5.0"]]
  :main ^:skip-aot hanoi.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
