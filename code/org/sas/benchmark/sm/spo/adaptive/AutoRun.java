package org.sas.benchmark.sm.spo.adaptive;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.femosaa.core.EAConfigure;
import org.femosaa.core.SASAlgorithmAdaptor;
import org.femosaa.seed.FixedSeeder;
import org.sas.benchmark.sm.Data;
import org.ssase.requirement.froas.RequirementPrimitive;
import org.ssase.requirement.froas.RequirementProposition;
import org.ssase.util.Repository;

public class AutoRun {
	
	private static String[] weights = new String[] { "1.0-0.0"/*"1.0-0.0", "0.0-1.0"*/ };
	private static String[] single_algs = new String[] {"irace"/*"irace", "ga", "rs"*/ };
	private static String[] multi_algs = new String[] { "nsgaii"};
	private static String[] multi_flash_algs = new String[] { "flash-nsgaii"  };
	private static String[] multi_flash_algs_raw = new String[] { "flash-raw-nsgaii"  };
	private static String[] single_flash_algs = new String[] { "flash-gs" };
	private static String benchmark = "SS-K";
	public static String form = "linear";//linear, sqrt, square
	public static int index = 0;
	public static boolean isNegative = true;
	//private static double[] w_a = new double[] {0.01,0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0,10};
	//private static double[] w_a = new double[] {0.01,0.1,0.3,0.5,0.7,0.9,1.0,10};
	//private static double[] w_a = new double[] {0.03,0.05,0.07,0.09,3,5,7,9};
	private static double[] w_a = new double[] {0.0};
	// if for measurement based, this should be ""
	private static String flash = "";//wotrigger; wodup
	public static void main(String[] args) {
		
		//SASAlgorithmAdaptor.isLogSolutionsInFull = true;
		
		//so();
		//pmo();
		//flash_so();
	    //boca_so();
		
		Parser.selected = benchmark;
		Simulator.setup();
		mo();
		
	
		/*
		SASAlgorithmAdaptor.isAdaptConstantly = false;
		SASAlgorithmAdaptor.isToFilterRedundantSolution = false;
		*/
	
		
		
		
	
	}
	
	public static void pmo() {
		Parser.selected = benchmark;
		Simulator.setup();
		SASAlgorithmAdaptor.isSeedSolution =false;
		SASAlgorithmAdaptor.isFuzzy = false;
		SASAlgorithmAdaptor.logGenerationOfObjectiveValue=-1;
		run_MOEA(-1, -1, "none");
	}
	
