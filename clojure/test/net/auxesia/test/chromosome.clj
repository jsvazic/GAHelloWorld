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

(ns net.auxesia.test.chromosome
  (:use [net.auxesia.chromosome :as chromosome] :reload)
  (:use [clojure.test])
  (:use [clojure.set]))

(deftest test-fitness
  (testing "fitness function"
    (testing "Perfect fitness"
      (is (= 0 (:fitness (chromosome/generate "Hello, world!")))))
	(testing "Random genes"
      (is (= 399 (:fitness (chromosome/generate "H5p&J;!l<X\\7l"))))
      (is (= 297 (:fitness (chromosome/generate "Vc;fx#QRP8V\\$"))))
      (is (= 415 (:fitness (chromosome/generate "t\\O`E_Jx$n=NF")))))))

(deftest test-generate
  (testing "generate function"
    (loop [c (chromosome/generate) idx (int 0)]
      (if (== idx 1000)
	    true
        (do
          (is (>= (:fitness c) 0))
	      (is (== (count chromosome/*target-gene*) (count (:gene c))))
		  (let [gene (:gene c)]
		    (loop [g-idx (int 0)]
		      (if (== g-idx (count gene))
		        true
			    (if (and (>= (int (get gene g-idx)) 32) (<= (int (get gene g-idx)) 121))
			      (recur (inc g-idx))
				  (is (false "Invalid character found!"))))))
	      (recur (chromosome/generate) (inc idx)))))))

(deftest test-mutate
  (testing "mutate function"
    (loop [c (chromosome/generate) idx (int 0)]
      (if (== idx 1000)
	    true
	    (let [mutated (chromosome/mutate c)
	          set1 (apply sorted-set (:gene c))
			  set2 (apply sorted-set (:gene mutated))]
          (do
		    (testing "Gene size for mutated chromosome"
		      (is (== (count (:gene c)) (count (:gene mutated)))))
			(testing "Difference count for mutated gene"
		      (is (<= (count (clojure.set/difference set1 set2)) 1)))
		    (recur (chromosome/generate) (inc idx))))))))

(defn- find-pivot 
  "Helper method used to find a pivot point between two strings."
  [s1 s2]
  (let [size (count s1)]
    (loop [idx (int 0)]
      (cond
        (== size idx) nil
        (not= (get s1 idx) (get s2 idx)) idx
        :default (recur (inc idx))))))

(deftest test-mate
  (testing "The mate function"
    (let [c1 (chromosome/generate)
          c2 (chromosome/generate)
		  children (chromosome/mate c1 c2)
		  pivot (find-pivot (:gene c1) (:gene (first children)))]
      (do
	    (testing "Size of returned sequence from the mate function"
	      (is (== 2 (count children))))
	    (testing "Size of the gene from the first child"
	      (is (== (count chromosome/*target-gene*) (count (:gene (first children))))))
	    (testing "Size of the gene from the second child"
	      (is (== (count chromosome/*target-gene*) (count (:gene (last children))))))
	    (testing "Mating results from the first child"
	      (loop [g-idx 0] ; Check the first child
	        (when (< g-idx (count (:gene c1)))
		      (if (< g-idx pivot)
		        (is (= (get (:gene c1) g-idx) (get (:gene (first children)) g-idx)))
			    (is (= (get (:gene c2) g-idx) (get (:gene (first children)) g-idx))))
		      (recur (inc g-idx)))))
	    (testing "Mating results from the second child"
	      (loop [g-idx 0] ; Check the second child
	        (when (< g-idx (count (:gene c2)))
		      (if (< g-idx pivot)
		        (is (= (get (:gene c2) g-idx) (get (:gene (second children)) g-idx)))
			    (is (= (get (:gene c1) g-idx) (get (:gene (second children)) g-idx))))
		      (recur (inc g-idx)))))))))