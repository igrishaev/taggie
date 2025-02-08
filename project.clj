(defproject com.github.igrishaev/taggie "0.1.0"

  :description
  "Can we gain anything from Clojure tags?"

  :url
  "https://github.com/igrishaev/taggie"

  :deploy-repositories
  {"releases"
   {:url "https://repo.clojars.org"
    :creds :gpg}
   "snapshots"
   {:url "https://repo.clojars.org"
    :creds :gpg}}

  :license
  {:name "The Unlicense"
   :url "https://choosealicense.com/licenses/unlicense/"}

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

  :dependencies
  [[org.clojure/clojure "1.10.3" :scope "provided"]])
