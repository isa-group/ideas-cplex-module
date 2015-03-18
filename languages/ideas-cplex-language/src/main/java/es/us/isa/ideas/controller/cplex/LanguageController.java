package es.us.isa.ideas.controller.cplex;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.us.isa.ideas.common.AppResponse;
import es.us.isa.ideas.common.AppResponse.Status;
import es.us.isa.ideas.controller.cplex.util.Config;
import es.us.isa.ideas.controller.cplex.util.OperationResponse;
import es.us.isa.ideas.controller.cplex.util.Util;
import es.us.isa.ideas.module.controller.BaseLanguageController;

@Controller
@RequestMapping("/language")
public class LanguageController extends BaseLanguageController {

	@RequestMapping(value = "/operation/{id}/execute", method = RequestMethod.POST)
	@ResponseBody
	public AppResponse executeOperation(String id, String content,
			String fileUri) {

		AppResponse appResponse = new AppResponse();
		
		InputStream in = LanguageController.class
				.getResourceAsStream("/config.json");
		String config = Util.getStringFromInputStream(in);
		
		try {
			Config.loadConfig(config);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		if (id.equals("execute")) {						
			String url = Config.getProperty("CSPWebReasonerEndpoint");
	        url += "/solve";

	        OperationResponse op = new OperationResponse();
	        try {
	        	op = Util.sendPost(url, content);
	        	
	        	if((Boolean) op.get("consistent")){
					appResponse.setMessage("<pre>The document is consistent.\n"
							+ op.get("result") + "</pre>");
					appResponse.setStatus(Status.OK_PROBLEMS);
				} else {
					appResponse.setMessage("<pre>The document has conflicts.\n"
							+ op.get("conflicts") + "</pre>");
					appResponse.setStatus(Status.OK_PROBLEMS);
				}	
	        	
	        } catch (Exception e) {
	        	appResponse.setMessage("<pre>There was a problem processing your request</pre>");
				appResponse.setStatus(Status.OK_PROBLEMS);
				e.printStackTrace();
	        }					
		}

		appResponse.setFileUri(fileUri);
		
		return appResponse;
	}

	@RequestMapping(value = "/format/{format}/checkLanguage", method = RequestMethod.POST)
	@ResponseBody
	public AppResponse checkLanguage(String id, String content, String fileUri) {

		AppResponse appResponse = new AppResponse();
		
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
//			appResponse.setStatus(Status.OK_PROBLEMS);
//		}
//		
//		appResponse.setAnnotations(errorHandler.getAnnotations().toArray(new AppAnnotations[0]));
//		
//		if(errorHandler.getAnnotations().size() == 0)
//			appResponse.setStatus(Status.OK);
//		else
//			appResponse.setStatus(Status.OK_PROBLEMS);
		
		return appResponse;
	}

	@RequestMapping(value = "/convert", method = RequestMethod.POST)
	@ResponseBody
	public AppResponse convertFormat(String currentFormat,
			String desiredFormat, String fileUri, String content) {

		AppResponse appResponse = new AppResponse();
		return appResponse;
	}
}
