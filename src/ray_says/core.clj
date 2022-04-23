(ns ray-says.core
  (:gen-class)
  (:require [clojure.string :as str]))

(defn gen-table
  [input]
  (->> input
       (partition 2 1)
       (reduce
        (fn [m [k v]]
          (assoc! m k (if (get m k)
                        (assoc (get m k) v (if (get (get m k) v)
                                             (inc (get (get m k) v))
                                             1))
                        {v 1}
                        )))
               (transient {}))
       (persistent!)))

(defn choice
  [m]
  (let [ks (keys m)]
    (->> (zipmap ks (range (count ks)))
         (reduce (fn [weighted-pool [k n]]
                (conj! weighted-pool (repeat (get m k) n)))
              (transient []))
         (persistent!)
         (flatten)
         (rand-nth)
         (nth ks))))

(def words (str/split (slurp (clojure.java.io/resource "ray_sentences.txt")) #" |\n"))
(def mem-gen-table (memoize gen-table))

(defn make-sentence
  ([]
   (make-sentence
    (->> (mem-gen-table words)
         (keys)
         (rand-nth)
         (get (mem-gen-table words))
         (keys)
         (first))))
  ([start]
  (let [table (mem-gen-table words)
        init-word (if (get table start)
                    (get table start)
                    (->> table
                       (keys)
                       (rand-nth)
                       (get table)))]
    (->> (loop [follow init-word
                sentence (transient [])]
           (let [new-word (choice follow)]
             (if (str/includes? new-word ".")
               (conj! sentence new-word)
               (recur (get table new-word) (conj! sentence new-word)))))
         (persistent!)
         (reduce #(str %1 " " %2))
         (str start " ")))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println (if (first args)
             (make-sentence (first args))
             (make-sentence))))
