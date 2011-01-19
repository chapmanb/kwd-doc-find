Clojure web server providing full-text document searching via Lucene

## Overview

This is a small web server which provides indexing and full-text keyword
searching for documents using [Lucene][0]. It is intended as a plugin server
for searching [data libraries in the Galaxy biological web framework][1]
but is meant to be general enough to use in other contexts.

## Setup

* Install [the leiningen build tool][2] for Clojure.
* Clone this repository: `git clone https://github.com/chapmanb/kwd-doc-find.git`
* Install dependencies: `lein deps`
* Run the web server: `lein run :web 8081`

## Usage

This provides a simple REST-style interface for building an index and
querying. The main input for index building is a document file; this
comma separated file contains two required fields, an identifier and a
path to the file. An optional third field can be specified with the
filename and additional text to use for searching; if not specified
the base name of the full file path will be used. The second file
field can also be left blank to search only the additional text,
instead of file contents:

        1,/your/home/file/path/toindex.txt
        2,/your/home/file/path/alsoindex.txt
        3,/your/home/file/path/dataset123.dat,real_file_name.txt
        4,,only_index_the_name.txt

The full text files and names are indexed, allowing retrieval by
either in the search.

Index and search with PUT and GET URLs from the commandline or any
programming language:

        curl -X PUT 'http://localhost:8081/index?docfile=files/test/doc-file.txt'
        {"docfile":"files/test/doc-file.txt"}

        curl -X GET 'http://localhost:8081/find?kwd=gene12&max=100'
        {"ids":["1","2"]}

[0]: http://lucene.apache.org/java/docs/index.html
[1]: https://bitbucket.org/galaxy/galaxy-central/wiki/DataLibraries/Libraries
[2]: https://github.com/technomancy/leiningen
