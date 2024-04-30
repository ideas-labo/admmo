package org.sas.benchmark.sm.spo.adaptive;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.femosaa.core.EAConfigure;
import org.femosaa.core.SASAlgorithmAdaptor;
import org.femosaa.core.SASSolutionInstantiator;
import org.femosaa.seed.FixedSeeder;
import org.femosaa.seed.Seeder;
import org.ssase.objective.Objective;
import org.ssase.objective.optimization.femosaa.ibea.IBEAwithKAndDRegion;
import org.ssase.objective.optimization.femosaa.nsgaii.NSGAIIwithKAndDRegion;
import org.ssase.objective.optimization.rs.RSRegion;
import org.ssase.primitive.ControlPrimitive;
import org.ssase.primitive.Primitive;
import org.ssase.region.OptimizationType;
import org.ssase.region.Region;
import org.ssase.util.Repository;

import jmetal.core.Algorithm;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.core.Variable;
import jmetal.encodings.variable.Int;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import weka.core.Instances;
import weka.core.Randomizable;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;
import weka.core.WekaException;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.meta.Bagging;
import weka.classifiers.trees.M5P;
import weka.classifiers.trees.REPTree;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;

public class Flash {
	
	private SASSolutionInstantiator factory = null;
	private Seeder seeder = null;
	public static Instances[] datasets;
	
	List<Objective> o = new ArrayList<Objective>();
	
	public static double[] weights = new double[0];
	public static int index = -1;
	public static AbstractClassifier[] models;
	
	private static int size = 30;
	private static int budget = 50;//50;
	Set<String> evaluated = new HashSet<String>();
	Set<String> unevaluated = new HashSet<String>();

