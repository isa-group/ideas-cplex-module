package es.us.isa.ideas.controller.cplex;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import es.us.isa.ideas.common.AppAnnotations;
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
			url += "/solver/solve";

			try {
				String json = Util.sendPost(url, content);
				Boolean solve = new Gson().fromJson(json.toString(), Boolean.class);

				url = Config.getProperty("CSPWebReasonerEndpoint");
				url += "/solver/explain";
				
				json = Util.sendPost(url, content);
				OperationResponse op = new Gson().fromJson(json.toString(), OperationResponse.class);
				
				if (solve) {
					appResponse.setMessage("<pre>The document is consistent.\n"
							+ op.get("result") + "</pre>");
					appResponse.setStatus(Status.OK_PROBLEMS);
				} else {
					if((Boolean) op.getResult().get("existInconsistencies")){
						appResponse.setMessage("<pre>The document is not consistent.\n"
								+ op.getResult().get("conflicts") + "</pre>");
						appResponse.setStatus(Status.OK_PROBLEMS);
					} else if((Boolean) op.getResult().get("existDeadTerms")){
						appResponse.setMessage("<pre>The document has dead terms.\n"
								+ op.getResult().get("conflicts_deadterms") + "</pre>");
						appResponse.setStatus(Status.OK_PROBLEMS);
					} else if((Boolean) op.getResult().get("existCondInconsTerms")){
						appResponse.setMessage("<pre>The document has conditionally inconsistent terms.\n"
								+ op.getResult().get("conflicts_condIncons") + "</pre>");
						appResponse.setStatus(Status.OK_PROBLEMS);
					}
				}

			} catch (Exception e) {
				appResponse
						.setMessage("<pre>There was a problem processing your request</pre>");
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

		InputStream in = LanguageController.class
				.getResourceAsStream("/config.json");
		String config = Util.getStringFromInputStream(in);

		try {
			Config.loadConfig(config);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		AppResponse appResponse = new AppResponse();
		List<AppAnnotations> annotations = new ArrayList<AppAnnotations>();

		String url = Config.getProperty("CSPWebReasonerEndpoint");
		url += "/language/check";

		String json;
		try {
			json = Util.sendPost(url, content);

			Type listType = new TypeToken<ArrayList<AppAnnotations>>() {
			}.getType();
			annotations = new Gson().fromJson(json.toString(), listType);

			appResponse.setAnnotations(annotations
					.toArray(new AppAnnotations[0]));
		} catch (Exception e) {
			e.printStackTrace();
			appResponse.setStatus(Status.OK_PROBLEMS);
		}

		if (annotations.size() == 0)
			appResponse.setStatus(Status.OK);
		else
			appResponse.setStatus(Status.OK_PROBLEMS);

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
