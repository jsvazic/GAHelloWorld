(ns gahelloworld.population)

(def *tournament-size* 64)

(defn gen-population [pop-size crossover elitism mutation]
  (let [population {}]
    (do
      (assoc population :crossover crossover
	     :elitism elitism
	     :mutation mutation)
      (loop [col [] idx 0]
	(== pop-size idx) population
	(recur (conj col (
      )))
 
  