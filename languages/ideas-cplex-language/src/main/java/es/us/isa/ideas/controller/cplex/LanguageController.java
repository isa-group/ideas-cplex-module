package es.us.isa.ideas.controller.cplex;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import es.us.isa.aml.reasoners.CSPWebReasoner;
import es.us.isa.aml.reasoners.Reasoner;
import es.us.isa.aml.util.Config;
import es.us.isa.aml.util.OperationResponse;
import es.us.isa.aml.util.ReasonerFactory;
import es.us.isa.aml.util.Util;
import es.us.isa.ideas.common.AppAnnotations;
import es.us.isa.ideas.common.AppResponse;
import es.us.isa.ideas.common.AppResponse.Status;
import es.us.isa.ideas.module.controller.BaseLanguageController;

@Controller
@RequestMapping("/language")
public class LanguageController extends BaseLanguageController {

	protected final String CONFIG_PATH = "/config.json";
	public static int MAX_SIZE = 1950000;

	@PostConstruct
	public void init() {

		System.out.println("config anterior: "
				+ Config.getInstance().getPropertiesMap());

		InputStream in = LanguageController.class
				.getResourceAsStream(CONFIG_PATH);
		String config = Util.getStringFromInputStream(in);

		try {
			Config.loadConfig(config);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		System.out.println("config posterior: "
				+ Config.getInstance().getPropertiesMap());
	}

	@RequestMapping(value = "/operation/{id}/execute", method = RequestMethod.POST)
	@ResponseBody
	public AppResponse executeOperation(String id, String content,
			String fileUri) {

		AppResponse appResponse = new AppResponse();

		if (content.length() < MAX_SIZE) {
			if (id.equals("execute")) {

				Reasoner reasoner = ReasonerFactory.createCSPReasoner();

				if (reasoner instanceof CSPWebReasoner) {
					CSPWebReasoner webReasoner = (CSPWebReasoner) reasoner;
					Boolean solve = webReasoner.solve(content);
					OperationResponse op = webReasoner.explain(content);

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
				} else {

					String url = Config.getProperty("CSPWebReasonerEndpoint");
					url += "/solver/solve";

					try {
						String json = Util.sendPost(url, content);
						Boolean solve = new Gson().fromJson(json.toString(),
								Boolean.class);

						url = Config.getProperty("CSPWebReasonerEndpoint");
						url += "/solver/explain";

						json = Util.sendPost(url, content);
						OperationResponse op = new Gson().fromJson(
								json.toString(), OperationResponse.class);

						if (solve) {
							appResponse
									.setMessage("<pre><b>The document is consistent.</b>\n"
											+ op.get("result") + "</pre>");
							appResponse.setStatus(Status.OK);
						} else {
							if (op.getResult().get("conflicts") != null) {
								appResponse
										.setMessage("<pre><b>The document is not consistent.</b>\n"
												+ op.getResult().get(
														"conflicts") + "</pre>");
								appResponse.setStatus(Status.OK_PROBLEMS);
							} else {
								appResponse
										.setMessage("<pre><b>The document is not consistent.</b>\n"
												+ op.get("result") + "</pre>");
								appResponse.setStatus(Status.OK_PROBLEMS);
							}
						}
					} catch (Exception e) {
						appResponse
								.setMessage("<pre>There was a problem processing your request.</pre>");
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

		String url = Config.getProperty("CSPWebReasonerEndpoint");
		url += "/language/check";

		String json;
		try {
			json = Util.sendPost(url, content);

			Type listType = new TypeToken<ArrayList<AppAnnotations>>() {
				private static final long serialVersionUID = 1L;
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
