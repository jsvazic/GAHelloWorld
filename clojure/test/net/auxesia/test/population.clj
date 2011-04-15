;; The MIT License
;; 
;; Copyright (c) 2011 John Svazic
;; 
;; Permission is hereby granted, free of charge, to any person obtaining a copy
;; of this software and associated documentation files (the "Software"), to deal
;; in the Software without restriction, including without limitation the rights
;; to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
;; copies of the Software, and to permit persons to whom the Software is
;; furnished to do so, subject to the following conditions:
;; 
;; The above copyright notice and this permission notice shall be included in
;; all copies or substantial portions of the Software.
;; 
;; THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
;; IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
;; FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL 
;; AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
;; LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
;; OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
;; THE SOFTWARE.

(ns net.auxesia.test.population
  (:use [net.auxesia.population :as population] :reload)
  (:use [clojure.test]))

(deftest test-crossover
  (testing "crossover property"
    (let [p1 (population/generate 1024 0.8 0.1 0.05)
		  p2 (population/generate 1024 0.0 0.1 0.05)
		  p3 (population/generate 1024 1.0 0.1 0.05)]
	  (is (== 80 (int (* 100 (:crossover p1)))))
	  (is (== 0 (int (* 100 (:crossover p2)))))
	  (is (== 100 (int (* 100 (:crossover p3))))))))

(deftest test-elitism
  (testing "elitism property"
    (let [p1 (population/generate 1024 0.8 0.1 0.05)
		  p2 (population/generate 1024 0.8 0.0 0.05)
		  p3 (population/generate 1024 0.8 0.99 0.05)]
	  (is (== 10 (int (* 100 (:elitism p1)))))
	  (is (== 0 (int (* 100 (:elitism p2)))))
	  (is (== 99 (int (* 100 (:elitism p3))))))))
	  
(deftest test-mutation
  (testing "mutation property"
    (let [p1 (population/generate 1024 0.8 0.1 0.05)
		  p2 (population/generate 1024 0.8 0.1 0.0)
		  p3 (population/generate 1024 0.8 0.1 1.0)]
	  (is (== 5 (int (* 100 (:mutation p1)))))
	  (is (== 0 (int (* 100 (:mutation p2)))))
	  (is (== 100 (int (* 100 (:mutation p3))))))))

(deftest test-population
  (testing "population property"
    (let [p (population/generate 1024 0.8 0.1 0.5)
		  c (vec (sort-by #(:fitness %) (:population p)))]
	  (testing "population size"
	    (is (== 1024 (count (:population p))))
		(is (== 1024 (count c))))
	  (testing "The population is actually sorted"
	    (loop [idx (int 0)]
		  (when (< (count c) idx)
		    (do
			  (is (= (get c idx) (get (:population p) idx)))
			  (recur (inc idx)))))))))
	
(deftest test-evolve
  (testing "evolve function"
    (let [p1 (population/generate 1024 0.8 0.1 0.05)
	      p2 (population/evolve p1)
		  elitism (int (Math/round (* (count (:population p1)) (:elitism p1))))]
	  (testing "to ensure the population properties were carried through an evolution"
	    (is (== (:crossover p1) (:crossover p2)))
		(is (== (:elitism p1) (:elitism p2)))
		(is (== (:mutation p1) (:mutation p2)))
		(is (== (count (:population p1)) (count (:population p2)))))
	  (testing "to ensure the proper elitism took place"
	    ;; Store the values for p2 into a map
		(let [elitism-map (zipmap (:population p2) (repeat (count (:population p2)) 1))]
		  (is (<= elitism (count (doall (filter #(contains? elitism-map %) (:population p1))))))
		  (is (< (count (doall (filter #(contains? elitism-map %) (:population p1)))) (count (:population p1)))))))))