	String[] ind = new String[] {"0.84:0.41:0.12:0.21:0.89:0.85:0.73:0.22:0.82:0.89:0.34:1.0:0.06:0.11:0.59:0.24:0.5:0.49:0.9:0.9:1.0:0.46:0.63:0.29:0.24:0.97:0.19:0.08:0.7:0.39",
			"0.26:0.86:0.66:0.71:0.14:0.99:0.43:0.16:0.18:0.34:0.02:0.99:0.85:0.55:0.61:0.1:0.55:0.27:0.68:0.39:0.96:0.62:0.28:0.38:0.99:0.76:0.42:0.07:0.91:0.42",
			"0.57:0.04:0.47:0.86:0.36:0.14:0.52:0.51:0.49:0.7:0.22:0.81:0.5:0.95:0.38:0.17:0.24:0.06:0.26:0.21:0.78:0.01:0.8:0.98:0.7:0.82:0.18:0.85:0.79:0.84",
			"0.91:0.66:0.7:0.65:0.81:0.17:0.35:0.6:0.23:0.93:0.3:0.21:0.42:0.18:0.18:0.6:0.49:0.41:0.16:0.58:0.57:0.28:0.06:0.51:0.91:0.35:0.83:0.05:0.18:0.8",
			"0.92:0.28:0.56:0.39:0.01:0.38:0.31:0.76:0.78:0.69:0.83:0.54:0.09:0.63:0.2:0.74:0.73:0.47:0.79:0.47:0.35:0.79:0.61:0.04:1.0:0.38:0.13:0.26:0.79:0.79",
			"0.03:0.85:0.45:0.23:0.47:0.68:0.33:0.56:0.99:0.87:0.76:0.95:0.69:0.22:0.79:0.68:0.02:0.52:0.52:0.21:0.57:0.27:0.39:0.76:0.07:0.83:0.32:0.92:0.33:0.05",
			"0.76:0.78:0.92:0.94:0.89:0.8:0.4:0.41:0.41:0.05:0.71:0.95:0.26:0.26:0.79:0.52:0.21:0.28:0.92:0.56:0.81:0.34:0.45:0.74:0.28:0.06:0.54:0.3:0.59:0.89",
			"0.93:0.14:0.57:0.81:0.04:1.0:0.43:0.44:0.27:0.47:0.75:0.6:0.97:0.4:0.22:0.82:0.66:0.48:0.0:0.29:0.02:0.33:0.45:0.12:0.61:0.2:0.1:0.89:0.9:0.32",
			"0.97:0.97:0.14:0.59:0.97:0.35:0.3:0.86:0.56:0.71:0.56:0.8:0.92:0.26:0.79:0.01:0.0:0.61:0.67:0.86:0.44:0.41:0.7:0.57:0.97:0.18:0.42:0.22:0.08:0.64",
			"0.58:0.89:0.56:0.91:0.95:0.69:0.7:0.61:0.21:0.81:0.04:0.82:0.14:0.86:0.61:0.63:0.89:0.16:0.43:0.79:0.88:0.81:0.25:0.14:0.39:0.08:0.59:0.68:0.01:0.67",
			"0.6:0.24:0.35:0.9:0.51:0.99:0.77:0.41:0.17:0.38:0.11:0.45:0.24:0.07:0.56:0.6:0.44:0.43:0.57:0.09:0.84:0.54:0.06:0.98:0.33:0.53:0.67:0.4:0.97:0.92",
			"0.28:0.44:0.83:0.7:0.6:0.57:0.07:0.29:0.01:0.19:0.46:0.59:0.33:0.95:0.34:0.75:0.28:0.56:0.05:0.55:0.93:0.28:0.05:0.75:0.31:0.81:0.52:0.2:0.92:0.41",
			"0.7:0.12:0.16:0.19:0.65:0.86:0.92:0.13:0.22:0.85:0.16:0.87:0.52:0.55:0.92:0.26:0.0:0.22:0.53:0.0:0.46:0.48:0.55:0.22:0.16:0.28:0.64:0.04:0.75:0.66",
			"0.94:0.07:0.17:0.02:0.59:0.07:0.78:0.03:0.07:0.75:0.05:0.28:0.53:0.54:0.46:0.8:0.51:0.53:0.14:0.53:0.89:0.15:0.17:0.76:0.76:0.04:0.99:0.16:0.05:0.78",
			"0.13:0.1:0.14:0.09:0.11:0.58:1.0:0.0:0.46:0.47:0.5:0.7:0.0:0.12:0.31:0.31:0.8:0.46:0.11:0.29:0.47:0.99:0.97:0.52:0.84:0.93:0.07:0.92:0.01:0.08",
			"0.77:0.57:0.61:0.25:0.35:0.16:0.52:0.42:0.14:0.48:0.71:0.06:0.02:0.23:0.42:0.16:0.2:0.23:0.92:0.11:0.92:0.25:0.04:0.69:0.36:0.53:0.54:0.85:0.77:0.42",
			"0.47:0.59:0.31:0.99:0.79:0.64:0.77:0.7:0.14:0.11:0.57:0.55:0.0:0.37:0.22:0.95:0.28:0.16:0.31:0.39:0.22:0.15:0.59:0.61:0.18:0.25:0.62:0.46:0.71:0.06",
			"0.75:0.72:1.0:0.36:0.14:0.51:0.02:0.86:0.2:0.22:0.93:0.07:0.61:0.73:0.69:0.09:0.46:0.07:0.18:0.27:0.92:0.38:0.05:0.36:0.73:0.87:0.31:0.62:0.65:0.12",
			"0.91:0.5:0.93:0.19:0.5:0.94:0.27:0.68:0.12:0.49:0.2:0.98:0.03:0.48:0.84:0.1:0.23:0.89:0.31:0.95:1.0:0.81:0.59:0.99:0.84:0.22:0.07:0.17:0.64:0.57",
			"0.75:0.18:0.07:0.74:0.73:0.09:0.57:0.25:0.86:1.0:0.22:0.18:0.87:0.33:0.92:0.58:0.47:0.37:0.89:0.65:0.24:0.45:0.01:0.55:0.73:0.42:0.18:0.47:0.4:0.37",
			"0.02:0.83:0.12:0.26:0.01:0.1:0.71:0.56:0.26:0.63:0.86:0.0:0.67:0.71:0.55:0.8:0.03:0.66:0.11:0.8:0.89:0.71:0.23:0.2:0.72:0.35:0.96:0.48:0.71:0.99",
			"0.87:0.69:0.59:0.36:0.37:0.18:0.2:0.63:0.37:0.78:0.43:0.72:0.78:0.85:0.35:0.74:0.78:0.35:0.38:0.46:0.71:0.76:0.42:0.51:0.33:0.24:0.76:0.89:0.83:0.91",
			"0.08:0.18:0.49:0.2:0.13:0.23:0.83:0.35:0.81:0.02:0.69:0.05:0.73:0.34:0.18:0.95:0.82:0.7:0.38:0.11:0.2:0.59:0.88:0.08:0.79:0.51:0.09:0.16:0.23:0.96",
			"0.38:0.27:0.56:0.75:0.75:0.67:0.04:0.51:0.16:0.79:0.96:0.65:0.26:0.26:0.12:0.88:0.33:0.72:0.83:0.14:0.59:0.66:0.88:0.52:0.81:0.63:0.74:0.46:0.47:0.18",
			"0.32:0.18:0.02:0.07:0.03:0.83:0.47:0.07:0.05:0.07:0.49:0.98:0.56:0.98:0.61:0.93:0.0:0.56:0.98:0.88:0.36:0.71:0.77:0.77:0.06:0.08:0.54:0.68:0.71:0.84",
			"0.57:0.43:0.11:0.72:0.47:0.67:0.29:0.66:0.19:0.88:0.77:0.75:0.96:0.79:0.89:0.89:0.1:0.46:0.73:0.08:0.06:0.75:0.63:0.61:0.89:0.07:0.3:0.6:0.37:0.16",
			"0.18:0.42:0.86:0.19:0.72:0.23:0.6:0.19:0.73:0.04:0.9:0.1:0.25:0.8:0.3:0.96:0.13:0.15:0.18:0.0:0.24:0.07:0.34:0.68:0.39:0.01:0.25:0.52:0.75:0.44",
			"0.09:0.93:0.15:0.98:0.67:0.49:0.15:0.49:0.93:0.75:0.96:0.28:0.72:0.85:0.99:0.41:0.96:0.22:0.87:0.83:0.78:0.86:0.93:0.1:0.08:0.49:0.71:0.21:0.05:0.74",
			"0.44:0.71:0.81:0.29:0.95:0.83:0.54:0.95:0.31:0.55:0.18:0.94:0.31:0.18:0.46:0.21:0.41:0.57:0.31:0.53:0.54:0.3:0.69:0.7:0.39:0.12:0.86:0.03:0.85:0.96",
			"0.21:0.17:0.37:0.57:0.01:0.98:0.53:0.39:0.77:1.0:0.38:0.92:0.34:0.28:0.48:0.17:0.22:0.48:0.69:0.36:0.85:0.18:0.21:0.79:0.23:0.53:0.43:0.73:0.93:0.86",
			"0.0:0.55:0.54:0.55:0.8:0.02:0.98:0.91:0.99:0.04:0.19:0.08:0.62:0.17:0.7:0.12:0.47:0.92:0.86:0.7:0.13:0.11:1.0:0.95:0.66:0.63:0.83:0.77:1.0:1.0",
			"0.63:0.41:0.72:0.81:0.29:0.29:0.16:0.59:0.35:0.7:0.3:0.1:0.83:0.66:0.71:0.06:0.22:0.84:0.57:0.91:0.6:0.96:0.93:0.86:0.38:0.14:0.82:0.74:0.5:0.73",
			"0.81:0.32:0.63:0.34:0.89:0.99:0.79:0.51:0.99:0.05:0.24:0.07:0.96:0.1:0.18:0.18:0.13:0.56:0.52:0.81:0.09:0.26:0.27:0.18:0.3:0.78:0.37:0.93:0.3:0.51",
			"0.65:0.6:0.3:0.45:0.23:0.29:0.87:0.56:0.53:0.89:0.09:0.28:0.05:0.09:0.51:0.69:0.66:0.67:0.07:0.36:0.44:0.81:0.19:0.22:0.16:0.72:0.33:0.9:0.66:0.7",
			"0.92:0.96:0.46:0.16:0.15:0.8:0.87:0.6:0.5:0.52:0.47:0.48:0.15:0.36:0.28:0.06:0.04:0.02:0.59:0.45:0.48:0.27:0.61:0.59:0.69:0.14:0.13:0.3:0.43:0.53",
			"0.0:0.96:0.03:0.37:0.62:0.8:0.91:0.73:0.22:0.62:0.62:0.64:0.49:0.92:0.97:0.54:0.59:0.64:0.47:0.57:0.2:0.6:0.22:0.17:0.92:1.0:0.42:0.8:0.53:0.0",
			"0.14:0.37:0.88:0.91:0.83:0.99:0.24:0.4:0.75:0.47:0.41:0.56:0.01:0.25:0.89:0.04:0.44:0.67:0.69:0.73:0.34:0.77:0.43:0.69:0.86:0.59:0.15:0.47:0.8:0.81",
			"0.67:0.32:0.47:0.14:0.2:0.08:0.48:0.66:0.07:0.49:0.15:0.97:0.78:0.85:0.87:0.84:0.8:0.49:0.05:0.41:0.26:0.33:0.74:0.19:0.82:0.9:0.73:0.7:0.75:0.51",
			"0.58:0.33:0.26:0.32:0.45:0.79:0.16:0.39:0.05:0.25:0.95:0.61:0.28:0.46:0.0:0.02:0.23:0.5:0.04:0.69:0.42:0.77:0.04:0.3:0.08:1.0:0.21:0.83:0.76:0.29",
			"0.32:0.26:0.02:0.24:0.2:0.36:0.5:0.39:0.86:0.81:0.56:0.99:0.01:0.73:0.84:0.56:0.69:0.9:0.82:0.69:0.91:0.95:0.45:0.18:0.32:0.3:0.02:0.11:0.09:0.28",
			"0.77:0.54:0.24:0.69:0.97:0.6:0.55:0.62:0.45:0.33:0.34:0.39:0.22:0.0:0.52:0.82:0.59:0.11:0.76:0.69:0.19:0.72:0.67:0.0:0.56:0.73:0.78:0.55:0.24:0.76",
			"0.95:0.78:0.4:0.47:0.22:0.6:0.79:0.72:0.32:0.74:0.09:0.78:0.13:0.49:0.19:0.28:0.44:0.63:0.59:0.53:0.47:0.87:0.33:0.34:0.99:0.13:0.77:0.47:0.37:0.81",
			"0.83:0.29:0.71:0.23:0.57:0.04:0.09:0.51:0.33:0.54:0.15:0.49:0.17:0.16:0.73:0.85:0.33:0.67:0.57:0.59:0.0:0.08:0.88:0.96:0.11:0.48:0.7:0.24:0.66:0.55",
			"0.7:0.2:1.0:0.2:0.95:0.29:0.98:0.77:0.34:0.95:0.78:0.05:0.0:0.53:0.38:0.99:0.37:0.23:0.5:0.44:0.53:0.81:0.82:0.11:0.62:0.3:0.5:0.28:0.28:0.88",
			"0.12:0.75:0.99:0.59:0.37:0.43:0.16:0.63:0.34:0.87:0.97:0.44:0.06:0.15:0.47:0.27:0.05:0.85:0.49:0.18:0.19:0.96:0.38:0.68:0.57:0.46:0.0:0.61:0.58:0.86",
			"0.34:0.43:0.48:0.98:0.34:1.0:0.85:0.05:0.11:0.93:0.52:0.74:0.32:0.24:0.58:0.43:0.14:0.54:0.16:0.29:0.9:0.71:0.42:0.87:0.79:0.32:0.51:0.61:0.03:0.44",
			"0.32:0.57:0.22:0.94:0.44:0.7:0.75:0.91:0.9:0.84:0.92:0.31:0.32:0.89:0.29:0.24:0.96:0.81:0.79:0.13:0.5:1.0:0.54:0.42:0.77:0.2:0.55:0.54:0.64:0.24",
			"0.94:0.16:0.27:0.01:0.21:0.41:0.43:0.95:0.33:0.48:0.33:0.75:0.02:0.8:0.91:0.17:0.0:0.17:0.01:0.12:0.97:0.07:0.4:0.19:0.38:0.41:0.96:0.92:0.22:0.28",
			"0.32:0.97:0.57:0.36:0.58:0.56:0.29:0.09:0.39:0.07:0.96:0.64:0.78:0.01:0.53:0.92:0.13:0.23:0.36:0.25:0.63:0.31:0.35:0.93:0.98:0.6:0.91:0.43:0.66:0.12",
			"0.33:0.55:0.26:0.52:0.7:0.98:0.01:0.26:0.99:0.03:0.77:0.87:0.66:0.87:0.34:0.77:0.04:0.26:0.4:0.82:0.04:0.49:0.57:0.16:0.99:0.41:0.31:0.84:0.89:0.58",
};
	
