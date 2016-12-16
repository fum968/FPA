package fpa;

import java.util.Random;

public class FPA {
	private static final Random random = new Random(System.currentTimeMillis());

	// ND: Normal distribution
	private static final double meanND = 0.0;
	private static final double stdDevND = 1.0;

	// Parameters
	private double lambda;
	private int dimensions;
	private double upperBound;
	private double lowerBound;

	/** Constructor with default values */
	public FPA() {
		this(1.5, 3, 2, -2);
	}

	/** Constructor */
	public FPA(double lambda, int dimensions, double upperBound, double lowerBound) {
		this.lambda = lambda;
		this.dimensions = dimensions;
		this.upperBound = upperBound;
		this.lowerBound = lowerBound;
	}

	/** Start */
	public void run(int iterations, int popSize, double switchProhability, Function function) {
		int indexBest, rand1, rand2;
		double fmin, fnew, epsilon;
		double[] fitness = new double[popSize];
		double[] candidate = new double[dimensions];
		double[] best = new double[dimensions];
		double[] levy = new double[dimensions];
		double[][] population = new double[popSize][dimensions];
		
		// Initialize the population
		for (int i = 0; i < popSize; i++) {
			for (int j = 0; j < dimensions; j++)
				population[i][j] = lowerBound + (upperBound - lowerBound) * random.nextDouble();

			fitness[i] = evaluate(population[i], function);
		}

		// Find the current best
		indexBest = 0;
		for (int i = 1; i < popSize; i++)
			if (fitness[i] < fitness[indexBest])
				indexBest = i;

		fmin = fitness[indexBest];
		for (int i = 0; i < dimensions; i++)
			best[i] = population[indexBest][i];

		// Iterations
		for (int iter = 0; iter < iterations; iter++) {
			for (int i = 0; i < popSize; i++) {
				if (random.nextDouble() > switchProhability) {
					/* Global Pollination */
					levy = levy();

					for (int j = 0; j < dimensions; j++) {
						candidate[j] = population[i][j] + levy[j] * (best[j] - population[i][j]);
					}
				} else {
					/* Local Pollination */
					epsilon = random.nextDouble();

					do {
						rand1 = random.nextInt(popSize);
					} while (rand1 == i);
					do {
						rand2 = random.nextInt(popSize);
					} while (rand2 == rand1);

					for (int j = 0; j < dimensions; j++)
						candidate[j] += epsilon * (population[rand1][j] - population[rand2][j]);
				}

				// Check bounds
				checkBounds(candidate);

				// Evaluate new solution
				fnew = evaluate(candidate, function);

				// If the new solution is better: Replace
				if (fnew < fitness[i]) {
					fitness[i] = fnew;
					for (int j = 0; j < dimensions; j++)
						population[i][j] = candidate[j];
				}

				// Update best solution
				if (fnew < fmin) {
					fmin = fnew;
					for (int j = 0; j < dimensions; j++)
						best[j] = candidate[j];
				}
			}
		}

		// Print
		System.out.println("Fmin: " + fmin);
		System.out.println("--------------------------");
		for (int i = 0; i < dimensions; i++)
			System.out.println("D" + Integer.toString(i + 1) + ": " + best[i]);
	}

	/** Evaluation */
	private double evaluate(double[] sol, Function func) {
		double fitness = 0;

		switch (func) {
		case SPHERE:
			for (int i = 0; i < dimensions; i++) {
				fitness += Math.pow(sol[i], 2);
			}
			return fitness;
		case ROSENBROCK:
			for (int i = 0; i < dimensions - 1; i++) {
				fitness += (100 * Math.pow((sol[i + 1] - Math.pow(sol[i], 2)), 2) + (Math.pow((1 - sol[i]), 2)));
			}
			return fitness;
		case RASTRIGIN:
			int A = 10;
			fitness += A * dimensions;
			for (int i = 0; i < dimensions; i++) {
				fitness += (Math.pow(sol[i], 2) - A * Math.cos(2 * Math.PI * sol[i]));
			}
			return fitness;
		default:
			return fitness;
		}
	}

	/** checks lower and upper bounds */
	private void checkBounds(double[] solution) {
		for (int i = 0; i < dimensions; ++i) {
			if (solution[i] < lowerBound)
				solution[i] = lowerBound;

			if (solution[i] > upperBound)
				solution[i] = upperBound;
		}
	}

	/** creates Levy flight samples */
	private double[] levy() {
		double[] step = new double[dimensions];

		double sigma = Math.pow(Gamma.gamma(1 + lambda) * Math.sin(Math.PI * lambda / 2)
				/ (Gamma.gamma((1 + lambda) / 2) * lambda * Math.pow(2, (lambda - 1) / 2)), (1 / lambda));

		for (int i = 0; i < dimensions; i++) {

			double u = Distribution.normal(random, meanND, stdDevND) * sigma;
			double v = Distribution.normal(random, meanND, stdDevND);

			step[i] = 0.01 * u / (Math.pow(Math.abs(v), (1 - lambda)));
		}
		return step;
	}

	/* Getter and setter */

	public void setDimension(int dimensions) {
		this.dimensions = dimensions;
	}

	public void setUpperBound(double upperBound) {
		this.upperBound = upperBound;
	}

	public void setLowerBound(double lowerBound) {
		this.lowerBound = lowerBound;
	}

	public void setLambda(double lambda) {
		this.lambda = lambda;
	}
}
