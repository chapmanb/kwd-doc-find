;; Tests for base searching capabilities using Lucene.
(ns search-test
  (:use [clojure.test]
        [kwd-doc-find.search]))

(defn- sorted-result-ids [index kwd]
  (-> (search-docs index kwd 10)
      (:ids)
      (sort)))

(deftest base-search
  (let [index (get-index)]
    (testing "Indexing a document file"
      (index-doc-file index "files/test/doc-file.txt"))
    (testing "Retrieval by keyword"
      (is (= '("1" "2") (sorted-result-ids index "gene12")))
      (is (= '("1") (sorted-result-ids index "gene34"))))
    (testing "Retrieval by filename"
      (is (= '("1") (sorted-result-ids index "o*.txt")))
      (is (= '("2") (sorted-result-ids index "t*.genelist"))))
    (testing "Ignore queries starting with wildcards"
      (is (= '() (sorted-result-ids index "*.txt"))))))

(deftest reset-index
  (let [index-dir "files/test/lucene-index"
        index (get-index index-dir)]
    (testing "Indexing and retrieval with file storage"
      (clear-index index-dir)
      (index-doc-file index "files/test/doc-file.txt")
      (is (= '("1" "2") (sorted-result-ids index "gene12"))))
    (testing "Re-indexing file storage with clearning"
      (clear-index index-dir)
      (index-doc-file index "files/test/doc-file2.txt")
      (is (= '("1") (sorted-result-ids index "gene12"))))
    (testing "Using an alternative filename"
      (is (= '("1") (sorted-result-ids index "alternate*")))
      (is (= '() (sorted-result-ids index "one*")))
      (is (= '("3") (sorted-result-ids index "onlyindex*"))))))