	private static int global_size_index = 0;
	
	/**
  	 * Constructor
  	 * @param problem Problem to solve
  	 */
	public Flash() {
	}
	
	public static void main(String[] a) {

		

		// Set<Integer> set = new HashSet<Integer> ();
		for (int k = 0; k < 50; k++) {
			String r = "";
			int n = size;
			do {
				int i = PseudoRandom.randInt(0, 100);
				// if(!set.contains(i)) {
				// set.add(i);
				r += r.equals("") ? i / 100.0 : ":" + i / 100.0;
				n--;
				// }

			} while (n > 0);

			System.out.print("\""+r+"\"" + ",\n");
		}
	}
	
	public double getSeedBestAvg(String b, int id) {
		Parser.selected = b;
		Parser.main(null);
		
		LinkedHashMap<String, Double> map = id == 0? Parser.map1 : Parser.map2;
		
		global_size_index = 0;
		
		
		 
		 SolutionSet set = new SolutionSet();
		 double best = 0;
		 for (int i = 0; i < 50; i++) {
			 List<Integer> listInt = new ArrayList<Integer> ();
			 String[] split = ind[global_size_index].split(":");
			 for (String si : split) {
				 double d = Double.parseDouble(si);
				 listInt.add((int)(d * (Parser.seeds.size() - 1)));
			 }
			 double temp_best = Double.MAX_VALUE;
			 for (int number : listInt) {
				 //System.out.print("training sample " + number + "\n");
				 String s1 = Parser.seeds.get(number);
				 if(map.get(s1) < temp_best) {
					 temp_best = map.get(s1);
				 }
				 
			 }
			 best += temp_best;
			 global_size_index++;
		 }
		 
		 return best / 50.0;
		
	}
	
