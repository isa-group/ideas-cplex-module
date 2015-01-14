package es.us.isa.ideas.controller.cplex;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.cp.IloCP;

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
				File temp = File.createTempFile("cplex-cp-temp", ".cpo");
				
				//write it
	    	    BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
	    	    bw.write(content);
	    	    bw.close();

				IloCP cp = new IloCP();
				cp.importModel(temp.getAbsolutePath());
				
				StringBuilder sb = new StringBuilder();
				sb.append("Has solution: " + cp.solve());
				if (cp.solve()) {    
	                 sb.append("\nSolution:");
	                 IloIntVar[] vars = cp.getAllIloIntVars();
	                 for (int i = 0; i < vars.length; i++)
	                     sb.append("\n\t" + vars[i].getName() + ": " + (int)cp.getValue(vars[i]));
	            }
				
				appResponse.setMessage("<pre>" + sb.toString() + "</pre>");
				appResponse.setStatus(Status.OK);
				
			} catch (IloException e) {
				appResponse.setMessage("<pre>" + e.getMessage() + "</pre>");
				appResponse.setStatus(Status.OK_PROBLEMS);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
