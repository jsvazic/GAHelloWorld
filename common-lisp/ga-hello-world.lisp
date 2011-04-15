;;;; The MIT License
;;;;
;;;; Copyright (c) 2011 John Svazic, Erik Winkels
;;;;
;;;; Permission is hereby granted, free of charge, to any person
;;;; obtaining a copy of this software and associated documentation
;;;; files (the "Software"), to deal in the Software without
;;;; restriction, including without limitation the rights to use,
;;;; copy, modify, merge, publish, distribute, sublicense, and/or sell
;;;; copies of the Software, and to permit persons to whom the
;;;; Software is furnished to do so, subject to the following
;;;; conditions:
;;;;
;;;; The above copyright notice and this permission notice shall be
;;;; included in all copies or substantial portions of the Software.
;;;;
;;;; THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
;;;; EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
;;;; OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
;;;; NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
;;;; HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
;;;; WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
;;;; FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
;;;; OTHER DEALINGS IN THE SOFTWARE.

;;;; author: Erik Winkels <aerique@xs4all.nl>
;;;;
;;;; See README.md for documentation.

;;; Generics and Stubs
;;;
;;; These are just to make SBCL shut up and have been added after all coding
;;; had been finished.

(defgeneric evolve (p))
(defgeneric mate (c1 c2))
(defgeneric mutate (c))
(defgeneric tournament-selection (p))

(defun random-gene (length) (declare (ignore length)))


;;; Classes

