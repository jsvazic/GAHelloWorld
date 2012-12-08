module GAHelloWorld
  RAND_SEED=Time.now.to_f
  TARGET_GENE='Hello World!'
  ALLOWED_LETTERS = (32.chr..122.chr).to_a

  class Chromosome
    def initialize(str)
      @gene=str
    end 

    def fitness
      @fitness ||= 
        begin
          diff=0
          gene_ary=Chromosome.to_int_array(@gene)
          target_ary =Chromosome.to_int_array(TARGET_GENE)
          gene_ary.size.times do |i| diff += (gene_ary[i].to_i - target_ary[i].to_i).abs  end
        end
    end 
    
    class << self
      def gen_random()
        str=''
        TARGET_GENE.size.times do |i|
          str << ALLOWED_LETTERS[rand(ALLOWED_LETTERS.size)]
        end
        Chromosome.new(str)
      end 
      private
      def to_int_array(str)
        #convenience method to get an array of strings for any string, compatible with ruby 1.9.3 and 1.8.7
        out=[]
          str.each_byte do |c| out << c end 
        out 
      end
    end
  end

  class Population
    def initialize(size=1024, crossover=0.8, elitism=0.1, mutation=0.03)
      @@tournamentSize = 3
      @size = size
      @crossover=crossover
      @elitism=elitism
      @mutation=mutation
      buf = []
      @size.times do |i|
        buf << Chromosome::gen_random()
      end 
      srand RAND_SEED
      @population = buf.sort{ |a,b| a.fitness <=> b.fitness }
    end 

    def tournament_selection
    end 
  end 
end 