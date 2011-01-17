;; Tests for base searching capabilities using Lucene.
(ns search-test
  (:use [clojure.test]
        [kwd-doc-find.search]))

(deftest base-search
  (let [index (get-index)]
    (testing "Indexing a document file"
      (index-doc-file index "files/test/doc-file.txt"))
    (testing "Retrieval by keyword"
      (is (= '("1" "2")
             (-> (search-docs index "gene12" 10)
                 (:ids)
                 (sort))))
      (is (= '("1")
             (-> (search-docs index "gene34" 10)
                 (:ids)
                 (sort)))))
    (testing "Retrieval by filename"
      (is (= '("1")
             (-> (search-docs index "o*.txt" 10)
                 (:ids)
                 (sort))))
      (is (= '("2")
             (-> (search-docs index "t*.genelist" 10)
                 (:ids)
                 (sort)))))
    (testing "Ignore queries starting with wildcards"
      (is (= '()
             (-> (search-docs index "*.txt" 10)
                 (:ids)))))))

(deftest reset-index
  (let [index-dir "files/test/lucene-index"
        index (get-index index-dir)]
    (testing "Indexing and retrieval with file storage"
      (clear-index index-dir)
      (index-doc-file index "files/test/doc-file.txt")
      (is (= '("1" "2")
             (-> (search-docs index "gene12" 10)
                 (:ids)
                 (sort)))))
    (testing "Re-indexing file storage with clearning"
      (clear-index index-dir)
      (index-doc-file index "files/test/doc-file2.txt")
      (is (= '("1")
             (-> (search-docs index "gene12" 10)
                 (:ids)
                 (sort)))))))
