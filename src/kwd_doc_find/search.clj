;; Full-text search powered by lucene.
;;
;; Works off of a high level doc-file which consists of a comma separated file
;; of identifiers and files to parse.

(ns kwd-doc-find.search
  (:use [clojure.java.io])
  (:require [clucy.core :as clucy]
            [clj-file-utils.core :as fs]
            [clojure.contrib.str-utils2 :as str2]))

(defn get-index [& [index-dir]]
  (if (nil? index-dir)
    (clucy/memory-index)
    (clucy/disk-index index-dir)))

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

(defn- parse-doc-file [rdr]
  "Extract identifiers and filenames from the top level docfile."
  (for [line (line-seq rdr)]
    (-> line
        (str2/chomp)
        (str2/split #","))))

(defn- file-lucene-map [id fname]
  "Preparse a clojure map with the contents of the file."
  (-> {:id id
       :text (str2/join " "
                        [(last (str2/split fname #"/"))
                         (slurp fname)])}
      (with-meta {:text {:stored false :indexed true}})))

(defn index-doc-file [index docfile]
  "Add index information from a high level document file."
  (with-open [rdr (reader docfile)]
    (doseq [info (parse-doc-file rdr)]
      (->> info
           (apply file-lucene-map)
           (clucy/add index)))))
