(defproject com.github.igrishaev/taggie "0.1.0-SNAPSHOT"

  :description
  "Tags, tags are everywhere (c)"

  :url
  "https://github.com/igrishaev/taggie"

  :deploy-repositories
  {"releases"
   {:url "https://repo.clojars.org"
    :creds :gpg}
   "snapshots"
   {:url "https://repo.clojars.org"
    :creds :gpg}}

  :release-tasks
  [["vcs" "assert-committed"]
   ["test"]
   ["change" "version" "leiningen.release/bump-version" "release"]
   ["vcs" "commit"]
   ["vcs" "tag" "--no-sign"]
   ["deploy"]
   ["change" "version" "leiningen.release/bump-version"]
   ["vcs" "commit"]
   ["vcs" "push"]]

  :license UNLICENSE

  :dependencies
  [[org.clojure/clojure "1.10.3" :scope "provided"]])
