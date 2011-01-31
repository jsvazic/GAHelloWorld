(ns gahelloworld.core)

(def *target-gene* "Hello, world!")

(defn calc-fitness [gene]
  (let [len (count gene)]
    (loop [fitness 0 idx 0]
	  (if (== idx len)
	    fitness
	    (recur (+ fitness (Math/abs (- (int (nth gene idx)) (int (nth *target-gene* idx))))) (inc idx))))))
		
(defn rand-gene [len]
  (loop [col [] idx 0]
    (if (== idx len)
	  col
	  (recur (conj col (char (+ 32 (rand 90)))) (inc idx)))))
	  
(defn mutate-gene [gene]
  (let [len (count gene)
        r-idx (+ 1 (rand len))]
    (apply str ((doall (take (dec r-idx) gene)) (char (+ 32 (rand 90))) (doall (drop r-idx gene))))))
    