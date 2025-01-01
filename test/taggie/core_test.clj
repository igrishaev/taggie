(ns taggie.core-test
  (:import
   (java.io File))
  (:require
   [clojure.test :refer [deftest is]]
   [taggie.core :as t]))

;; test dup on/off

(deftest test-io
  (let [res (pr-str (new File "test"))]
    (is (= "#File \"test\""
           res))
    ))
