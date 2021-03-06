;; Full-text search powered by lucene.

(ns kwd-doc-find.search
  (:use [clojure.java.io])
  (:require [clucy.core :as clucy]
            [clojure-csv.core :as csv]
            [clj-file-utils.core :as fs]
            [clojure.contrib.str-utils2 :as str2]))

(defn get-index [& [index-dir]]
  (binding [clucy/*optimize-frequency* 200]
    (if (nil? index-dir)
      (clucy/memory-index)
      (clucy/disk-index index-dir))))

(defn clear-index [index-dir]
  (if (fs/exists? index-dir)
    (fs/rm-rf index-dir)))

(defn search-docs [index kwd max-results]
  "Search index for files with the given keyword."
  {:ids
   ;; Lucene does not support queries starting with wildcards
   (if (.contains [\* \?] (first kwd))
     []
     (map #(:id %)
          (clucy/search index kwd max-results :text)))})

(defn- file-lucene-map [id fname & [alt-fname]]
  "Prepare a clojure map with the contents of the file."
  (let [short-name (if (nil? alt-fname)
                     (last (str2/split fname #"/"))
                     alt-fname)
        full-text (if (fs/exists? fname) (slurp fname) "")]
    (-> {:id id
         :text (str2/join " " [short-name full-text])}
        (with-meta {:text {:stored false :indexed true}}))))

(defn index-doc-file [index docfile]
  "Add index information from a high level document file."
  (binding [clucy/*content* false]
    (with-open [rdr (reader docfile)]
      (doseq [info (map #(first (csv/parse-csv %)) (line-seq rdr))]
        (->> info
             (apply file-lucene-map)
             (clucy/add index))))))
