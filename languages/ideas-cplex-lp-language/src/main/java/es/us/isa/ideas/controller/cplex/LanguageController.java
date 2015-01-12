package es.us.isa.ideas.controller.cplex;

import ilog.concert.IloException;
import ilog.cplex.IloCplex;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.us.isa.ideas.common.AppResponse;
import es.us.isa.ideas.common.AppResponse.Status;
import es.us.isa.ideas.module.controller.BaseLanguageController;

@Controller
@RequestMapping("/language")
public class LanguageController extends BaseLanguageController {

	@RequestMapping(value = "/operation/{id}/execute", method = RequestMethod.POST)
	@ResponseBody
	public AppResponse executeOperation(String id, String content, String fileUri) {

		AppResponse appResponse = new AppResponse();
		
		if(id.equals("execute")){
			
			try {				
				// create a temp file
				File temp = File.createTempFile("cplex-lp-temp", ".lp");
				
				//write it
	    	    BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
	    	    bw.write(content);
	    	    bw.close();

				IloCplex cplex = new IloCplex();
				cplex.importModel(temp.getAbsolutePath());
				
				StringBuilder sb = new StringBuilder();
				sb.append("Has solution: " + cplex.solve());
				sb.append("\nOptimal solution: " + cplex.getObjValue());
				
				appResponse.setMessage("<pre>" + sb.toString() + "</pre>");
				appResponse.setStatus(Status.OK);
				
			} catch (IloException e) {
				appResponse.setMessage("<pre>" + e.getMessage() + "</pre>");
				appResponse.setStatus(Status.OK_PROBLEMS);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			/*
			
			try {
				
				IloEnv env = new IloEnv();
				IloCplex cplex = new IloCplex();
				IloModel model = (IloModel) cplex;
				
				
				//variables
				
				IloNumVar x = cplex.numVar(0, 40);
				IloNumVar y = cplex.numVar(0, 40);
				
				
				//expresiones
				
				// 0.12*x + 0.15*y
				IloNumExpr exp = cplex.sum(cplex.prod(0.12, x), cplex.prod(0.15, y));
				
//				IloObjective cost = cplex.addMinimize(exp);
				cplex.addMinimize(exp);

				
				//constraints
				
				// 60*x + 60*y >= 300
				IloRange cons01 = cplex.addGe(cplex.sum(cplex.prod(60, x),  cplex.prod(60, y)), 300);

				// 12*x + 6*y >= 36
				IloRange cons02 = cplex.addGe(cplex.sum(cplex.prod(12, x),  cplex.prod(6, y)), 36);
				
				// 10*x + 30*y >= 90
				IloRange cons03 = cplex.addGe(cplex.sum(cplex.prod(10, x),  cplex.prod(30, y)), 90);
				
				model.add(cons01);
				model.add(cons02);
				model.add(cons03);
				
				System.out.println("Has solution: " + cplex.solve());
				System.out.println("Optimal solution: " + cplex.getObjValue());
				System.out.println("Reduced cost of x: " + cplex.getReducedCost(x));
				System.out.println("Reduced cost of y: " + cplex.getReducedCost(y));
				System.out.println("Dual of cons01: " + cplex.getDual(cons01));
				System.out.println("Dual of cons02: " + cplex.getDual(cons02));
				System.out.println("Dual of cons03: " + cplex.getDual(cons03));
				
				
			} catch (IloException e) {
				e.printStackTrace();
			}
			*/
		}
		
		return appResponse;
	}

	@RequestMapping(value = "/format/{format}/checkLanguage", method = RequestMethod.POST)
	@ResponseBody
	public AppResponse checkLanguage(String id, String content, String fileUri) {

		AppResponse appResponse = new AppResponse();
		return appResponse;
	}

	@RequestMapping(value = "/convert", method = RequestMethod.POST)
	@ResponseBody
	public AppResponse convertFormat(String currentFormat, String desiredFormat, String fileUri, String content) {
		
		AppResponse appResponse = new AppResponse();
		return appResponse;
	}
}
