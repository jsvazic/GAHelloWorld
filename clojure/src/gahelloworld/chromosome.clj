(ns gahelloworld.chromosome)

(def *target-str* (vec "Hello, world!"))

(defn- fitness [str]
  (let [len (count str)]
    (loop [fitness 0 idx 0]
	  (if (== idx len)
	    fitness
	    (recur (+ fitness (Math/abs (- (int (nth str idx))
					   (int (nth *target-str* idx)))))
		   (inc idx))))))

(defn- rand-gene [len]
  (vec (repeatedly len (fn [] (char (+ 32 (rand-int 90)))))))
	  
(defn mutate [c]
  (let [gene (:gene c)
	idx (rand-int (count gene))]
    (assoc c :gene (assoc gene idx
			  (char (mod (+ (int (nth gene idx))
					(+ 32 (rand-int 90)))
				     122))))))

(defn mate [c1 c2]
  (let [gene1 (:gene c1)
	gene2 (:gene c2)
	pivot (rand-int (count gene1))
	child1 (into (drop pivot gene2) (reverse (take pivot gene1)))
	child2 (into (drop pivot gene1) (reverse (take pivot gene2)))]
    (vector
     (hash-map :gene child1 :fitness (fitness child1))
     (hash-map :gene child2 :fitness (fitness child2)))))

(defn generate
  ([]
     (let [gene (rand-gene (count *target-str*))]
       (generate gene)))
  ([gene]
     (hash-map :gene gene :fitness (fitness gene))))