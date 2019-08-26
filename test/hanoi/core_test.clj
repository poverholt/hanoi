(ns hanoi.core-test
  (:require [clojure.test :refer :all]
            [hanoi.core :refer :all]
            [monger.core :as mg]
            [clojure.string :as str]))

(defn get-towers
  []
  (let [conn (connect!)
        db (mg/get-db conn "Towers")
        towers (fetch-towers db)]
    (do (disconnect! conn)
        towers)))

(deftest connect-test
  (testing "MongoDb Atlas connect."
    (let [conn (connect!)]
      (is (= (str/includes? conn "Unbound") false)))))

(deftest fetch-towers-test
  (testing "Fetching towers from DB."
    (let [towers (get-towers)]
      (is (= (vector? towers) true))
      (is (= (count towers) 3))
      (is (= (every? vector? towers) true)))))

(deftest update-towers-test
  (testing "Fetching towers from DB."
    (let [conn (connect!)
          db (mg/get-db conn "Towers")
          test-towers [[][2 1][3]]]
      (do (update-towers! db test-towers)
          (is (= (get-towers) test-towers))))))

(deftest init-towers-test
  (testing "Fetching towers from DB."
    (let [conn (connect!)
          db (mg/get-db conn "Towers")]
      (do (init-towers! db 0 false) (is (= (get-towers) [[][][]])))
      (do (init-towers! db 1 false) (is (= (get-towers) [[1][][]])))
      (do (init-towers! db 2 false) (is (= (get-towers) [[2 1][][]])))
      (do (init-towers! db 0 true) (is (= (get-towers) [[][][]])))
      (do (init-towers! db 1 true) (is (= (get-towers) [[][][1]])))
      (do (init-towers! db 2 true) (is (= (get-towers) [[][][2 1]]))))))

(deftest play-test
  (testing "Test various plays."
    (do 
      (play 0) (is (= (get-towers) [[][][]]) "play 0 failed.")
      (play 1) (is (= (get-towers) [[][][1]]) "play 1 failed.")
      (play 4) (is (= (get-towers) [[][][4 3 2 1]]) "play 4 failed.")
      (play 0 {:reverse true}) (is (= (get-towers) [[][][]]) "play 0 reverse failed.")
      (play 1 {:reverse true}) (is (= (get-towers) [[1][][]]) "play 1 reverse failed.")
      (play 4 {:reverse true}) (is (= (get-towers) [[4 3 2 1][][]]) "play 4 reverse failed."))))

