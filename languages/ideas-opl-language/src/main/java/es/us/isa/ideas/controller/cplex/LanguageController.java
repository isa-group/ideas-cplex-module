package es.us.isa.ideas.controller.cplex;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import es.us.isa.aml.reasoners.CSPWebReasoner;
import es.us.isa.aml.reasoners.CplexHandler;
import es.us.isa.aml.reasoners.CplexReasoner;
import es.us.isa.aml.reasoners.Reasoner;
import es.us.isa.aml.util.Config;
import es.us.isa.aml.util.OperationResponse;
import es.us.isa.aml.util.ReasonerFactory;
import es.us.isa.aml.util.Util;
import es.us.isa.ideas.module.common.AppAnnotations;
import es.us.isa.ideas.module.common.AppResponse;
import es.us.isa.ideas.module.common.AppResponse.Status;
import es.us.isa.ideas.module.controller.BaseLanguageController;

@Controller
@RequestMapping("/language")
public class LanguageController extends BaseLanguageController {

	protected final String CONFIG_PATH = "/config.json";
	public static int MAX_SIZE = 1950000;

	@PostConstruct
	public void init() {
		InputStream in = LanguageController.class
				.getResourceAsStream(CONFIG_PATH);
		String config = Util.getStringFromInputStream(in);
		Config.load(config);
	}

	@RequestMapping(value = "/operation/{id}/execute", method = RequestMethod.POST)
	@ResponseBody
	public AppResponse executeOperation(String id, String content,
			String fileUri, String data) {

		AppResponse appResponse = new AppResponse();

		if (content.length() < MAX_SIZE) {
			if (id.equals("execute")) {

				Reasoner reasoner = ReasonerFactory.createCSPReasoner();
				Boolean solve = null;
				OperationResponse op = null;

				if (reasoner instanceof CSPWebReasoner) {
					CSPWebReasoner webReasoner = (CSPWebReasoner) reasoner;
					solve = webReasoner.solve(content);
					op = webReasoner.explain(content);
				} else if (reasoner instanceof CplexReasoner) {
					CplexHandler handler = new CplexHandler();
					handler.init();
					try {
						String json = handler.solve(content);
						solve = new Gson().fromJson(json.toString(),
								Boolean.class);
						json = handler.explain(content);
						op = new Gson().fromJson(json.toString(),
								OperationResponse.class);
					} catch (Exception e) {
						appResponse
								.setMessage("<pre>There was a problem processing your request.</pre>");
						appResponse.setStatus(Status.OK_PROBLEMS);
					}

				}

				if (solve) {
					appResponse
							.setMessage("<pre><b>The document is consistent.</b>\n"
									+ op.get("result") + "</pre>");
					appResponse.setStatus(Status.OK);
				} else {
					if (op.getResult().get("conflicts") != null) {
						appResponse
								.setMessage("<pre><b>The document is not consistent.</b>\n"
										+ op.getResult().get("conflicts")
										+ "</pre>");
						appResponse.setStatus(Status.OK_PROBLEMS);
					} else {
						appResponse
								.setMessage("<pre><b>The document is not consistent.</b>\n"
										+ op.get("result") + "</pre>");
						appResponse.setStatus(Status.OK_PROBLEMS);
					}
				}
			}
		} else {
			appResponse
					.setMessage("<pre>File exceeds maximum allowed size.</pre>");
			appResponse.setStatus(Status.OK_PROBLEMS);
		}

		appResponse.setFileUri(fileUri);

		return appResponse;
	}

	@RequestMapping(value = "/format/{format}/checkLanguage", method = RequestMethod.POST)
	@ResponseBody
	public AppResponse checkLanguage(String id, String content, String fileUri) {

		AppResponse appResponse = new AppResponse();
		List<AppAnnotations> annotations = new ArrayList<AppAnnotations>();

		String url = Config.getInstance().getCSPWebReasonerEndpoint();
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
