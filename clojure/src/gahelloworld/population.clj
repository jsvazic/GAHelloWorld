(ns gahelloworld.population)
(require '[gahelloworld.chromosome :as chromosome])

(def *tournament-size* 64)

(defn gen-population [pop-size crossover elitism mutation]
  (let [chromosomes (sort-by
		     (fn [x] (:fitness x))
		     (vec (repeatedly pop-size chromosome/generate)))]
    (hash-map :crossover crossover
	      :elitism elitism
	      :mutation mutation
	      :population chromosomes)))

(defn- tournament-selection [population]
  (let [pop-size (count (:population population))]
    (loop [best (nth (:population population) (rand-int pop-size))
	   i 0]
      (if (= i *tournament-size*)
	best
	(let [contender (nth (:population population) (rand-int pop-size))
	      new-best (fn [x y] (if (<= (:fitness x) (:fitness y)) x y)
			best contender)]
	  (recur new-best (inc i)))))))

(defn evolve [population]
  (let [chromosomes (:population population)
	elitisim (:elitism population)
	mutation (:mutation population)
	crossover (:crossover population)
        pop-size (count chromosomes)
	elitism-count (int (Math/round (* elitisim pop-size)))
	tmp (take elitism-count chromosomes)]
    (loop [buffer tmp idx (inc elitism-count)]
      (if (>= pop-size (count buffer))
	(assoc population :population
	       (sort-by (fn [x] (:fitness x)) (vec (take pop-size buffer))))
	(if (<= crossover (rand))
	  (let [c1 (tournament-selection chromosomes)
		c2 (tournament-selection chromosomes)
		children (into (chromosome/mate c1 c2)
			       (chromosome/mate c2 c1))]
	    ) ;; Perform a crossover operation
	  (if (<= mutation (rand))
	    (recur (conj buffer (chromosome/mutate (nth chromosomes idx)))
		   (inc idx))
	    (recur (conj buffer (nth chromosomes idx))
		   (inc idx))))))))
    