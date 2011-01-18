;; Web server and high level routing for requests.

(ns kwd-doc-find.web
  (:use [compojure.core]
        [ring.adapter.jetty]
        [ring.middleware.json-params]
        [kwd-doc-find.search])
  (:require [clj-json.core :as json]))

(defn json-response [data & [status]]
  {:status (or status 200)
   :headers {"Content-Type" "application/json"}
   :body (json/generate-string data)})

(def index-dir "lucene-index")

(defn web-find [kwd & [max]]
  (let [index (get-index index-dir)]
    (json-response
     (search-docs index kwd (Integer/parseInt (or max "1000"))))))

(defn web-index [docfile]
  (let [index (get-index index-dir)]
    (clear-index index-dir)
    (index-doc-file index docfile))
  (json-response {:docfile docfile}))

(defroutes kwd-doc-find-routes
  (GET "/find" [kwd max] (web-find kwd max))
  (PUT "/index" [docfile] (web-index docfile)))

(def kwd-doc-find-app
  (wrap-json-params kwd-doc-find-routes))

;; Entry point for running scripts.
(defn -main [& [port]]
  (run-jetty #'kwd-doc-find-app
             {:port (Integer/parseInt (or port "8081"))}))
