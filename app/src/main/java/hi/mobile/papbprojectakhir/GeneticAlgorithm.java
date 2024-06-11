package hi.mobile.papbprojectakhir;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class GeneticAlgorithm {
    private static final int POPULATION_SIZE = 50;
    private static final int NUM_GENERATIONS = 50;
    private static final double MUTATION_RATE = 0.2;
    private static final double CROSSOVER_RATE = 0.5;
    private static final int TOURNAMENT_SIZE = 3;

    private List<Food> foodData;
    private double targetCalories;

    public GeneticAlgorithm(List<Food> foodData, double targetCalories) {
        this.foodData = foodData;
        this.targetCalories = targetCalories;
    }

    public List<Food> run() {
        List<List<Food>> population = initializePopulation();
        List<Food> bestIndividual = null;

        for (int generation = 0; generation < NUM_GENERATIONS; generation++) {
            population = evolvePopulation(population);
            bestIndividual = getBestIndividual(population);
        }

        return bestIndividual != null ? bestIndividual : new ArrayList<>();
    }

    private List<List<Food>> initializePopulation() {
        List<List<Food>> population = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < POPULATION_SIZE; i++) {
            List<Food> individual = new ArrayList<>();
            for (int j = 0; j < 10; j++) { // 10 makanan per individu
                individual.add(foodData.get(random.nextInt(foodData.size())));
            }
            population.add(individual);
        }

        return population;
    }

    private List<List<Food>> evolvePopulation(List<List<Food>> population) {
        List<List<Food>> newPopulation = new ArrayList<>();

        for (int i = 0; i < POPULATION_SIZE; i++) {
            List<Food> parent1 = tournamentSelection(population);
            List<Food> parent2 = tournamentSelection(population);

            List<Food> offspring;
            if (Math.random() < CROSSOVER_RATE) {
                offspring = crossover(parent1, parent2);
            } else {
                offspring = new ArrayList<>(parent1);
            }

            if (Math.random() < MUTATION_RATE) {
                mutate(offspring);
            }

            newPopulation.add(offspring);
        }

        return newPopulation;
    }

    private List<Food> tournamentSelection(List<List<Food>> population) {
        List<List<Food>> tournament = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < TOURNAMENT_SIZE; i++) {
            tournament.add(population.get(random.nextInt(POPULATION_SIZE)));
        }

        return getBestIndividual(tournament);
    }

    private List<Food> crossover(List<Food> parent1, List<Food> parent2) {
        List<Food> offspring = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < parent1.size(); i++) {
            if (random.nextBoolean()) {
                offspring.add(parent1.get(i));
            } else {
                offspring.add(parent2.get(i));
            }
        }

        return offspring;
    }

    private void mutate(List<Food> individual) {
        Random random = new Random();
        int index = random.nextInt(individual.size());
        individual.set(index, foodData.get(random.nextInt(foodData.size())));
    }

    private List<Food> getBestIndividual(List<List<Food>> population) {
        return population.stream()
                .min(Comparator.comparingDouble(this::evaluateFitness))
                .orElse(new ArrayList<>());
    }

    private double evaluateFitness(List<Food> individual) {
        return individual.stream()
                .mapToDouble(food -> Math.abs(targetCalories - food.getCalories()))
                .sum();
    }
}