	public static void flash_so() {
		Parser.selected = benchmark;
		Simulator.setup();
		SASAlgorithmAdaptor.isSeedSolution = false;
		SASAlgorithmAdaptor.isFuzzy = false;
		SASAlgorithmAdaptor.logGenerationOfObjectiveValue=-1;
		SASAlgorithmAdaptor.isWeightedSumNormalized = false;
	

		File f = new File("/Users/"+System.getProperty("user.name")+"/research/monitor/ws-soa/sas");

		try {
			if (f.exists()) {
				delete(f);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		for (String p : weights) {

			String[] s = p.split("-");
			double[] w = new double[s.length];
			for (int i = 0; i < s.length; i++) {
				w[i] = Double.parseDouble(s[i]);
			}

			single_algs = single_flash_algs;
			
			for (String alg : single_algs) {
				
				/*if("ga".equals(alg) && (benchmark.equals("SS-M") || benchmark.equals("SS-N"))) {
					SASAlgorithmAdaptor.isSeedSolution = true;
				} else {
					SASAlgorithmAdaptor.isSeedSolution = true;
				}*/

				Simulator.alg = alg;
				Simulator.weights = w;

				Simulator.main_test();

				File source = new File("/Users/"+System.getProperty("user.name")+"/research/monitor/ws-soa/sas");
				File r = new File(
						"/Users/"+System.getProperty("user.name")+"/research/experiments-data/s-vs-m/configuration-optimization/"
								+ p + "/" + benchmark + "/" + alg + "/"
								+ "/sas");
				File dest = new File(
						"/Users/"+System.getProperty("user.name")+"/research/experiments-data/s-vs-m/configuration-optimization/"
								+ p + "/" + benchmark + "/" + alg + "/"
								+ "/sas");

				if (r.exists()) {
					System.out.print("Remove " + r + "\n");
					try {
						delete(r);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				if (!dest.exists()) {
					dest.mkdirs();
				}

				try {
					copyFolder(source, dest);
					if (source.exists()) {
						System.out.print("Remove " + source + "\n");
						delete(source);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				System.out
						.print("End of "
								+ "/Users/"+System.getProperty("user.name")+"/research/experiments-data/s-vs-m/configuration-optimization/"
								+ p + "/" + benchmark + "/" + alg + "/" + "\n");
				// try {
				// Thread.sleep((long)2000);
				// } catch (InterruptedException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }

			}
		}

	}
	
	public static void boca_so() {
		Parser.selected = benchmark;
		Simulator.setup();
		SASAlgorithmAdaptor.isSeedSolution = false;
		SASAlgorithmAdaptor.isFuzzy = false;
		SASAlgorithmAdaptor.logGenerationOfObjectiveValue=-1;
		SASAlgorithmAdaptor.isWeightedSumNormalized = false;
	

		File f = new File("/Users/"+System.getProperty("user.name")+"/research/monitor/ws-soa/sas");

		try {
			if (f.exists()) {
				delete(f);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		for (String p : weights) {

			String[] s = p.split("-");
			double[] w = new double[s.length];
			for (int i = 0; i < s.length; i++) {
				w[i] = Double.parseDouble(s[i]);
			}

			single_algs = new String[] {"boca"};
			
			for (String alg : single_algs) {
				
				/*if("ga".equals(alg) && (benchmark.equals("SS-M") || benchmark.equals("SS-N"))) {
					SASAlgorithmAdaptor.isSeedSolution = true;
				} else {
					SASAlgorithmAdaptor.isSeedSolution = true;
				}*/

				Simulator.alg = alg;
				Simulator.weights = w;

				Simulator.main_test();

				File source = new File("/Users/"+System.getProperty("user.name")+"/research/monitor/ws-soa/sas");
				File r = new File(
						"/Users/"+System.getProperty("user.name")+"/research/experiments-data/s-vs-m/configuration-optimization/"
								+ p + "/" + benchmark + "/" + alg + "/"
								+ "/sas");
				File dest = new File(
						"/Users/"+System.getProperty("user.name")+"/research/experiments-data/s-vs-m/configuration-optimization/"
								+ p + "/" + benchmark + "/" + alg + "/"
								+ "/sas");

				if (r.exists()) {
					System.out.print("Remove " + r + "\n");
					try {
						delete(r);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				if (!dest.exists()) {
					dest.mkdirs();
				}

				try {
					copyFolder(source, dest);
					if (source.exists()) {
						System.out.print("Remove " + source + "\n");
						delete(source);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				System.out
						.print("End of "
								+ "/Users/"+System.getProperty("user.name")+"/research/experiments-data/s-vs-m/configuration-optimization/"
								+ p + "/" + benchmark + "/" + alg + "/" + "\n");
				// try {
				// Thread.sleep((long)2000);
				// } catch (InterruptedException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }

			}
		}

	}
	
	public static void flash_mo () {
		//double l = Double.MAX_VALUE/0.001;
				//3.0,48.89200000000001 3.0,33.305
				//System.out.print(l > 1);
				
				//if(1==1) return;
				Parser.selected = benchmark;
				Simulator.setup();
				SASAlgorithmAdaptor.isSeedSolution = false;
				SASAlgorithmAdaptor.isFuzzy = true;
				SASAlgorithmAdaptor.logGenerationOfObjectiveValue=-1;
			

				File f = new File("/Users/"+System.getProperty("user.name")+"/research/monitor/ws-soa/sas");

				try {
					if (f.exists()) {
						delete(f);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				String[] forms = new String[] {"linear"/*"linear", "sqrt", "square"*/};
				int[] ii = new int[] {0,1/*0,1*/};
			    //index = 0;
				//double best = Double.MAX_VALUE;
				//double best_w = 0.0;
				
				multi_algs = multi_flash_algs;

				for (String ff : forms) {

					for (int i : ii) {
						index = i;
						for (double w : w_a) {
							form = ff;
							CombineProposition.beta = w;
							run_MOEA(index, w, form);
							/*
							 * double a12 = Data.readObjective(index, form);
							 * 
							 * if (a12 > best) { best = a12; best_w = w; }
							 */

							/*
							 * double m = Data.readObjectiveMedian(index, ff);
							 * 
							 * if (m < best) { best = m; best_w = w; }
							 */
							System.out.print("Objective: " + index + ", on form: " + form + ", w: " + w + ", i: " + i);
						}

						
					}
				}
				
				
				SASAlgorithmAdaptor.isFuzzy = false;
				//run_MOEA(-1, -1, "none");

				/*form = "linear";
				CombineProposition.beta = 10;
				run_MOEA(form);
				form = "sqrt";
				CombineProposition.beta = 0.8;
				run_MOEA(form);
				form = "square";
				CombineProposition.beta = 0.01;
				run_MOEA(form);*/
	}
	
	
	
	public static void mo() {
		//double l = Double.MAX_VALUE/0.001;
				//3.0,48.89200000000001 3.0,33.305
				//System.out.print(l > 1);
				
				//if(1==1) return;
				//Parser.selected = benchmark;
				//Simulator.setup();
				SASAlgorithmAdaptor.isSeedSolution = false;
				SASAlgorithmAdaptor.isFuzzy = true;
				SASAlgorithmAdaptor.logGenerationOfObjectiveValue=-1;
			

				File f = new File("/Users/"+System.getProperty("user.name")+"/research/monitor/ws-soa/sas");

				try {
					if (f.exists()) {
						delete(f);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				String[] forms = new String[] {"linear"/*"linear", "sqrt", "square"*/};
				int[] ii = new int[] {index/*0,1*/};
			    //index = 0;
				//double best = Double.MAX_VALUE;
				//double best_w = 0.0;

				for (String ff : forms) {

					for (int i : ii) {
						index = i;
						for (double w : w_a) {
							form = ff;
							CombineProposition.beta = w;
							run_MOEA(index, w, form);
							//run_MOEA(index, form);
							/*
							 * double a12 = Data.readObjective(index, form);
							 * 
							 * if (a12 > best) { best = a12; best_w = w; }
							 */

							/*
							 * double m = Data.readObjectiveMedian(index, ff);
							 * 
							 * if (m < best) { best = m; best_w = w; }
							 */
							System.out.print("Objective: " + index + ", on form: " + form + ", w: " + w + ", i: " + i);
						}

						
					}
				}
				
				
				SASAlgorithmAdaptor.isFuzzy = false;
				//run_MOEA(-1, -1, "none");

				/*form = "linear";
				CombineProposition.beta = 10;
				run_MOEA(form);
				form = "sqrt";
				CombineProposition.beta = 0.8;
				run_MOEA(form);
				form = "square";
				CombineProposition.beta = 0.01;
				run_MOEA(form);*/
	}
	
	public static void mo_extra() {
		//double l = Double.MAX_VALUE/0.001;
				//3.0,48.89200000000001 3.0,33.305
				//System.out.print(l > 1);
				
				//if(1==1) return;
				Parser.selected = benchmark;
				Simulator.setup();
				SASAlgorithmAdaptor.isSeedSolution = false;
				SASAlgorithmAdaptor.isFuzzy = true;
				SASAlgorithmAdaptor.logGenerationOfObjectiveValue=-1;
			

				File f = new File("/Users/"+System.getProperty("user.name")+"/research/monitor/ws-soa/sas");

				try {
					if (f.exists()) {
						delete(f);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				String[] forms = new String[] {"linear"/*"linear", "sqrt", "square"*/};
				int[] ii = new int[] {0,1/*0,1*/};
			    //index = 0;
				//double best = Double.MAX_VALUE;
				//double best_w = 0.0;

				for (String ff : forms) {

					for (int i : ii) {
						index = i;
						for (double w : w_a) {
							form = ff;
							CombineProposition.beta = w;
							//run_MOEA(index, w, form);
							run_MOEA(index, form);
							/*
							 * double a12 = Data.readObjective(index, form);
							 * 
							 * if (a12 > best) { best = a12; best_w = w; }
							 */

							/*
							 * double m = Data.readObjectiveMedian(index, ff);
							 * 
							 * if (m < best) { best = m; best_w = w; }
							 */
							System.out.print("Objective: " + index + ", on form: " + form + ", w: " + w + ", i: " + i);
						}

						
					}
				}
				
				
				SASAlgorithmAdaptor.isFuzzy = false;
				//run_MOEA(-1, -1, "none");

				/*form = "linear";
				CombineProposition.beta = 10;
				run_MOEA(form);
				form = "sqrt";
				CombineProposition.beta = 0.8;
				run_MOEA(form);
				form = "square";
				CombineProposition.beta = 0.01;
				run_MOEA(form);*/
	}
	
	public static void so() {
		Parser.selected = benchmark;
		Simulator.setup();
		SASAlgorithmAdaptor.isSeedSolution = false;
		SASAlgorithmAdaptor.isFuzzy = false;
		SASAlgorithmAdaptor.logGenerationOfObjectiveValue=-1;
		SASAlgorithmAdaptor.isWeightedSumNormalized = false;
		SASAlgorithmAdaptor.isAdaptiveWeightinMMO = false;
		SASAlgorithmAdaptor.isToFilterRedundantSolution = false;
		SASAlgorithmAdaptor.isBoundNormalizationForTarget = false;
	

		File f = new File("/Users/"+System.getProperty("user.name")+"/research/monitor/ws-soa/sas");

		try {
			if (f.exists()) {
				delete(f);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		for (String p : weights) {

			String[] s = p.split("-");
			double[] w = new double[s.length];
			for (int i = 0; i < s.length; i++) {
				w[i] = Double.parseDouble(s[i]);
			}

			for (String alg : single_algs) {
				
				/*if("ga".equals(alg) && (benchmark.equals("SS-M") || benchmark.equals("SS-N"))) {
					SASAlgorithmAdaptor.isSeedSolution = true;
				} else {
					SASAlgorithmAdaptor.isSeedSolution = true;
				}*/

				Simulator.alg = alg;
				Simulator.weights = w;

				Simulator.main_test();

				File source = new File("/Users/"+System.getProperty("user.name")+"/research/monitor/ws-soa/sas");
				File r = new File(
						"/Users/"+System.getProperty("user.name")+"/research/experiments-data/s-vs-m/configuration-optimization/"
								+ p + "/" + benchmark + "/" + alg + "/"
								+ "/sas");
				File dest = new File(
						"/Users/"+System.getProperty("user.name")+"/research/experiments-data/s-vs-m/configuration-optimization/"
								+ p + "/" + benchmark + "/" + alg + "/"
								+ "/sas");

				if (r.exists()) {
					System.out.print("Remove " + r + "\n");
					try {
						delete(r);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				if (!dest.exists()) {
					dest.mkdirs();
				}

				try {
					copyFolder(source, dest);
					if (source.exists()) {
						System.out.print("Remove " + source + "\n");
						delete(source);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				System.out
						.print("End of "
								+ "/Users/"+System.getProperty("user.name")+"/research/experiments-data/s-vs-m/configuration-optimization/"
								+ p + "/" + benchmark + "/" + alg + "/" + "\n");
				// try {
				// Thread.sleep((long)2000);
				// } catch (InterruptedException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }

			}
		}

	}

	public static void run_MOEA(int i, double w, String form) {
		
		for (String alg : multi_algs) {
			Simulator.alg = alg;

			Simulator.main_test();

			File source = new File("/Users/"+System.getProperty("user.name")+"/research/monitor/ws-soa/sas");
			File r = new File(
					"/Users/"+System.getProperty("user.name")+"/research/experiments-data/s-vs-m/configuration-optimization/"
							+ "/" + benchmark + "/" + form  + "/" + alg + "/" + i + "/" + w + flash + "/" + "/sas");
			File dest = new File(
					"/Users/"+System.getProperty("user.name")+"/research/experiments-data/s-vs-m/configuration-optimization/"
							+ "/" + benchmark + "/" + form  + "/" + alg + "/" + i + "/" + w + flash  + "/"  + "/sas");

			if (r.exists()) {
				System.out.print("Remove " + r + "\n");
				try {
					delete(r);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (!dest.exists()) {
				dest.mkdirs();
			}

			try {
				copyFolder(source, dest);
				if (source.exists()) {
					System.out.print("Remove " + source + "\n");
					delete(source);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out
					.print("End of "
							+ "/Users/"+System.getProperty("user.name")+"/research/experiments-data/s-vs-m/configuration-optimization/"
							+ "/" + benchmark + "/" + form  + "/" + alg + "/" + i + "/" + w + flash  + "/" + "\n");

		}
		File f = new File("/Users/"+System.getProperty("user.name")+"/research/monitor/ws-soa/sas");

		try {
			if (f.exists()) {
				delete(f);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void run_MOEA(int i, String form) {
	
		for (String alg : multi_algs) {
			Simulator.alg = alg;

			Simulator.main_test();

			File source = new File("/Users/"+System.getProperty("user.name")+"/research/monitor/ws-soa/sas");
			File r = new File(
					"/Users/"+System.getProperty("user.name")+"/research/experiments-data/s-vs-m/configuration-optimization/"
							+ "/" + benchmark + "/" + form  + "/" + alg + "/" + i + "/" + "1.0*" + "/" + "/sas");
			File dest = new File(
					"/Users/"+System.getProperty("user.name")+"/research/experiments-data/s-vs-m/configuration-optimization/"
							+ "/" + benchmark + "/" + form  + "/" + alg + "/" + i + "/" + "1.0*" + "/"  + "/sas");

			if (r.exists()) {
				System.out.print("Remove " + r + "\n");
				try {
					delete(r);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (!dest.exists()) {
				dest.mkdirs();
			}

			try {
				copyFolder(source, dest);
				if (source.exists()) {
					System.out.print("Remove " + source + "\n");
					delete(source);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out
					.print("End of "
							+ "/Users/"+System.getProperty("user.name")+"/research/experiments-data/s-vs-m/configuration-optimization/"
							+ "/" + benchmark + "/" + form  + "/" + alg + "/" + i + "/" + "1.0*" + "/" + "\n");

		}
		File f = new File("/Users/"+System.getProperty("user.name")+"/research/monitor/ws-soa/sas");

		try {
			if (f.exists()) {
				delete(f);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void copyFolder(File src, File dest) throws IOException {

		if (src.isDirectory()) {

			// if directory not exists, create it
			if (!dest.exists()) {
				dest.mkdir();
				System.out.println("Directory copied from " + src + "  to "
						+ dest);
			}

			// list all the directory contents
			String files[] = src.list();

			for (String file : files) {
				// construct the src and dest file structure
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);
				// recursive copy
				copyFolder(srcFile, destFile);
			}

		} else {
			// if file, then copy it
			// Use bytes stream to support all file types
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dest);

			byte[] buffer = new byte[1024];

			int length;
			// copy the file content in bytes
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}

			in.close();
			out.close();
			System.out.println("File copied from " + src + " to " + dest);
		}

	}

	public static void delete(File file) throws IOException {

		if (file.isDirectory()) {

			// directory is empty, then delete it
			if (file.list().length == 0) {

				file.delete();
				// System.out.println("Directory is deleted : "
				// + file.getAbsolutePath());

			} else {

				// list all the directory contents
				String files[] = file.list();

				for (String temp : files) {
					// construct the file structure
					File fileDelete = new File(file, temp);

					// recursive delete
					delete(fileDelete);
				}

				// check the directory again, if empty then delete it
				if (file.list().length == 0) {
					file.delete();
					// System.out.println("Directory is deleted : "
					// + file.getAbsolutePath());
				}
			}

		} else {
			// if file, then delete it
			file.delete();
			// System.out.println("File is deleted : " +
			// file.getAbsolutePath());
		}
	}
}
