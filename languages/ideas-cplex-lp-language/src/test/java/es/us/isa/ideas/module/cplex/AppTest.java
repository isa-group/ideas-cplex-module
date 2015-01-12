package es.us.isa.ideas.module.cplex;

import ilog.concert.IloException;
import ilog.cp.IloCP;
import ilog.cplex.IloCplex;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import es.us.isa.iagree.errors.IAgreeErrorListener;
import es.us.isa.iagree.model.Agreement;
import es.us.isa.iagree.parser.MiAgreeListener;
import es.us.isa.iagree.parser.iAgreeLexer;
import es.us.isa.iagree.parser.iAgreeParser;
import es.us.isa.iagree.parser.iAgreeParser.EntryContext;

public class AppTest {
	public static void main(String[] args) {

		String content = "Maximize" + "\n"
				+ "obj: x1 + 2 x2 + 3 x3 + x4" + "\n"
				+ "Subject To" + "\n"
				+ "c1: - x1 + x2 + x3 + 10 x4 <= 20" + "\n"
				+ "c2: x1 - 3 x2 + x3 <= 30" + "\n"
				+ "c3: x2 - 3.5 x4 = 0" + "\n"
				+ "Bounds" + "\n"
				+ "0 <= x1 <= 40" + "\n"
				+ "2 <= x4 <= 3" + "\n"
				+ "General" + "\n"
				+ "x4" + "\n"
				+ "End";
		
		test_cplex(content);

		// String sample = loadFile("test_template.iagreetemplate");
		// // String sample = loadFile("test_offer.iagreeoffer");
		// // System.out.println(sample);
		//
		// Agreement ad = createModel(sample, true);
		// String iagree = WriteriAgree.createFile(ad);
		//
		// CplexAnalyzer an = new CplexAnalyzer();
		//
		// ValidOp op = new ValidOp();
		// op.addDocument(ad);
		// op.execute(an);
		// System.out.println(op.isValid());
		// System.out.println(op.getExplanation());

	}

	private static Agreement createModel(String sample, Boolean simplified) {

		iAgreeLexer lexer = new iAgreeLexer(new ANTLRInputStream(sample));

		// Get a list of matched tokens
		CommonTokenStream tokens = new CommonTokenStream(lexer);

		// Pass the tokens to the parser
		iAgreeParser parser = new iAgreeParser(tokens);

		IAgreeErrorListener errorListener = new IAgreeErrorListener();
		parser.addErrorListener(errorListener);

		// Specify our entry point
		EntryContext context = parser.entry();

		// Walk it and attach our listener
		MiAgreeListener listener = new MiAgreeListener();
		listener.setSimplified(simplified);
		ParseTreeWalker walker = new ParseTreeWalker();
		walker.walk(listener, context);

		Map<String, Object> res = new HashMap<String, Object>();
		String metricUri = listener.getMetricUri();
		String metrics = listener.getMetrics();

		return listener.getDocument();
	}

	private static void test_cplex(String content) {

		try {

			// create a temp file
			File temp = File.createTempFile("cplex-temp", ".lp");
			
			//write it
    	    BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
    	    bw.write(content);
    	    bw.close();

			IloCplex cplex = new IloCplex();
			cplex.importModel(temp.getAbsolutePath());
			System.out.println("\n\nHas solution: " + cplex.solve());
			System.out.println("Optimal solution: " + cplex.getObjValue());
		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * try { IloEnv env = new IloEnv(); IloCplex cplex = new IloCplex();
		 * IloModel model = (IloModel) cplex;
		 * 
		 * //variables
		 * 
		 * IloNumVar x = cplex.numVar(0, 40); IloNumVar y = cplex.numVar(0, 40);
		 * 
		 * 
		 * //expresiones
		 * 
		 * // 0.12*x + 0.15*y IloNumExpr exp = cplex.sum(cplex.prod(0.12, x),
		 * cplex.prod(0.15, y));
		 * 
		 * // IloObjective cost = cplex.addMinimize(exp);
		 * cplex.addMinimize(exp);
		 * 
		 * 
		 * //constraints
		 * 
		 * // 60*x + 60*y >= 300 IloRange cons01 =
		 * cplex.addGe(cplex.sum(cplex.prod(60, x), cplex.prod(60, y)), 300);
		 * 
		 * // 12*x + 6*y >= 36 IloRange cons02 =
		 * cplex.addGe(cplex.sum(cplex.prod(12, x), cplex.prod(6, y)), 36);
		 * 
		 * // 10*x + 30*y >= 90 IloRange cons03 =
		 * cplex.addGe(cplex.sum(cplex.prod(10, x), cplex.prod(30, y)), 90);
		 * 
		 * model.add(cons01); model.add(cons02); model.add(cons03);
		 * 
		 * System.out.println("Has solution: " + cplex.solve());
		 * System.out.println("Optimal solution: " + cplex.getObjValue());
		 * System.out.println("Reduced cost of x: " + cplex.getReducedCost(x));
		 * System.out.println("Reduced cost of y: " + cplex.getReducedCost(y));
		 * System.out.println("Dual of cons01: " + cplex.getDual(cons01));
		 * System.out.println("Dual of cons02: " + cplex.getDual(cons02));
		 * System.out.println("Dual of cons03: " + cplex.getDual(cons03));
		 * 
		 * 
		 * } catch (IloException e) { e.printStackTrace(); }
		 */
	}

	public static String loadFile(String filePath) {
		// Location of file to read
		File f = new File(filePath);
		FileInputStream is;
		String res = "";
		try {
			is = new FileInputStream(f);
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line.replaceAll("	", "\t") + "\n");
			}
			res = sb.toString();
			is.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}
}