(defclass chromosome ()
  ((target :reader target :initarg :target :initform "Hello, world!")
   (gene :reader gene :initarg :gene :initform (random-gene 13))
   (fitness :reader fitness :initarg :fitness :initform 1))
  (:documentation "This class is used to define a chromosome for the genetic algorithm
  simulation.
  This class is essentially nothing more than a container for the details
  of the chromosome, namely the gene (the string that represents our
  target string) and the fitness (how close the gene is to the target
  string).
  Calling MATE or MUTATE will result in a new chromosome instance being
  created."))


(defclass population ()
  ((size :reader size :initarg :size :initform 1024)
   (tournament-size :reader tournament-size :initarg :tournament-size
                    :initform 3)
   (crossover :reader crossover :initarg :crossover :initform 0.8)
   (elitism :reader elitism :initarg :elitism :initform 0.1)
   (mutation :reader mutation :initarg :mutation :initform 0.03)
   (chromosomes :accessor chromosomes :initarg :chromosomes :initform nil))
  (:documentation "A class representing a population for a genetic algorithm simulation.
  A population is simply a sorted collection of chromosomes
  (sorted by fitness) that has a convenience method for evolution.  This
  implementation of a population uses a tournament selection algorithm for
  selecting parents for crossover during each generation's evolution.
  Calls to EVOLVE will generate a new collection of chromosome objects."))


;;; Functions

(defun random-gene (length)
  "Generates a random gene of LENGTH characters between ASCII values 32 and
  122."
  (loop repeat length
        collect (code-char (+ (random 91) 32)) into result
        finally (return (coerce result 'string))))


(defun make-chromosome (&optional (target "Hello, world!") (gene nil))
  "Returns an instance of the CHROMOSOME class.  If GENE is nil a gene
  will be created using RANDOM-GENE."
  (let ((new-gene (if gene gene (random-gene (length target)))))
    (make-instance 'chromosome :target target :gene new-gene)))


(defun make-population (&key (size 2048) (target "Hello, world!")
                             (crossover 0.8) (elitism 0.1) (mutation 0.3))
  "Returns an instance of the POPULATION class."
  (let ((chromosomes (loop repeat size
                           collect (make-chromosome target) into result
                           finally (return (sort result (lambda (a b)
                                                          (< (fitness a)
                                                             (fitness b))))))))
    (make-instance 'population :size size :crossover crossover :elitism elitism
                               :mutation mutation :chromosomes chromosomes)))


(defun mkstr (&rest args)
  "Returns ARGS as a concatenated string."
  (with-output-to-string (s)
    (dolist (a args) (princ a s))))


;;; Chromosome Methods

(defmethod initialize-instance :after ((c chromosome) &key)
  "Sets (FITNESS CHROMOSOME)."
  (let ((fitness (loop for i from 0 below (length (gene c))
                       sum (abs (- (char-code (elt (gene c) i))
                                   (char-code (elt (target c) i)))))))
    (setf (slot-value c 'fitness) fitness)))


(defmethod mate ((c1 chromosome) (c2 chromosome))
  "Method used to mate the chromosome C1 with C2 resulting in a list
  containing two new chromosomes."
  (let* ((pivot (random (length (gene c1))))
         (gene1 (mkstr (subseq (gene c1) 0 pivot)
                       (subseq (gene c2) pivot)))
         (gene2 (mkstr (subseq (gene c2) 0 pivot)
                       (subseq (gene c1) pivot))))
    (list (make-chromosome (target c1) gene1)
          (make-chromosome (target c1) gene2))))


(defmethod mutate ((c chromosome))
  "Method used to generate a new chromosome based on a change in a
  random character in the gene of this chromosome.
  Returns a new chromosome instance."
  (let* ((gene (gene c))
         (delta (+ (random 91) 32))
         (random-index (random (length gene))))
    (setf (elt gene random-index)
          ;; Is this a correct transcription of the Python code?
          ;(code-char (mod (+ (char-code (elt gene random-index)) delta) 122)))
          ;; Lets just use a new random char for now.
          (code-char delta))
    (make-chromosome (target c) gene)))


(defmethod print-object ((obj chromosome) stream)
  (print-unreadable-object (obj stream :type t)
    (format stream "f=~D ~S" (fitness obj) (gene obj))))


;;; Population Methods

(defmethod evolve ((p population))
  "Method to evolve the population of chromosomes."
  (loop with index = (floor (* (size p) (elitism p)))
        with new-chromosomes = (subseq (chromosomes p) 0 index)
        while (< index (size p))
        do (if (<= (random 1.0) (crossover p))
               (let ((children (mate (tournament-selection p)
                                     (tournament-selection p))))
                 (loop for child in children
                       do (if (<= (random 1.0) (mutation p))
                              (push (mutate child) new-chromosomes)
                              (push child new-chromosomes)))
                 (incf index 2))
               (progn
                 (if (<= (random 1.0) (mutation p))
                     (push (mutate (elt (chromosomes p) index))
                           new-chromosomes)
                     (push (elt (chromosomes p) index) new-chromosomes))
                 (incf index)))
        finally (setf (chromosomes p)
                      (sort new-chromosomes (lambda (a b)
                                              (< (fitness a) (fitness b)))))))


(defmethod print-object ((obj population) stream)
  (print-unreadable-object (obj stream :type t)
    (format stream "~D ~S" (size obj) (first (chromosomes obj)))))


(defmethod tournament-selection ((p population))
  (loop with best = (random (size p))
        repeat (tournament-size p)
        for cont = (random (size p))
        ;; We're comparing the indexes directly since the chromosomes are
        ;; sorted from best to worst.  Robust code should compare the fitness
        ;; of the candidates.
        when (< cont best) do (setf best cont)
        finally (return (elt (chromosomes p) best))))



;;; Main Program

(defun main (&optional (max-generations 16384))
  (setf *random-state* (make-random-state t))
  (let ((p (make-population :size 2048 :crossover 0.8 :elitism 0.1
                            :mutation 0.3)))
    (loop for g from 0 to max-generations
          for best-gene = (first (chromosomes p))
          for fitness = (fitness best-gene)
          do (format t "Generation ~D: ~S~%" g best-gene)
             (if (= fitness 0)
                 (loop-finish)
                 (evolve p))
          finally (unless (= fitness 0)
                    (format t (mkstr "Maximum generations reached without "
                                     "success.~%"))))))
