# helloWorldGeneticAlgorithm
Genetic Algorithm That Solves For User Defined Strings in Matlab (example uses 'Hello, world!')

The algorithm should be able to solve for any target string that contains ASCII characters. The string size is totally adjustable and is handled by the algorithm so there is no need to preallocate the size of the string, simply change it to whatever value you wish.

Please feel free to change the type of selection and crossover or adjust the rate of mutation or the poplulation size. It should allow the user to get a feel for how differnent parameters effect the algorithm and will hopefully help build a stronger understanding of each individual part of the genetic algorithm.

The latest version of the algorithm includes an option to plot the progress of fitness throughout the generations, but can be turned off.

A sample output from the algorithm running with: 
  * Population Size = 1,000
  * Mutation Rate = 10%
  * 1 Point Crossover
  * Tournament Selection (with tournament size = 4)
  * Target string = 'Hello, world!'

Gives an output of:

Gen: 1  |  Fitness: 219  |  JAmYv'&L_Cov1

Gen: 2  |  Fitness: 150  |  Vlrrd:VnuBc

Gen: 4  |  Fitness: 130  |  JPmbj6ljThT 

Gen: 5  |  Fitness: 105  |  :^mYv'&oj\jb(

Gen: 6  |  Fitness: 100  |  Ilrrf,(sluBc

Gen: 7  |  Fitness: 68  |  Iilsj6lrsgd

Gen: 9  |  Fitness: 52  |  Iildq-(slusc

Gen: 10  |  Fitness: 41  |  Iildq-(vnuob

Gen: 11  |  Fitness: 38  |  Iilmh'&wmsjb

Gen: 12  |  Fitness: 33  |  Iilmh'&wmunb!

Gen: 13  |  Fitness: 27  |  Iildq-wmsjd#

Gen: 14  |  Fitness: 25  |  Ihnlr,(wnunb!

Gen: 15  |  Fitness: 22  |  Iilmj-wnsjb!

Gen: 16  |  Fitness: 21  |  Iillq-&wmsjd#

Gen: 17  |  Fitness: 16  |  Iillq,wmsjd!

Gen: 19  |  Fitness: 14  |  Igllq,wmsjd!

Gen: 20  |  Fitness: 12  |  Igllq,wmsjd!

Gen: 22  |  Fitness: 11  |  Igllq,wnsld#

Gen: 23  |  Fitness: 10  |  Igllq,wmsld!

Gen: 24  |  Fitness: 8  |  Igllq,wnsld!

Gen: 27  |  Fitness: 7  |  Igllq,!wosld!

Gen: 30  |  Fitness: 6  |  Igllo,!wnsld!

Gen: 32  |  Fitness: 5  |  Hglln,!wosld!

Gen: 34  |  Fitness: 4  |  Igllo,world!

Gen: 36  |  Fitness: 3  |  Hgllo,world!

Gen: 37  |  Fitness: 2  |  Iello,!world!

Gen: 40  |  Fitness: 1  |  Hello,!world!

Gen: 77  |  Fitness: 0  |  Hello, world!

Elapsed time is 0.069605 seconds.
