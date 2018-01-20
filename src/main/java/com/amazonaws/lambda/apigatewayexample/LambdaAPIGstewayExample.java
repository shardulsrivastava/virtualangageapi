package com.amazonaws.lambda.apigatewayexample;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;

import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

public class LambdaAPIGstewayExample implements RequestStreamHandler {
	JSONParser parser = new JSONParser();

	public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {

		LambdaLogger logger = context.getLogger();
		logger.log("Loading Java Lambda handler of LambdaAPIGstewayExample");

		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		JSONObject responseJson = new JSONObject();
		String name = "you";
		String city = "World";
		String time = "day";
		String day = null;
		String responseCode = "200";
		String greeting = "";
		long id = 0;
		List<String> error = new ArrayList<String>();
		String errorMessage = "";

		try {
			JSONObject event = (JSONObject) parser.parse(reader);
			if (event.get("queryStringParameters") != null) {
				JSONObject qps = (JSONObject) event.get("queryStringParameters");
				if (qps.get("time") != null) {
					time = (String) qps.get("time");
				}
			}

			if (event.get("pathParameters") != null) {
				JSONObject pps = (JSONObject) event.get("pathParameters");
				if (pps.get("proxy") != null) {
					city = (String) pps.get("proxy");
				}
			}

			if (event.get("headers") != null) {
				JSONObject hps = (JSONObject) event.get("headers");
				if (hps.get("day") != null) {
					day = (String) hps.get("header");
				}
			}

			if (event.get("body") != null) {
				String courseName = "";
				String firstName = "";
				String lastName = "";
				String email = "";
				String phoneNumber = "";
				String message = "";
				JSONObject body = (JSONObject) parser.parse((String) event.get("body"));
				if (isNotEmpty(body.get("courseName"))) {
					courseName = (String) body.get("courseName");
				} else {
					error.add("Course Name");
				}
				if (isNotEmpty(body.get("firstName"))) {
					firstName = (String) body.get("firstName");
				} else {
					error.add("First Name");
				}
				if (isNotEmpty(body.get("lastName"))) {
					lastName = (String) body.get("lastName");
				} else {
					error.add("Last Name");
				}
				if (isNotEmpty(body.get("email"))) {
					email = (String) body.get("email");
				} else {
					error.add("Email");
				}
				if (isNotEmpty(body.get("phoneNumber"))) {
					phoneNumber = (String) body.get("phoneNumber");
				} else {
					error.add("Phone No");
				}
				if (body.get("message") != null) {
					message = (String) body.get("message");
				}
				if (error.isEmpty()) {
					greeting = "Registration Details are Course : " + courseName + ", Name :" + firstName + " "
							+ lastName + ", Email : " + email + ", Phone No : " + phoneNumber + ",message : " + message;
					RegistrationDetails registrationDetails = new RegistrationDetails();
					registrationDetails.setCourseName(courseName);
					registrationDetails.setEmail(email);
					registrationDetails.setFirstName(firstName);
					registrationDetails.setLastName(lastName);
					registrationDetails.setMessage(message);
					registrationDetails.setPhoneNumber(phoneNumber);
					errorMessage = new RegistrationService().saveRegistration(registrationDetails);
				} else {
					errorMessage = String.format("%s can't be null", getErrorFields(error));
				}
			}

			JSONObject responseBody = new JSONObject();
			if (isNotEmpty(greeting)) {
				responseBody.put("message", greeting);
			}
			responseBody.put("status", isEmpty(errorMessage) ? "Success" : "Failed");
			responseBody.put("errorMessage", errorMessage);

			JSONObject headerJson = new JSONObject();
			headerJson.put("Access-Control-Allow-Origin", "*");
			headerJson.put("Access-Control-Allow-Credentials", "true");
			
			responseJson.put("statusCode", responseCode);
			responseJson.put("headers", headerJson);
			responseJson.put("body", responseBody.toString());

		} catch (ParseException pex) {
			responseJson.put("statusCode", "400");
			responseJson.put("exception", pex);
		}

		logger.log(responseJson.toJSONString());
		OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
		writer.write(responseJson.toJSONString());
		writer.close();
	}

	public boolean isEmpty(Object val) {

		return val == null || val.toString().equals("");

	}

	public boolean isNotEmpty(Object val) {

		return !isEmpty(val);

	}

	public String getErrorFields(List<String> error) {
		String errorString = error.toString();
		errorString = errorString.substring(1, errorString.length() - 1);
		return errorString;
	}

}