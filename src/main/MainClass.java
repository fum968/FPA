package main;

import fpa.FPA;
import fpa.Function;

public class MainClass {
	private static final int iterations = 200;
	private static final int popSize = 20;
	private static final double switchProhability = 0.8;
	private static final Function function = Function.ROSENBROCK;
	
	public static void main(String[] args) {
		FPA fpa = new FPA();
		fpa.run(iterations, popSize, switchProhability, function);
	}
}
