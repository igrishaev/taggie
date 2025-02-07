(ns taggie.core-test
  (:import
   (java.sql Timestamp)
   (java.time Instant
              Duration
              LocalDate
              LocalDateTime)
   (clojure.lang Atom Ref)
   (java.sql Timestamp)
   (java.io File)
   (java.nio ByteBuffer)
   (java.util.regex Pattern)
   (java.util Date)
   (java.net URL URI))
  (:require
   [clojure.pprint :as pprint]
   [clojure.java.io :as io]
   [clojure.string :as str]
   [clojure.test :refer [deftest is testing]]
   [taggie.edn :as edn]
   [taggie.core :as tag]))

(defn arr= [arr1 arr2]
  (is (= (vec arr1) (vec arr2))))

(defn re= [re1 re2]
  (is (instance? Pattern re1))
  (is (instance? Pattern re2))
  (is (= (str re1) (str re2))))

(defn atom= [a1 a2]
  (is (instance? Atom a1))
  (is (instance? Atom a2))
  (is (= @a1 @a2)))

(defn ref= [r1 r2]
  (is (instance? Ref r1))
  (is (instance? Ref r2))
  (is (= @r1 @r2)))

(defn str= [o1 o2]
  (is (= (class o1) (class o1)))
  (is (= (str o1) (str o2))))

(defn bb= [^ByteBuffer bb1 ^ByteBuffer bb2]
  (is (instance? ByteBuffer bb1))
  (is (instance? ByteBuffer bb2))
  (is (= (-> bb1 .array vec)
         (-> bb2 .array vec))))

(defn objects= [^objects arr1 ^objects arr2]
  (and (= (alength arr1)
          (alength arr2))
       (every? true?
               (for [[i1 i2]
                     (map vector arr1 arr2)]
                 (cond
                   (instance? Atom i1)
                   (atom= i1 i2)
                   :else
                   (is (= i1 i2)))))))

(defn validate
  ([data repr]
   (validate data repr =))

  ([data repr fn=]

   (is (fn= data
            (eval
             (read-string
              (with-out-str
                (pprint/pprint data))))))

   (is (fn= data
            (eval
             (read-string
              (edn/write-string data)))))

   (is (fn= data
            (eval (edn/read-string repr))))

   (is (= repr
          (str/trim (pr-str data))))

   (is (fn= data
            (eval (read-string (format "(do (do (do %s)))" repr)))))))

(deftest test-io
  (validate (io/file "test")
            "#File \"test\""))

(deftest test-write-net
  (validate (new URL "http://test.com")
            "#URL \"http://test.com\"")
  (validate (new URI "http://test.com")
            "#URI \"http://test.com\""))

(deftest test-write-java-time
  (validate (Instant/parse "2025-01-06T14:03:23.819994Z")
            "#Instant \"2025-01-06T14:03:23.819994Z\"")

  (validate (Duration/parse "PT72H")
            "#Duration \"PT72H\"")

  (validate (LocalDate/parse "2034-01-30")
            "#LocalDate \"2034-01-30\"")

  (validate (LocalDateTime/parse "2025-01-08T11:08:13.232516")
            "#LocalDateTime \"2025-01-08T11:08:13.232516\"")

  ;; TODO

)


(deftest test-util
  (validate #"some regex"
            "#regex \"some regex\""
            re=)

  (validate (-> "2025-01-06T14:03:23.819Z"
                Instant/parse
                Date/from)
            "#Date \"2025-01-06T14:03:23.819Z\"")

  (validate (ByteBuffer/wrap (byte-array [1 2 3]))
            "#ByteBuffer [1 2 3]"
            bb=))


(deftest test-sql
  (validate (-> "2025-01-06T14:03:23.819Z"
                Instant/parse
                Timestamp/from)
            "#sql/Timestamp \"2025-01-06T14:03:23.819Z\""
            str=))


(deftest test-arrays

  (testing "booleans"
    (validate (boolean-array 3)
              "#booleans [false false false]"
              arr=))

  (testing "bytes"
    (validate (byte-array [1 2 3])
              "#bytes [1 2 3]"
              arr=))

  (testing "chars"
    (validate (char-array [\a \b \c])
              "#chars [\\a \\b \\c]"
              arr=))

  (testing "doubles"
    (validate (double-array [1 2 3])
              "#doubles [1.0 2.0 3.0]"
              arr=))

  (testing "floats"
    (validate (float-array [1 2 3])
              "#floats [1.0 2.0 3.0]"
              arr=))

  (testing "ints"
    (validate (int-array [1 2 3])
              "#ints [1 2 3]"
              arr=))

  (testing "longs"
    (validate (long-array [1 2 3])
              "#longs [1 2 3]"
              arr=))

  (validate (object-array [1 true {:foo 1} (atom 42)])
            "#objects [1 true {:foo 1} #atom 42]"
            objects=))


(deftest test-clojure

  (validate (atom 1)
            "#atom 1"
            atom=)

  (let [a (eval (read-string "#atom #atom #atom 42"))]
    (is (= 42 @@@a)))

  (let [a (atom (atom (atom (byte-array [1 2 3]))))]
    (is (= "#atom #atom #atom #bytes [1 2 3]"
           (pr-str a))))

  (validate (ref 1)
            "#ref 1"
            ref=))

(deftest test-edn-file

  (let [file (File/createTempFile "tmp" ".edn")
        data1 {:aaa (LocalDate/parse "2023-02-23")
               :bbb ['a 'b (atom [1 2 3 #"rEgEx"])]
               :ccc (new File "hello")}

        _
        (edn/write file data1)

        content
        (slurp file)

        data2
        (edn/read file)]

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


(deftest test-edn-string

  (let [data1 {:aaa (LocalDate/parse "2023-02-23")
               :bbb ['a 'b (atom [1 2 3 (ref {:test 1})])]
               :ccc (new File "hello")}

        content
        (edn/write-string data1)

        data2
        (edn/read-string content)]

    (is (= "{:aaa #LocalDate \"2023-02-23\",
 :bbb [a b #atom [1 2 3 #ref {:test 1}]],
 :ccc #File \"hello\"}
"
           content))

    (is (= (-> data1
               (update-in [:bbb 2] swap! pop))
           (-> data2
               (update-in [:bbb 2] swap! pop))))))
