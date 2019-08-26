(ns hanoi.core
  "Shows Towers of Hanoi solution for variable number of rings. See function play for argument details.
   The algorithm is shown in Recursive Implementation section of https://en.wikipedia.org/wiki/Tower_of_Hanoi.
   The towers are represented as a vector of 3 ring stack vectors.
  
   The data is stored in a MongoDb Atlas project named Hanoi. The DB is named Towers.
   MongoDB Connection String...
   mongodb+srv://PjoMongoDbUser:<password>@towers-niikl.mongodb.net/test?retryWrites=true&w=majority
     where <password> is pwdHanoi"
  (:gen-class)
  (:require [monger.core :as mg]
            [monger.credentials :as mcr]
            [monger.collection :as mc])
  (:import [com.mongodb MongoOptions ServerAddress]
           [org.bson.types ObjectId]))

(def debug false)
(def mongodb-uri "mongodb+srv://PjoMongoDbUser:pwdHanoi@towers-niikl.mongodb.net/test?retryWrites=true&w=majority")
(def oid (ObjectId. "5d630e8a83973a49e8bfeb9c")) ; id for towers in DB.
(def padding "     ")

(defn connect! [] (:conn (mg/connect-via-uri mongodb-uri)))

(defn disconnect! [conn] (mg/disconnect conn))

(defn fetch-towers
  [db]
  (:all_towers (mc/find-map-by-id db "documents" oid)))

(defn update-towers!
  [db towers]
  (mc/update db "documents" {} { :_id oid :all_towers towers }))

(defn init-towers!
  "Reset db with all rings on tower 0 or, if reverse?, tower 2."
  [db num-rings reverse?]
  (let [init-tower (vec (range num-rings 0 -1))]
    (if reverse?
      (update-towers! db [[] [] init-tower])
      (update-towers! db [init-tower [] []]))))

(defn move
  "Move n disks from source tower to target tower, intermediate steps using auxiliary tower.
   Algorithm is shown in Recursive Implementation section of https://en.wikipedia.org/wiki/Tower_of_Hanoi."
  [db n source target auxiliary]
  (when (> n 0)
    (move db (dec n) source auxiliary target)

    (do
      (when debug (print (str "Towers after fetch: " (fetch-towers db) "." padding)))
      (let [instructions (str "Move ring " n " from " source " to " target "." padding)]
        (if debug (print instructions) (println instructions)))
      (update-towers! db (-> (fetch-towers db)
                             (update source pop)
                             (update target conj n))))
      (when debug (println (str "Done. Towers just set to db is now: " (fetch-towers db) ".")))

    (move db (dec n) auxiliary target source)))

(defn play
  "Shows Towers of Hanoi solution for variable number of rings.
   To move from right to left, use option {:reverse true}."
  [num-rings & options]
  (let [conn (connect!)
        db (mg/get-db conn "Towers")
        reverse? (:reverse (first options))]
    (if reverse?
      (do (init-towers! db num-rings reverse?) (move db num-rings 2 0 1))
      (do (init-towers! db num-rings reverse?) (move db num-rings 0 2 1)))
    (disconnect! conn)))

(defn -main
  "Shows example call."
  []
  (play 3 {:reverse true}))

