(ns gahelloworld.chromosome)

(def *target-gene* (into [] "Hello, world!"))

(defn calc-fitness [gene]
  (let [len (count gene)]
    (loop [fitness 0 idx 0]
	  (if (== idx len)
	    fitness
	    (recur (+ fitness (Math/abs (- (int (nth gene idx))
					   (int (nth *target-gene* idx)))))
		   (inc idx))))))

(defn rand-char []
  (char (+ 32 (rand 90))))

(defn rand-gene [len]
  (loop [col [] idx 0]
    (if (== idx len)
	  col
	  (recur (conj col (rand-char)) (inc idx)))))
	  
(defn mutate-gene [gene]
  (let [idx (rand (count gene))]
    (assoc gene idx (mod (+ (int (nth gene idx)) (rand-char)) 122))))

(defn mate [gene1 gene2]
  (let [pivot (rand (count gene1))]
    (vector
	  (into (drop pivot gene2) (reverse (take pivot gene1)))
	  (into (drop pivot gene1) (reverse (take pivot gene2))))))

(defn gen-chromosome []
  (let [gene (rand-gene (count *target-gene*))
	chromosome {}]
    (assoc chromosome :gene gene :fitness (calc-fitness gene))))