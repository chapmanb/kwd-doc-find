(defproject kwd-doc-find "0.1-SNAPSHOT"
  :description "Server access for full-text search of documents with keywords."
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [compojure "0.5.3"]
                 [ring/ring-jetty-adapter "0.3.5"]
                 [ring-json-params "0.1.3"]
                 [clj-json "0.3.1"]
                 [org.clojars.chapmanb/clucy "0.2.0-SNAPSHOT"]
                 [clojure-csv "1.2.2"]
                 [clj-file-utils "0.2.1"]]
  :dev-dependencies [[swank-clojure "1.2.1"]]
  :run-aliases {:web kwd-doc-find.web})
