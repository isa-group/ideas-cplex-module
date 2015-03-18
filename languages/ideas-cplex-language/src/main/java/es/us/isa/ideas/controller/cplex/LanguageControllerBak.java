//package es.us.isa.ideas.controller.cplex;
//
//import ilog.concert.IloConstraint;
//import ilog.concert.IloIntVar;
//import ilog.concert.cppimpl.IloEnv;
//import ilog.cp.IloCP;
//import ilog.cplex.IloCplex;
//import ilog.opl.IloOplElement;
//import ilog.opl.IloOplErrorHandler;
//import ilog.opl.IloOplFactory;
//import ilog.opl.IloOplModel;
//import ilog.opl.IloOplModelDefinition;
//import ilog.opl.IloOplModelSource;
//import ilog.opl.IloOplSettings;
//
//import java.io.BufferedWriter;
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileWriter;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import es.us.isa.ideas.common.AppAnnotations;
//import es.us.isa.ideas.common.AppResponse;
//import es.us.isa.ideas.common.AppResponse.Status;
//import es.us.isa.ideas.controller.error.CustomErrorHandler;
//import es.us.isa.ideas.module.controller.BaseLanguageController;
//
//@Controller
//@RequestMapping("/language")
//public class LanguageControllerBak extends BaseLanguageController {
//
//	@RequestMapping(value = "/operation/{id}/execute", method = RequestMethod.POST)
//	@ResponseBody
//	public AppResponse executeOperation(String id, String content,
//			String fileUri) {
//
//		AppResponse appResponse = new AppResponse();
//
//		if (id.equals("execute")) {
//
//			ByteArrayOutputStream errors = new ByteArrayOutputStream();
//			try {
//				// create a temp file
//				
//				String[] aux = fileUri.split("/");
//				String filename = aux[aux.length-1]; 
//				
//				File temp = File.createTempFile(filename.replace(".opl", ""), ".opl");
//
//				// write it
//				BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
//				bw.write(content);
//				bw.close();
//
//				IloEnv env = new IloEnv();
//				IloOplFactory oplF = new IloOplFactory();
//				IloOplErrorHandler errHandler = oplF.createOplErrorHandler(errors);
//				IloOplModelSource modelSource = oplF.createOplModelSource(temp.getAbsolutePath());
//				
//				IloOplSettings settings = new IloOplSettings(env, errHandler);
//				IloOplModelDefinition def = oplF.createOplModelDefinition(
//						modelSource, settings);
//
//				String using = content.substring(0, content.indexOf("\n"))
//						.trim();
//				Boolean useCP = using.equals("using CP;") ? true : false;
//
//				if (useCP) {
//					System.out.println("Using CP Optimizer...");
//
//					IloCP cp = oplF.createCP();
//					cp.setOut(null);
//					cp.setParameter(IloCP.IntParam.ConflictRefinerOnVariables,
//							IloCP.ParameterValues.On);
//					IloOplModel opl = oplF.createOplModel(def, cp);
//					opl.generate();
//
////					List<IloConstraint> cts_list = new ArrayList<IloConstraint>();
////					for (Iterator<IloOplElement> it = opl.getElementIterator(); it
////							.hasNext();) {
////						IloOplElement e = it.next();
////						if (!e.isDecisionVariable() && !e.isData()) {
////							cts_list.add(e.asConstraint());
////						}
////					}
////					IloConstraint[] constraints = cts_list
////							.toArray(new IloConstraint[0]);
////					double[] prefs = new double[constraints.length];
////					for (int c1 = 0; c1 < constraints.length; c1++) {
////						prefs[c1] = 1.0;// change it per your requirements
////					}
//					
//					if (cp.solve()) {
//						StringBuilder sb = new StringBuilder();
//						ByteArrayOutputStream baos = new ByteArrayOutputStream();
//						opl.printSolution(baos);
//						sb.append(baos.toString());
//						appResponse.setMessage("<pre>" + sb.toString()
//								+ "</pre>");
//						appResponse.setStatus(Status.OK);
//					} else {
//						StringBuilder sb = new StringBuilder();
//						sb.append("The document has conflicts" + "\n");
//						appResponse.setMessage("<pre>" + sb.toString()
//								+ "</pre>");
//						appResponse.setStatus(Status.OK);
//					}
//					
//					cp.clearModel();
//					opl.end();
//					env.end();
//				} else {
//					IloCplex cplex = oplF.createCplex();
//					cplex.setOut(null);
//					IloOplModel opl = oplF.createOplModel(def, cplex);
//					opl.generate();
//
//					List<IloConstraint> cts_list = new ArrayList<IloConstraint>();
//					for (Iterator<IloOplElement> it = opl.getElementIterator(); it
//							.hasNext();) {
//						IloOplElement e = it.next();
//						if (!e.isDecisionVariable() && !e.isData()) {
//							cts_list.add(e.asConstraint());
//						}
//					}
//					IloConstraint[] constraints = cts_list
//							.toArray(new IloConstraint[0]);
//					double[] prefs = new double[constraints.length];
//					for (int c1 = 0; c1 < constraints.length; c1++) {
//						prefs[c1] = 1.0;// change it per your requirements
//					}
//					if (cplex.refineConflict(constraints, prefs)) {
//						StringBuilder sb = new StringBuilder();
//						sb.append("The document has conflicts" + "\n");
//						sb.append("\nConflict Refinement process finished: Printing Conflicts" + "\n");
//
//						IloCplex.ConflictStatus[] conflict = cplex
//								.getConflict(constraints);
//						int numConConflicts = 0;
//						int numBoundConflicts = 0;
//						int numSOSConflicts = 0;
//
//						for (int c2 = 0; c2 < constraints.length; c2++) {
//							if (conflict[c2]
//									.equals(IloCplex.ConflictStatus.Member)) {
//								sb.append("\t" + "Proved: " + constraints[c2] + "\n");
//
//							} else if (conflict[c2]
//									.equals(IloCplex.ConflictStatus.PossibleMember)) {
//								sb.append("\t" + "Possible: " + constraints[c2] + "\n");
//							}
//						}
//						sb.append("\n" + "Conflict Summary:");
//						sb.append("\n\t" + "Constraint conflicts     = "
//								+ numConConflicts);
//						sb.append("\n\t" + "Variable Bound conflicts = "
//								+ numBoundConflicts);
//						sb.append("\n\t" + "SOS conflicts            = "
//								+ numSOSConflicts);
//
//						// cplex.SetParam(Cplex.IntParam.FeasOptMode,
//						// 0);//change per feasopt requirements
//						// Relax contraints only, modify if variable bound
//						// relaxation is required
//
//						if (cplex.feasOpt(constraints, prefs)) {
//							double[] infeas = cplex
//									.getInfeasibilities(constraints);
//							// Print bound changes
//							sb.append("\n\n" + "Suggested Bound changes:" + "\n");
//
//							for (int c3 = 0; c3 < infeas.length; c3++)
//								if (infeas[c3] != 0)
//									sb.append("\t" + constraints[c3]
//											+ " ; Change = " + infeas[c3]);
//
//							sb.append("\n\nRelaxed Model's obj value = "
//									+ cplex.getObjValue());
//							sb.append("\nRelaxed Model's solution status:"
//											+ cplex.getCplexStatus());
//
//							for (Iterator<IloOplElement> it = opl
//									.getElementIterator(); it.hasNext();) {
//								IloOplElement e = it.next();
//								try {
//									if (e.isDecisionVariable()) {
//										IloIntVar var = e.asIntVar();
//										sb.append("\nRelaxed Model's Variable: "
//														+ e.getName()
//														+ "; Value = "
//														+ cplex.getValue(var));
//									}
//								} catch (Exception ex) {
//									// TODO: handle exception
//								}
//							}
//							
//							appResponse.setMessage("<pre>" + sb.toString()
//									+ "</pre>");
//							appResponse.setStatus(Status.OK);
//						} else {
//							sb.append("\nFeasOpt failed- Could not repair infeasibilities");
//							appResponse.setMessage("<pre>" + sb.toString()
//									+ "</pre>");
//							appResponse.setStatus(Status.OK);
//						}
//
//					} else if (cplex.solve()) {
//						StringBuilder sb = new StringBuilder();
//						ByteArrayOutputStream baos = new ByteArrayOutputStream();
//						opl.printSolution(baos);
//						sb.append(baos.toString());
//						appResponse.setMessage("<pre>" + sb.toString()
//								+ "</pre>");
//						appResponse.setStatus(Status.OK);
//					}
//
//					cplex.clearModel();
//					opl.end();
//					env.end();
//				}
//
//				// StringBuilder sb = new StringBuilder();
//				// sb.append("Has solution: " + cp.solve());
//				// if (cp.solve()) {
//				// sb.append("\nSolution:");
//				// IloIntVar[] vars = cp.getAllIloIntVars();
//				// for (int i = 0; i < vars.length; i++)
//				// sb.append("\n\t" + vars[i].getName() + ": " +
//				// (int)cp.getValue(vars[i]));
//				// }
//				//
//				// appResponse.setMessage("<pre>" + sb.toString() + "</pre>");
//				appResponse.setStatus(Status.OK);
//
//			} catch (Exception e) {
//				if(!errors.toString().isEmpty())
//					appResponse.setMessage("<pre>" + errors.toString() + "</pre>");
//				else
//					appResponse.setMessage("<pre>" + e.getMessage() + "</pre>");
//				appResponse.setStatus(Status.OK_PROBLEMS);
//			}
//		}
//
//		appResponse.setFileUri(fileUri);
//		
//		return appResponse;
//	}
//
//	@RequestMapping(value = "/format/{format}/checkLanguage", method = RequestMethod.POST)
//	@ResponseBody
//	public AppResponse checkLanguage(String id, String content, String fileUri) {
//
//		AppResponse appResponse = new AppResponse();
//		
//		CustomErrorHandler errorHandler = null;
//		
//		try {
//			String[] aux = fileUri.split("/");
//			String filename = aux[aux.length-1]; 
//			
//			File temp = File.createTempFile(filename.replace(".opl", ""), ".opl");
//
//			// write it
//			BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
//			bw.write(content);
//			bw.close();
//
//			IloEnv env = new IloEnv();
//			IloOplFactory oplF = new IloOplFactory();
//			errorHandler = new CustomErrorHandler(oplF);
//			
//			IloOplModelSource modelSource = oplF.createOplModelSource(temp.getAbsolutePath());
//			
//			IloOplSettings settings = new IloOplSettings(env, errorHandler);
//			IloOplModelDefinition def = oplF.createOplModelDefinition(
//					modelSource, settings);
//
//			String using = content.substring(0, content.indexOf("\n"))
//					.trim();
//			Boolean useCP = using.equals("using CP;") ? true : false;
//
//			if (useCP) {
//				IloCP cp = oplF.createCP();
//				cp.setOut(null);
//				IloOplModel opl = oplF.createOplModel(def, cp);
//				opl.generate();
//			} else {
//				IloCplex clex = oplF.createCplex();
//				clex.setOut(null);
//				IloOplModel opl = oplF.createOplModel(def, clex);
//				opl.generate();
//			}
//		} catch(Exception e){
//		}
//		
//		appResponse.setAnnotations(errorHandler.getAnnotations().toArray(new AppAnnotations[0]));
//		
//		if(errorHandler.getAnnotations().size() == 0)
//			appResponse.setStatus(Status.OK);
//		else
//			appResponse.setStatus(Status.OK_PROBLEMS);
//		
//		return appResponse;
//	}
//
//	@RequestMapping(value = "/convert", method = RequestMethod.POST)
//	@ResponseBody
//	public AppResponse convertFormat(String currentFormat,
//			String desiredFormat, String fileUri, String content) {
//
//		AppResponse appResponse = new AppResponse();
//		return appResponse;
//	}
//}
