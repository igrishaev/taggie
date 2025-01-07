(ns taggie.core-test
  (:import
   (java.sql Timestamp)
   (java.time Instant
              LocalDate)
   (java.io File)
   (java.util Date)
   (java.net URL URI))
  (:require
   [clojure.string :as str]
   [clojure.test :refer [deftest is testing]]
   [taggie.core :as tag]))

(deftest test-write-io

  (let [res1
        (binding [*print-dup* true]
          (pr-str (new File "test")))

        res2
        (binding [*print-dup* false]
          (pr-str (new File "test")))]

    (is (= "#File \"test\""
           res1
           res2))))

(deftest test-write-net

  (let [res1
        (binding [*print-dup* true]
          (pr-str (new URL "http://test.com")))

        res2
        (binding [*print-dup* false]
          (pr-str (new URL "http://test.com")))]

    (is (= "#URL \"http://test.com\""
           res1
           res2))))

(deftest test-write-java-time

  (let [string "2025-01-06T14:03:23.819994Z"]
    (is (= (format "#Instant \"%s\"" string)
           (pr-str (Instant/parse string)))))

  (let [string "2025-01-06"]
    (is (= (format "#LocalDate \"%s\"" string)
           (pr-str (LocalDate/parse string))))))


(deftest test-write-util

  (is (= "#regex \"some regex\""
         (pr-str #"some regex")))

  (let [line "2025-01-06T14:03:23.819Z"]
    (is (= (format "#Date \"%s\"" line)
           (-> line
               Instant/parse
               Date/from
               pr-str)))))


(deftest test-write-sql
  (let [line "2025-01-06T14:03:23.819Z"]
    (is (= (format "#sql.Timestamp \"%s\"" line)
           (-> line Instant/parse Timestamp/from pr-str)))))


(deftest test-write-clojure

  (is (= "#atom #LocalDate \"2024-11-29\""
         (pr-str (atom (LocalDate/parse "2024-11-29")))))

  (is (= "#ref #LocalDate \"2024-11-29\""
         (pr-str (ref (LocalDate/parse "2024-11-29"))))))


(deftest test-write-read-edn-file

  (let [file (File/createTempFile "tmp" ".edn")
        data1 {:aaa (LocalDate/parse "2023-02-23")
               :bbb ['a 'b (atom [1 2 3 #"rEgEx"])]
               :ccc (new File "hello")}

        _
        (tag/write file data1)

        content
        (slurp file)

        data2
        (tag/read file)]

    (is (= "{:aaa #LocalDate \"2023-02-23\",
 :bbb [a b #atom [1 2 3 #regex \"rEgEx\"]],
 :ccc #File \"hello\"}
"
           content))

    (is (= (str #"rEgEx")
           (-> data2 :bbb (get 2) deref (get 3) str)))

    (is (= (-> data1
               (update-in [:bbb 2] swap! pop))
           (-> data2
               (update-in [:bbb 2] swap! pop))))))


(defn arr= [arr1 arr2]
  (is (= (vec arr1) (vec arr2))))


(deftest test-arrays

  ;; (testing "booleans"
  ;;   (is (= "#booleans [false, false, false]"
  ;;          (str/trim (tag/write-string (boolean-array 3)))))
  ;;   (is (arr= (boolean-array 3)
  ;;             #booleans [false, false, false])))

  (testing "bytes"
    (let [line
          (str/trim (tag/write-string (byte-array 64)))]
      (is (= "#bytes [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0]"
             line)))
    (is (arr= (byte-array [1 2 3])
              #bytes [1 2 3])))

  (testing "chars"
    (is (= "#chars [\\a, \\b, \\c]"
           (str/trim (tag/write-string (char-array [\a \b \c])))))

    (is (= 1 #chars [\a \b \c]))

    #_
    (is (arr= (char-array [\a \b \c])
              #chars [\a \b \c]))

    )



  )


(deftest test-write-read-edn-string

  (let [data1 {:aaa (LocalDate/parse "2023-02-23")
               :bbb ['a 'b (atom [1 2 3 (ref {:test 1})])]
               :ccc (new File "hello")}

        content
        (tag/write-string data1)

        data2
        (tag/read-string content)]

    (is (= "{:aaa #LocalDate \"2023-02-23\",
 :bbb [a b #atom [1 2 3 #regex \"rEgEx\"]],
 :ccc #File \"hello\"}
"
           content))

    (is (= (-> data1
               (update-in [:bbb 2] swap! pop))
           (-> data2
               (update-in [:bbb 2] swap! pop))))))