	public SolutionSet execute_RS(double[] weights) throws JMException, ClassNotFoundException {
		
		SASAlgorithmAdaptor.logMeasurementOfObjectiveValue = false;
		SASAlgorithmAdaptor.PRINT_SOLUTIONS = false;
		SASAlgorithmAdaptor.LOG_SOLUTIONS = false;
		SASAlgorithmAdaptor.logGenerationOfObjectiveValue = -1;
		
		EAConfigure.getInstance().setupFLASHConfigurationInternal();
		EAConfigure.getInstance().measurement = -1;
		
		Set<Objective> objs = Repository.getAllObjectives();
		
		for (Objective ob : objs) {
			if ("sas-rubis_software-P1".equals(ob.getName())) {
				o.add(ob);
			} 
		}
		
		for (Objective ob : objs) {
			if ("sas-rubis_software-P2".equals(ob.getName())) {
				o.add(ob);
			} 
		}
		
		
		this.weights = weights;
		
		
		int max_measurement = budget;//budget;
		int measurement = 0;
	
		SolutionSet solutionSet = seed(size);
		measurement += size;
		
		int obj = solutionSet.get(0).numberOfObjectives();
		datasets = new Instances[obj];
		models = new AbstractClassifier[obj];
		
		train(solutionSet, obj);
		
		do {
			
			
			Solution s = null;
			try {
				s = random(o);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			boolean[] success = evaluate(s);//evaluate(s);
			
			if(success[0]) {
				solutionSet.add(s);			
				train(solutionSet, obj);
				
			
				
				//if (SASAlgorithmAdaptor.logMeasurementOfObjectiveValue) {
					org.femosaa.util.Logger.logSolutionSetWithGeneration(solutionSet, "SolutionSetWithMeasurement.rtf", 
								measurement );				
					
				//}
			}
			
			

			if(success[1]) {
				measurement++;
			}
			
			
		} while (measurement < max_measurement);
		
		
		org.femosaa.util.Logger.logSolutionSet(solutionSet, "SolutionSet.rtf");
		
		 
		
		// TODO Auto-generated method stub
		return solutionSet;
	}
	
    public SolutionSet execute_GS(double[] weights) throws JMException, ClassNotFoundException {
		
		SASAlgorithmAdaptor.logMeasurementOfObjectiveValue = false;
		SASAlgorithmAdaptor.PRINT_SOLUTIONS = false;
		SASAlgorithmAdaptor.LOG_SOLUTIONS = false;
		SASAlgorithmAdaptor.logGenerationOfObjectiveValue = -1;
		
		EAConfigure.getInstance().setupFLASHConfigurationInternal();
		EAConfigure.getInstance().measurement = -1;
		
		Set<Objective> objs = Repository.getAllObjectives();
		
		for (Objective ob : objs) {
			if ("sas-rubis_software-P1".equals(ob.getName())) {
				o.add(ob);
			} 
		}
		
		for (Objective ob : objs) {
			if ("sas-rubis_software-P2".equals(ob.getName())) {
				o.add(ob);
			} 
		}
		
		
		this.weights = weights;
		
		
		int max_measurement = budget;
		int measurement = 0;
	
		SolutionSet solutionSet = random_seed(size);//seed(size);
		measurement += size;
		
		int obj = solutionSet.get(0).numberOfObjectives();
		datasets = new Instances[obj];
		models = new AbstractClassifier[obj];
		
		train(solutionSet, obj);
		
		do {
			
			
			Solution s = null;
			try {
				s = true_grid(o);//grid(o); //random(o);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			boolean success[] = evaluate(s);
			
			if(success[0]) {
				solutionSet.add(s);
				
				train(solutionSet, obj);
				
				//if (SASAlgorithmAdaptor.logMeasurementOfObjectiveValue) {
					org.femosaa.util.Logger.logSolutionSetWithGeneration(solutionSet, "SolutionSetWithMeasurement.rtf", 
								measurement );				
					
				//}
			} 
			
			
			if(success[1]) {
				measurement++;
			}
			
			if(!success[0]&& success[1]) {
				//System.out.print("invalid one\n");
			}
			
		} while (measurement < max_measurement);
		
		
		
		if(SASAlgorithmAdaptor.isLogSolutionsInFull) {
			Set<String> full_set = new HashSet<String>();
			for (int i = 0; i < solutionSet.size(); i++) {
				
				

				full_set.add(convertFullInfo(solutionSet.get(i)));
			}
			
			
			org.femosaa.util.Logger.logSolutionFull(full_set, "FullSolution.rtf");
			
		}
		org.femosaa.util.Logger.logSolutionSet(solutionSet, "SolutionSet.rtf");
		
		
		
		// TODO Auto-generated method stub
		return solutionSet;
	}
    
    @SuppressWarnings("unchecked")
	public SolutionSet execute_BOCA(double[] weights) throws JMException, ClassNotFoundException {
		
		SASAlgorithmAdaptor.logMeasurementOfObjectiveValue = false;
		SASAlgorithmAdaptor.PRINT_SOLUTIONS = false;
		SASAlgorithmAdaptor.LOG_SOLUTIONS = false;
		SASAlgorithmAdaptor.logGenerationOfObjectiveValue = -1;
		
		EAConfigure.getInstance().setupFLASHConfigurationInternal();
		EAConfigure.getInstance().measurement = -1;
		
		Set<Objective> objs = Repository.getAllObjectives();
		
		for (Objective ob : objs) {
			if ("sas-rubis_software-P1".equals(ob.getName())) {
				o.add(ob);
			} 
		}
		
		for (Objective ob : objs) {
			if ("sas-rubis_software-P2".equals(ob.getName())) {
				o.add(ob);
			} 
		}
		
		
		this.weights = weights;
		
		
		int max_measurement = budget;
		int measurement = 0;
	
		SolutionSet solutionSet = random_seed(size);//seed(size);
		measurement += size;
		
		int obj = solutionSet.get(0).numberOfObjectives();
		datasets = new Instances[obj];
		models = new AbstractClassifier[obj];
		
		train(solutionSet, obj);
		
		int index = 0;
		if(weights[0] == 1.0) {
			index = 0;			
		} else {
			index = 1;
		}
		
		do {
			
			
			NewRandomForest rf = (NewRandomForest)models[index];
			
			// 1 get important optins
			
			
			List<ControlPrimitive> list = Repository.getSortedControlPrimitives(o.get(0));
			double[] importance = rf.computeAverageImpurityDecreasePerAttribute(list.size());
			
			List<Tuple> entry = new ArrayList<Tuple>();
			
			for (int i = 0 ; i < importance.length; i++) {
				Tuple t = new Tuple();
				t.importance = importance[i];
				t.p = list.get(i);
				entry.add(t);
			}
			
			Collections.sort(entry, new Comparator() {

				@Override
				public int compare(Object arg0, Object arg1) {
					Tuple t1 = (Tuple)arg0;
					Tuple t2 = (Tuple)arg1;
					
					if(t1.importance > t2.importance) {
						return -1;
					} else if(t1.importance < t2.importance) {
						return 1;
					}
					
					return 0;
				}
				
				
			});
			

			List<ControlPrimitive> top = new ArrayList<ControlPrimitive>();
			
			int K = (int)Math.round(0.5 * list.size());//8;
			if(K == 0) {
				K = 8;//2
			}
			System.out.print("K: " + K + "\n");
			//int K = 8;
			
			for (int i = 0 ; i < K; i++) {
				top.add(entry.get(i).p);
			}
		
			// 2 determine candidate to sample
			
			Set<String> candidates = new HashSet<String>();
			
			String config = "";
			for (int i = 0; i < entry.size(); i++) {
				config += ":x"; 
			}
			config = config.replaceFirst(":", "");
			
			getAllConfigImportance(config, list, top, candidates, 0);	
			
			//System.out.print("imporatnce: " + top.size() + "\n");
			
			Set<String> full_candidates = new HashSet<String>();
			
			int C = 256; //2 ** 8, 2 to the power of 8 
			int C_i = countUnimportantOptions(C, measurement);
			
			for (String s : candidates)  {
				
				for (int l = 0; l < C_i; l ++) {

					String[] sp = s.split(":");
	    			String temp = "";
	    			for (int j = 0; j < sp.length; j++) {
	    				if(sp[j].equals("x")) {
	    					ControlPrimitive cp = list.get(j);
	    					int value = PseudoRandom.randInt(0,cp.getValueVector().length-1);
	    					temp += ":" + value;
	    				} else {
	    					temp += ":" + sp[j];
	    				}
	    			}
	    			temp = temp.replaceFirst(":", "");
	    			
	    			full_candidates.add(temp);
				}
				
			}
			//System.out.print("candidates " + candidates.size() + ", C_i " + C_i +  ", full_candidates " + full_candidates.size() + "\n");
			// 3 get the best EI
			
			Solution s = null;
			try {
				double eta = Double.MAX_VALUE;
				for (int l = 0; l < solutionSet.size(); l++) {
					double v = solutionSet.get(l).getObjective(index);
					if (v < eta) {
						eta = v;
					}
				}
				s = true_grid_EI(eta, o, full_candidates);//grid(o); //random(o);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			boolean success[] = evaluate(s);
			
			if(success[0]) {
				solutionSet.add(s);
				
				train(solutionSet, obj);
				
				//if (SASAlgorithmAdaptor.logMeasurementOfObjectiveValue) {
					org.femosaa.util.Logger.logSolutionSetWithGeneration(solutionSet, "SolutionSetWithMeasurement.rtf", 
								measurement );				
					
				//}
			} 
			
			
			if(success[1]) {
				measurement++;
			}
			
			if(!success[0]&& success[1]) {
				//System.out.print("invalid one\n");
			}
			//System.out.print("measurement: " + measurement + "\n");
		} while (measurement < max_measurement);
		
		
		org.femosaa.util.Logger.logSolutionSet(solutionSet, "SolutionSet.rtf");
		
		if(SASAlgorithmAdaptor.isLogSolutionsInFull) {
			Set<String> full_set = new HashSet<String>();
			Iterator itr = solutionSet.iterator();
			while(itr.hasNext()) {
				full_set.add(convertFullInfo((Solution)itr.next()));
			}
			org.femosaa.util.Logger.logSolutionFull(full_set, "FullSolution.rtf");
			
		}
		 
		
		// TODO Auto-generated method stub
		return solutionSet;
	}
	
    public SolutionSet execute_MO(String alg, int index) throws JMException, ClassNotFoundException {
		
		SASAlgorithmAdaptor.logMeasurementOfObjectiveValue = false;
		SASAlgorithmAdaptor.PRINT_SOLUTIONS = false;
		SASAlgorithmAdaptor.LOG_SOLUTIONS = false;
		SASAlgorithmAdaptor.logGenerationOfObjectiveValue = -1;
		EAConfigure.getInstance().setupFLASHConfigurationInternal();
		EAConfigure.getInstance().measurement = -1;
		//System.out.print("EAConfigure.getInstance().measurement " + EAConfigure.getInstance().measurement + "\n");
		
		Set<Objective> objs = Repository.getAllObjectives();
		
		for (Objective ob : objs) {
			if ("sas-rubis_software-P1".equals(ob.getName())) {
				o.add(ob);
			} 
		}
		
		for (Objective ob : objs) {
			if ("sas-rubis_software-P2".equals(ob.getName())) {
				o.add(ob);
			} 
		}
		
		
		this.index = index;
		
		
		int max_measurement = budget;
		int measurement = 0;
	
		SolutionSet solutionSet = seed(size);
		measurement += size;
		
		int obj = solutionSet.get(0).numberOfObjectives();
		datasets = new Instances[obj];
		models = new AbstractClassifier[obj];
		
		train(solutionSet, obj);
		
		/*SASAlgorithmAdaptor.isSeedSolution = true;
		List<String> l = new ArrayList<String>();
		int k = 0;
		for (String s : evaluated) {
			l.add(s);
			k++;
			if(k >= 5)
				break;
		}
		
		FixedSeeder.getInstance().setSeeds(l);*/
		
		
		int count = 0;
		do {
			
			
			Solution s = alg.equals("nsgaii") ? NSGAII(o) : IBEA(o);
		
			boolean success[] = evaluate(s);
			
			if(success[0]) {
				solutionSet.add(s);
				
				train(solutionSet, obj);
				
				//if (SASAlgorithmAdaptor.logMeasurementOfObjectiveValue) {
					org.femosaa.util.Logger.logSolutionSetWithGeneration(solutionSet, "SolutionSetWithMeasurement.rtf", 
								measurement );				
					
				//}
			} else {
				//System.out.print("invalid one\n");
			}
			
			
			
			
			if(success[1]) {
				measurement++;
			}
			System.out.print("measurement " + measurement + "\n");
			count++;
		} while (measurement < max_measurement /*&& count < 100*/);
		
		
		org.femosaa.util.Logger.logSolutionSet(solutionSet, "SolutionSet.rtf");
		
		 
		
		// TODO Auto-generated method stub
		return solutionSet;
	}
	
	public SolutionSet seed (int n) throws JMException  {
		
		
		 List<Integer> listInt = new ArrayList<Integer> ();
		 Set<String> unique_set = new HashSet<String> ();
		
		 
		 SolutionSet set = new SolutionSet();
		 
		 /*do {
			 int i = PseudoRandom.randInt(0,Parser.seeds.size()-1);
			 if(unique_set.contains(String.valueOf(i))) {
				 
			 } else {
				 unique_set.add(String.valueOf(i));
				 listInt.add(i);
				 n--;
			 }
			 
		 } while (n != 0);*/
		 
		 /*int gap = Parser.seeds.size() / n;
		 for (int i = 0; i < Parser.seeds.size(); i++) {
			 if (i % gap == 0) {
				 listInt.add(i);
				 if (listInt.size() == n) {
					 break;
				 }
			 }
		 }*/
		 
		 String[] split = ind[global_size_index].split(":");
		 for (String si : split) {
			 double d = Double.parseDouble(si);
			 listInt.add((int)(d * (Parser.seeds.size() - 1)));
		 }
		 
		 // For next repeat
		 global_size_index++;
		 
		 if(global_size_index >= 50) {
			 global_size_index = 0;
		 } 
		
		 
		 for (int number : listInt) {
			 //System.out.print("training sample " + number + "\n");
			 String s1 = Parser.seeds.get(number);
			 String[] sp1 = s1.split(":");
			 
			 Solution sol1 = new Solution(2);
			 
			 Variable[] v1 = new Variable[sp1.length];
			 for (int k = 0; k < sp1.length; k++) {		 
				 v1[k] = new Int(Integer.parseInt(sp1[k]), Integer.MIN_VALUE,Integer.MAX_VALUE);
			 }
			 
			 sol1.setDecisionVariables(v1);
			 
			 if(AutoRun.isNegative) {
				 sol1.setObjective(0, -1.0 * Parser.map1.get(s1)); 
			 } else {
				 sol1.setObjective(0, Parser.map1.get(s1));
			 }
				 
			 
			 
			 //
			 sol1.setObjective(1, Parser.map2.get(s1));
			 
			 
			 set.add(sol1);
			 String v = "";
			for(int i = 0; i < sol1.getDecisionVariables().length; i++) {
					v += v.equals("")? (int)sol1.getDecisionVariables()[i].getValue() : ":" + (int)sol1.getDecisionVariables()[i].getValue();
			 }
			 evaluated.add(v);
		 }
		
		
		 
		 return set;
		
	}
	
	
	public SolutionSet random_seed (int n) throws JMException  {
		
		List<String> seeds = new ArrayList<String>();
		if(unevaluated.size() == 0) {
			// This is needed for SS-N, otherwise it is intractable
			//for (String s : Parser.map1.keySet()) {
			//	unevaluated.add(s);
			//}
			getAllConfig("", unevaluated, 0);
			seeds.addAll(unevaluated);
		}
		
		
		 List<Integer> listInt = new ArrayList<Integer> ();
		 Set<String> unique_set = new HashSet<String> ();
		
		 
		 SolutionSet set = new SolutionSet();
		 
		 do {
			 //int i = PseudoRandom.randInt(0,Parser.seeds.size()-1);
			 int i = PseudoRandom.randInt(0,unevaluated.size()-1);
			 if(unique_set.contains(String.valueOf(i))) {
				 
			 } else {
				 unique_set.add(String.valueOf(i));
				 listInt.add(i);
				 n--;
			 }
			 
		 } while (n != 0);
		 
		 /*int gap = Parser.seeds.size() / n;
		 for (int i = 0; i < Parser.seeds.size(); i++) {
			 if (i % gap == 0) {
				 listInt.add(i);
				 if (listInt.size() == n) {
					 break;
				 }
			 }
		 }*/
		 
		
		
		 
		 for (int number : listInt) {
			 //System.out.print("training sample " + number + "\n");
			 //String s1 = Parser.seeds.get(number);
			 String s1 = seeds.get(number);
			 
			 if(!Parser.map1.containsKey(s1)) {
				 continue;
			 } else {

				 String[] sp1 = s1.split(":");
				 
				 Solution sol1 = new Solution(2);
				 
				 Variable[] v1 = new Variable[sp1.length];
				 for (int k = 0; k < sp1.length; k++) {		 
					 v1[k] = new Int(Integer.parseInt(sp1[k]), Integer.MIN_VALUE,Integer.MAX_VALUE);
				 }
				 
				 sol1.setDecisionVariables(v1);
				 
				 if(AutoRun.isNegative) {
					 sol1.setObjective(0, -1.0 * Parser.map1.get(s1)); 
				 } else {
					 sol1.setObjective(0, Parser.map1.get(s1));
				 }
					 
				 
				 
				 //
				 sol1.setObjective(1, Parser.map2.get(s1));
				 
				 
				 set.add(sol1);
				 String v = "";
				for(int i = 0; i < sol1.getDecisionVariables().length; i++) {
						v += v.equals("")? (int)sol1.getDecisionVariables()[i].getValue() : ":" + (int)sol1.getDecisionVariables()[i].getValue();
				 }
				 evaluated.add(v);
			 }
			 
		 }
		
		// This is needed for SS-N
		// set.clear();
		 System.out.print("Training set: " + set.size() + "\n" );
		 
		 // If too little, then force to meet the min requirements.
		 if(set.size() < 3) {
			 int no = 3 - set.size();
			 for (int x = 0; x < no; x++) {
				 //System.out.print("training sample " + number + "\n");
				 String s1 = Parser.seeds.get(PseudoRandom.randInt(0,Parser.seeds.size()-1));
				
				 

					 String[] sp1 = s1.split(":");
					 
					 Solution sol1 = new Solution(2);
					 
					 Variable[] v1 = new Variable[sp1.length];
					 for (int k = 0; k < sp1.length; k++) {		 
						 v1[k] = new Int(Integer.parseInt(sp1[k]), Integer.MIN_VALUE,Integer.MAX_VALUE);
					 }
					 
					 sol1.setDecisionVariables(v1);
					 
					 if(AutoRun.isNegative) {
						 sol1.setObjective(0, -1.0 * Parser.map1.get(s1)); 
					 } else {
						 sol1.setObjective(0, Parser.map1.get(s1));
					 }
						 
					 
					 
					 //
					 sol1.setObjective(1, Parser.map2.get(s1));
					 
					 
					 set.add(sol1);
					 String v = "";
					for(int i = 0; i < sol1.getDecisionVariables().length; i++) {
							v += v.equals("")? (int)sol1.getDecisionVariables()[i].getValue() : ":" + (int)sol1.getDecisionVariables()[i].getValue();
					 }
					 evaluated.add(v);
				 }
				 
			 
			 
			 System.out.print("After training set: " + set.size() + "\n" );
		 }
		 
		 return set;
		
	}
	
	/**
	 * 
	 * @param s
	 * @return first if train, second if increase
	 * @throws JMException
	 */
	public boolean[] evaluate(Solution s) throws JMException {
		String v = "";
		for(int i = 0; i < s.getDecisionVariables().length; i++) {
			v += v.equals("")? (int)s.getDecisionVariables()[i].getValue() : ":" + (int)s.getDecisionVariables()[i].getValue();
		}
	
		
		/*if(obj_index == 2) {
			map = Parser.map1;
		}*/
		
		//if(map.containsKey(v)) {
		//	System.out.print(map.containsKey(v) + ": " + v + "***\n");
		//}
		
		if(evaluated.contains(v)) {
			return new boolean[] {false,false};
		}
		
		evaluated.add(v);
		
		if(Parser.map1.containsKey(v)) {
			double r1 = Parser.map1.get(v);
			double r2 = Parser.map2.get(v);
			if(!Parser.map1.containsKey(v) || !Parser.map2.containsKey(v)) {
				return new boolean[] {false,true};
			}
			
			if(Parser.map1.get(v) == 0 || Parser.map2.get(v) == 0) {
				return new boolean[] {false,true};
			}
			
			
			 
			 if(AutoRun.isNegative) {
				 s.setObjective(0, -1.0*r1);
			 } else {
				 s.setObjective(0, r1);
			 }
			
			//
			s.setObjective(1, r2);
			
			
			
			return new boolean[] {true,true};
		} else {
			//System.out.print("cannot found " +v+"\n");
			return new boolean[] {false,true};
		}
		
	
	}
	
	
	
	public void train (SolutionSet solutionSet, int obj) throws JMException {
		
		for (int k = 0; k < obj; k++) {
					
			
			ArrayList<Attribute> attrs = new ArrayList<Attribute>();
			
			for (int g = 0; g < Repository.getSortedControlPrimitives(o.get(0)).size(); g++) {
				//System.out.print(Repository.getSortedControlPrimitives(o.get(0)).get(g).getName() + "***\n");
				attrs.add(new Attribute(Repository.getSortedControlPrimitives(o.get(0)).get(g).getName(), g));
			}
			
			attrs.add(new Attribute("smbo_output", attrs.size()));
			
			Instances dataset = new Instances("data_instances", attrs, 0);
			datasets[k] = dataset;
			dataset.setClassIndex(dataset.numAttributes() - 1);
			
			for (int i = 0; i < solutionSet.size(); i++) {
				Instance trainInst = new DenseInstance(dataset.numAttributes());
				
				String v = "";
				for (int j = 0; j < dataset.numAttributes() - 1; j++) {
					
					v += v.equals("")? (int)solutionSet.get(i).getDecisionVariables()[j].getValue() : ":" +  (int)solutionSet.get(i).getDecisionVariables()[j].getValue();
					//(FEMOSAASolution) solutionSet.get(i)
					
					//trainInst.setValue(j, ));
				}
				
				
				
				
				String raw = Parser.index_to_raw.get(v);
				String[] split = raw.split(":");
				
				for (int l = 0; l < split.length; l++) {
					trainInst.setValue(l, Double.parseDouble(split[l]));
				}

				
				trainInst.setValue(dataset.numAttributes() - 1, solutionSet.get(i).getObjective(k));
				trainInst.setDataset(dataset);
				dataset.add(trainInst);
			}
			
			models[k] = new NewRandomForest();//new M5P();new REPTree() (REPTree is the CART for FLASH)
			try {
				models[k].buildClassifier(dataset);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * This is actually randomly use the first 1000 samples, which is the same as random search
	 * @param o
	 * @return
	 * @throws Exception
	 */
	public Solution grid (List<Objective> o) throws Exception {
		
		if(unevaluated.size() == 0) {
			for (String s : Parser.map1.keySet()) {
				unevaluated.add(s);
			}
		}
		
		
		for (String s : evaluated) {
			if(unevaluated.contains(s)) {
				unevaluated.remove(s);
			}
		}
		
		String v = "";
		double best = Double.MAX_VALUE;
		Instance best_inst = null;
		int index = 0;
		if(weights[0] == 1.0) {
			index = 0;			
		} else {
			index = 1;
		}
		
		int cap = 0;
		
		List<String> list = new ArrayList<String>();
		list.addAll(unevaluated);
		
		Collections.shuffle(list);
		
		for (String s : list) {
			String raw = Parser.index_to_raw.get(s);
			
			String[] split = raw.split(":");
		
			
			final Instance inst = new DenseInstance(split.length + 1);
			for (int i = 0; i < split.length; i++) {
				inst.setValue(i, Double.parseDouble(split[i]));
			}
			inst.setDataset(datasets[index]);
			double r = models[index].distributionForInstance(inst)[0];
			
			//System.out.print("predict " + r + "\n");
			
			if (r < best) {
				v = s;
				best_inst = inst;
			}
			cap++;
			if(cap >= 1000) {
				break;
			}
		}
		
		Solution sol1 = new Solution(2);
		String[] sp1 = v.split(":"); 
		 Variable[] v1 = new Variable[sp1.length];
		 for (int k = 0; k < sp1.length; k++) {		 
			 v1[k] = new Int(Integer.parseInt(sp1[k]), Integer.MIN_VALUE,Integer.MAX_VALUE);
		 }
		 
		 sol1.setDecisionVariables(v1);
		 
		 sol1.setObjective(0, models[0].distributionForInstance(best_inst)[0]);
		 sol1.setObjective(1, models[1].distributionForInstance(best_inst)[0]);
		 
		 return sol1;
	}
	
    public Solution true_grid (List<Objective> o) throws Exception {
		
		if(unevaluated.size() == 0) {
			//for (String s : Parser.map1.keySet()) {
			//	unevaluated.add(s);
			//}
			getAllConfig("", unevaluated, 0);
		}
		
		
		for (String s : evaluated) {
			if(unevaluated.contains(s)) {
				unevaluated.remove(s);
			}
		}
		
		String v = "";
		double best = Double.MAX_VALUE;
		Instance best_inst = null;
		int index = 0;
		if(weights[0] == 1.0) {
			index = 0;			
		} else {
			index = 1;
		}
		
	
		
		List<String> list = new ArrayList<String>();
		list.addAll(unevaluated);
		
		//System.out.print("unevaluated: " + unevaluated.size() + "\n");
		
		for (String s : list) {
			String raw = "";
			if(Parser.index_to_raw.containsKey(s)) {
			    raw = Parser.index_to_raw.get(s);
			} else {
				String[] sp = s.split(":");
				for (int i = 0; i < sp.length; i++) {
					
					
					List<Double> l = Parser.raw_map.get(i);
					//System.out.print(i + " : " + l.size() + "\n");
					double d = l.get(Integer.parseInt(sp[i]));
					
					if (i < sp.length - 1) {
						raw += d + ":";
					} else {
						raw += d + ":";
					}
					
				}
			}
			
			
			
			String[] split = raw.split(":");
		
			
			final Instance inst = new DenseInstance(split.length + 1);
			for (int i = 0; i < split.length; i++) {
				inst.setValue(i, Double.parseDouble(split[i]));
			}
			inst.setDataset(datasets[index]);
			double r = models[index].distributionForInstance(inst)[0];
			
			//System.out.print("predict " + r + "\n");
			
			if (r < best) {
				v = s;
				best_inst = inst;
			}
			
		}
		
		Solution sol1 = new Solution(2);
		String[] sp1 = v.split(":"); 
		 Variable[] v1 = new Variable[sp1.length];
		 for (int k = 0; k < sp1.length; k++) {		 
			 v1[k] = new Int(Integer.parseInt(sp1[k]), Integer.MIN_VALUE,Integer.MAX_VALUE);
		 }
		 
		 sol1.setDecisionVariables(v1);
		 
		 sol1.setObjective(0, models[0].distributionForInstance(best_inst)[0]);
		 sol1.setObjective(1, models[1].distributionForInstance(best_inst)[0]);
		 
		 return sol1;
	}
    
    public Solution true_grid_EI (double eta, List<Objective> o, Set<String> full_candidates) throws Exception {
		
		
		
		String v = "";
		double best = Double.NEGATIVE_INFINITY;
		Instance best_inst = null;
		int index = 0;
		if(weights[0] == 1.0) {
			index = 0;			
		} else {
			index = 1;
		}
		
	
		
		List<String> list = new ArrayList<String>();
		list.addAll(full_candidates);
		
		//System.out.print("unevaluated: " + unevaluated.size() + "\n");
		
		for (String s : list) {
			String raw = "";
			if(Parser.index_to_raw.containsKey(s)) {
			    raw = Parser.index_to_raw.get(s);
			} else {
				String[] sp = s.split(":");
				for (int i = 0; i < sp.length; i++) {
					
					
					List<Double> l = Parser.raw_map.get(i);
					//System.out.print(i + " : " + l.size() + "\n");
					double d = l.get(Integer.parseInt(sp[i]));
					
					if (i < sp.length - 1) {
						raw += d + ":";
					} else {
						raw += d + ":";
					}
					
				}
			}
			
			
			
			String[] split = raw.split(":");
		
			
			final Instance inst = new DenseInstance(split.length + 1);
			for (int i = 0; i < split.length; i++) {
				inst.setValue(i, Double.parseDouble(split[i]));
			}
			inst.setDataset(datasets[index]);
			//double r = models[index].distributionForInstance(inst)[0];
			
			
			double[] array = ((NewRandomForest)models[index]).getMeanAndSTD(inst);
			double r = getEI(eta, array[0], array[1]);
			
			
			//System.out.print("predict " + r + "\n");
			
			
			if (r > best && !evaluated.contains(s)) {
				v = s;
				best_inst = inst;
			}
			
		}
		//System.out.print("predict " + list.size() + "\n");
		Solution sol1 = new Solution(2);
		String[] sp1 = v.split(":"); 
		 Variable[] v1 = new Variable[sp1.length];
		 for (int k = 0; k < sp1.length; k++) {		 
			 v1[k] = new Int(Integer.parseInt(sp1[k]), Integer.MIN_VALUE,Integer.MAX_VALUE);
		 }
		 
		 sol1.setDecisionVariables(v1);
		 
		 sol1.setObjective(0, models[0].distributionForInstance(best_inst)[0]);
		 sol1.setObjective(1, models[1].distributionForInstance(best_inst)[0]);
		 
		 return sol1;
	}
    
    private void getAllConfig(String config, Set<String> list, int k) {
    	
    	String[] sp = Parser.bounds.split(":");
    	int no = Integer.parseInt(sp[k]);
    	for (int i = 0; i <= no; i++) {
    		
    		if(k < sp.length - 1) {
    				getAllConfig(config+":"+i,list, k+1);			
    			
    		} else {
    			String f = config+":"+i;
    			f = f.replaceFirst(":", "");
    			//System.out.print(f + "\n");
    			list.add(f);
    		}
    		
    	}
    	
    }
    
  private void getAllConfigImportance(String config, List<ControlPrimitive> entry, List<ControlPrimitive> top, Set<String> list, int k) {
    	
    	
	  
	    ControlPrimitive cp = top.get(k);
    	double no = cp.getValueVector()[cp.getValueVector().length - 1];
    	for (int i = 0; i <= no; i++) {
    		
    		if(k < top.size() - 1) {
    			
    			int index = entry.indexOf(cp);
    			String[] sp = config.split(":");
    			String temp = "";
    			for (int j = 0; j < sp.length; j++) {
    				if(j == index) {
    					temp += ":" + i;
    				} else {
    					temp += ":" + sp[j];
    				}
    			}
    			temp = temp.replaceFirst(":", "");
    			getAllConfigImportance(temp, entry, top, list, k+1);		
    			
    		} else {
    			
    			int index = entry.indexOf(cp);
    			String[] sp = config.split(":");
    			String temp = "";
    			for (int j = 0; j < sp.length; j++) {
    				if(j == index) {
    					temp += ":" + i;
    				} else {
    					temp += ":" + sp[j];
    				}
    			}
    			temp = temp.replaceFirst(":", "");
    			//System.out.print(f + "\n");
    			list.add(temp);
    		}
    		
    	}
    	
    }
  
  private int countUnimportantOptions(int C, int current_meansurement) {
		double decay_point = 0.5;
		
		int offset = 20;
		
		double scale = 10;
		
		
		
		double ro = ((scale * scale) / (2 * Math.log(decay_point))) * -1.0;
		
		double t = Math.exp(((Math.pow(Math.max(0, current_meansurement - offset),2) / (2 * ro)) * -1.0));
	
		
		//System.out.print((p <= t ) + " : " + p +  " : " + t + "\n");
		
		int r = (int)Math.round(C * t);
		return r == 0? 1 : r;
	}
  
  private double getEI(double eta, double m, double s) {
	  double z = (eta - m) / s;
	  NormalDistribution norm = new NormalDistribution();
	  return (eta - m) * norm.cumulativeProbability(z) + s * norm.probability(z);
  }
	
	public Solution random (List<Objective> o) {
		Region.selected = OptimizationType.RS;

		RSRegion moead = new RSRegion(weights);
		moead.addObjectives(o);
		long time = System.currentTimeMillis();
		return moead.raw_optimize();		
		
	}
	
	public Solution NSGAII (List<Objective> o) {
		Region.selected = OptimizationType.NSGAII;

		NSGAIIwithKAndDRegion moead = new NSGAIIwithKAndDRegion(index);
		moead.addObjectives(o);
		long time = System.currentTimeMillis();
		return moead.raw_optimize();		
		
	}
	
	public Solution IBEA (List<Objective> o) {
		Region.selected = OptimizationType.IBEA;

		IBEAwithKAndDRegion moead = new IBEAwithKAndDRegion(index);
		moead.addObjectives(o);
		long time = System.currentTimeMillis();
		return moead.raw_optimize();		
		
	}
	
	public static class NewRandomForest extends RandomForest  {
		
		public double[] getMeanAndSTD(Instance inst)  {
			return ((NewBagging)m_bagger).getMeanAndSTD(inst);
		}
		 /**
		   * Builds a classifier for a set of instances.
		   * 
		   * @param data the instances to train the classifier with
		   * @throws Exception if something goes wrong
		   */
		  @Override
		  public void buildClassifier(Instances data) throws Exception {

		    
		    // can classifier handle the data?
		    getCapabilities().testWithFail(data);

		    // remove instances with missing class
		    data = new Instances(data);
		    data.deleteWithMissingClass();

		    m_bagger = new NewBagging();

		    // RandomTree implements WeightedInstancesHandler, so we can
		    // represent copies using weights to achieve speed-up.
		    m_bagger.setRepresentCopiesUsingWeights(true);

		    RandomTree rTree = new RandomTree();

		    // set up the random tree options
		    m_KValue = m_numFeatures;
		    if (m_KValue < 1) {
		      m_KValue = (int) Utils.log2(data.numAttributes() - 1) + 1;
		    }
		    rTree.setKValue(m_KValue);
		    rTree.setMaxDepth(getMaxDepth());
		    rTree.setDoNotCheckCapabilities(true);
		    rTree.setBreakTiesRandomly(getBreakTiesRandomly());

		    // set up the bagger and build the forest
		    m_bagger.setClassifier(rTree);
		    m_bagger.setSeed(m_randomSeed);
		    m_bagger.setNumIterations(m_numTrees);
		    m_bagger.setCalcOutOfBag(!getDontCalculateOutOfBagError());
		    m_bagger.setNumExecutionSlots(m_numExecutionSlots);
		    m_bagger.buildClassifier(data);
		    //System.out.print(data.numAttributes() + "**\n");
		  }
		  
		  public double[] computeAverageImpurityDecreasePerAttribute(int num) {
			  try {
				return ((NewBagging)m_bagger).computeAverageImpurityDecreasePerAttribute(num);
			} catch (WekaException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			  
			  return null;
		  }
	}
	
	  public String convertFullInfo(Solution s) {
		  //System.out.print(s.numberOfVariables() + "\n");
		  String data = "";
		  try {
		 

			for(int i = 0; i < s.getDecisionVariables().length; i++) {
				
				data +=  s.getDecisionVariables()[i].getValue() + (i ==  s.getDecisionVariables().length - 1? "" : ":");
			}
			data += "=";
			
			for(int i = 0; i < s.numberOfObjectives(); i++) {		
				data +=  s.getObjective(i) + (i ==  s.numberOfObjectives() - 1? "" : ",");
			}
			
			
		  } catch (Exception e) {
			  e.printStackTrace();
		  }
		  
		  return data;
	  }
	
	public static class NewBagging extends Bagging {
		
		  
		 /**
		   * Computes the average impurity decrease per attribute over the trees
		   *
		   * @param nodeCounts an optional array that, if non-null, will hold the count
		   *          of the number of nodes at which each attribute was used for
		   *          splitting
		   * @return the average impurity decrease per attribute over the trees
		   */
		  public double[] computeAverageImpurityDecreasePerAttribute(int num) throws WekaException {
			 // System.out.print(num + "**\n");
			double[] nodeCounts = new double[num];

		    if (m_Classifiers == null) {
		      throw new WekaException("Classifier has not been built yet!");
		    }

		   

		    double[] impurityDecreases = new double[num];
		    /*if (nodeCounts == null) {
		      nodeCounts = new double[m_data.numAttributes()];
		    }*/
		    
		    for (weka.classifiers.Classifier c : m_Classifiers) {
		      double[][] forClassifier = ((RandomTree) c).getImpurityDecreases();
		      //System.out.print(forClassifier.length + "**\n");
		      for (int i = 0; i < num; i++) {
		        impurityDecreases[i] += forClassifier[i][0];
		        nodeCounts[i] += forClassifier[i][1];
		      }
		    }
		    for (int i = 0; i < num; i++) {
		      if (nodeCounts[i] > 0) {
		        impurityDecreases[i] /= nodeCounts[i];
		      }
		    }

		    return impurityDecreases;
		  }
		  
		  public double[] getMeanAndSTD(Instance inst)  {
			  double[] results = new double[m_Classifiers.length];
			  int k = 0;
			  for (weka.classifiers.Classifier c : m_Classifiers) {
				  try {
					results[k] = ((RandomTree) c).distributionForInstance(inst)[0];
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				  k++;
			  }
			  
			  return calculateSD(results);
		  }
		  
		  public static double[] calculateSD(double numArray[])
		    {
		        double sum = 0.0, standardDeviation = 0.0;
		        int length = numArray.length;

		        for(double num : numArray) {
		            sum += num;
		        }

		        double mean = sum/length;

		        for(double num: numArray) {
		            standardDeviation += Math.pow(num - mean, 2);
		        }

		        return new double[] {mean, Math.sqrt(standardDeviation/length)};
		    }
	}
	
	public static class Tuple{
		public ControlPrimitive p;
		public double importance;
	}

}
