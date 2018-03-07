package com.moxieit.orderplatform.lambda.function;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moxieit.orderplatform.function.service.api.GoogleDTO;
import com.moxieit.orderplatform.function.service.api.GoogleService;
import com.moxieit.orderplatform.lambda.function.service.impl.GoogleCashServiceImpl;
import com.moxieit.orderplatform.lambda.function.service.impl.GoogleCategoriesServiceImpl;
import com.moxieit.orderplatform.lambda.function.service.impl.GoogleConfirmOrderServiceImpl;
import com.moxieit.orderplatform.lambda.function.service.impl.GoogleDeliveryServiceImpl;
import com.moxieit.orderplatform.lambda.function.service.impl.GoogleMenuCategoriesServiceImpl;
import com.moxieit.orderplatform.lambda.function.service.impl.GoogleMenuItemServiceImpl;
import com.moxieit.orderplatform.lambda.function.service.impl.GooglePhoneNumberServiceImpl;
import com.moxieit.orderplatform.lambda.function.service.impl.GooglePickUpServiceImpl;
import com.moxieit.orderplatform.lambda.function.service.impl.GoogleQuantityServiceImpl;
import com.moxieit.orderplatform.lambda.function.service.impl.GoogleSpicyLevelServiceImpl;
import com.moxieit.orderplatform.lambda.function.service.impl.GoogleTransactionDecision;
import com.moxieit.orderplatform.lambda.function.service.impl.GoogleTransactionRequirementsCheckServiceImpl;
import com.moxieit.orderplatform.lambda.function.service.impl.GoogleTransactionsConfirmationDecision;
import com.moxieit.orderplatform.lambda.function.service.impl.GooglepermissionsServiceImpl;
import com.moxieit.orderplatform.lambda.function.service.impl.GoogleRecentOrderServiceImpl;
import com.moxieit.orderplatform.lambda.response.BaseResponse;
import com.moxieit.orderplatform.lambda.response.GoogleResponse;

public class GoogleHomeOrchestration implements RequestStreamHandler {

	@SuppressWarnings("unchecked")
	 public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
	//public BaseResponse handleRequest(Object input, Context context) {
		String botName = "SITARA";
		String restaurantId = "23";
	    JSONParser parser = new JSONParser();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		JSONObject event = new JSONObject();
		try {
			event = (JSONObject)parser.parse(reader);
			
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		System.out.println("my input:" + event);
		 Map<String, String > maptime = new HashMap<String,  String>();
		Map<String, Map<String, Map<String, String>>> map = new HashMap<String, Map<String, Map<String, String>>>();
		Map<String, Map<String, Map<String,Map<String,Map<String, String>>>>> device = new HashMap<String, Map<String,Map<String, Map<String,Map<String, String>>>>>();
		Map<String, Map<String, Map<String, Map<String, String>>>> useridmap = new HashMap<String, Map<String, Map<String, Map<String, String>>>>();
		Map<String, Map<String, Map<String, List<Map<String, List<Map<String, String>>>>>>> requestmap = new HashMap<String, Map<String, Map<String, List<Map<String, List<Map<String, String>>>>>>>();
		Map<String, Map<String, Map<String, List<Map<String, List<Map<String,  Map<String, String>>>>>>>> resultcheck = new HashMap<String, Map<String, Map<String, List<Map<String, List<Map<String, Map<String, String>>>>>>>>();
		Map<String,  Map<String,List<Map<String, Map<String, String>>>>> integerValue = new HashMap<String, Map<String,List<Map<String, Map<String, String>>>>>();
		maptime = (Map<String, String>) event;
		map = (Map<String, Map<String, Map<String, String>>>) event;
		device = (Map<String, Map<String, Map<String,Map<String,Map<String, String>>>>>) event;
		useridmap = (Map<String, Map<String, Map<String, Map<String, String>>>>) event;
		requestmap = (Map<String, Map<String, Map<String, List<Map<String, List<Map<String, String>>>>>>>) event;
		integerValue = (Map<String,  Map<String,List<Map<String, Map<String, String>>>>>) event;
		resultcheck = (Map<String, Map<String, Map<String, List<Map<String, List<Map<String,Map<String, String>>>>>>>>) event;
		
		String request = (String) requestmap.get("originalRequest").get("data").get("inputs").get(0).get("rawInputs").get(0).get("query");
		 String timestamp = (String) maptime.get("timestamp");
		System.out.println("request value" + request);

		String intentName = (String) map.get("result").get("metadata").get("intentName");
		String userId = (String) useridmap.get("originalRequest").get("data").get("user").get("userId");
		
		String formattedAddress = "";
		String userName = "";
		String permissionsGranted = "";
		try {
			permissionsGranted = (String)requestmap.get("originalRequest").get("data").get("inputs").get(0).get("arguments").get(0).get("textValue");
			System.out.println("permissionsGranted value" + permissionsGranted);
		} catch (NullPointerException e) {
			System.out.println("Google permissions Granted is null");
		}
		
		try {
			formattedAddress = (String) device.get("originalRequest").get("data").get("device").get("location").get("formattedAddress");
		} catch (NullPointerException e) {
			System.out.println("Google formattedAddress is null");
		}
		try {
			userName = (String) device.get("originalRequest").get("data").get("user").get("profile").get("displayName");
		} catch (NullPointerException e) {
			System.out.println("Google userName is null");
		}
		System.out.println("my intentName:" + intentName);
		OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8"); 
		String json=null;
		ObjectMapper mapper = new ObjectMapper();

		if (intentName.equalsIgnoreCase("RecentOrder")) {
			GoogleRecentOrderServiceImpl googleService = new GoogleRecentOrderServiceImpl();
			GoogleDTO googleDTO = new GoogleDTO();
			googleDTO.setUserId(userId);
			googleDTO.setRequest(request);
			googleDTO.setIntentName(intentName);
			googleDTO.setBotName(botName);
			googleDTO.setRestaurantId(restaurantId);
			try {
				 json = mapper.writeValueAsString(googleService.serveLex(googleDTO, context));			 
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				writer.write(json);  
		        writer.close();
			return;
		} else if (intentName.equalsIgnoreCase("Menu")) {
			//GoogleService googleService = new GoogleMenuCategoriesServiceImpl();
			GoogleMenuCategoriesServiceImpl googleService = new GoogleMenuCategoriesServiceImpl();	
			GoogleDTO googleDTO = new GoogleDTO();			
			googleDTO.setUserId(userId);
			googleDTO.setRequest(request);
			googleDTO.setIntentName(intentName);
			googleDTO.setBotName(botName);
			googleDTO.setRestaurantId(restaurantId);
			try {
			 json = mapper.writeValueAsString(googleService.serveLex(googleDTO, context));			 
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			writer.write(json);  
	        writer.close();
			return;
		} else if (intentName.equalsIgnoreCase("QuickBites") || intentName.equalsIgnoreCase("SideOrders")
				|| intentName.equalsIgnoreCase("Desserts") || intentName.equalsIgnoreCase("MainCourse")
				|| intentName.equalsIgnoreCase("ComboCorner") || intentName.equalsIgnoreCase("DosaCorner")
				|| intentName.equalsIgnoreCase("RiceCorner") || intentName.equalsIgnoreCase("KothuParatha")
				|| intentName.equalsIgnoreCase("Drinks") || intentName.equalsIgnoreCase("Pastries")
				|| intentName.equalsIgnoreCase("Kothu Paratha")) {
			GoogleCategoriesServiceImpl googleService = new GoogleCategoriesServiceImpl();
			GoogleDTO googleDTO = new GoogleDTO();
			googleDTO.setUserId(userId);
			googleDTO.setRequest(request);
			googleDTO.setIntentName(intentName);
			googleDTO.setBotName(botName);
			googleDTO.setRestaurantId(restaurantId);
			try {
				 json = mapper.writeValueAsString(googleService.serveLex(googleDTO, context));			 
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				writer.write(json);  
		        writer.close();
				return;
		} else if (intentName.equalsIgnoreCase("MenuItem")) {
			GoogleMenuItemServiceImpl googleService = new GoogleMenuItemServiceImpl();
			GoogleDTO googleDTO = new GoogleDTO();
			googleDTO.setUserId(userId);
			googleDTO.setRequest(request);
			googleDTO.setIntentName(intentName);
			googleDTO.setBotName(botName);
			googleDTO.setRestaurantId(restaurantId);
			try {
				 json = mapper.writeValueAsString(googleService.serveLex(googleDTO, context));			 
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				writer.write(json);  
		        writer.close();
				return;
		} else if (intentName.equalsIgnoreCase("Quantity")) {
			String intvalue = null;
			String quantity = null;
			try{
			intvalue = (String)integerValue.get("result").get("contexts").get(0).get("parameters").get("number");
			quantity = (String) map.get("result").get("parameters").get("number");
			System.out.println("Google intvalue is"+ intvalue);
			System.out.println("Google intvalue is "+quantity);
			}catch(Exception e){
				System.out.println("Google intvalue is null");
			}
			GoogleQuantityServiceImpl googleService = new GoogleQuantityServiceImpl();
			GoogleDTO googleDTO = new GoogleDTO();
			googleDTO.setUserId(userId);
			googleDTO.setRequest(request);
			googleDTO.setIntentName(intentName);
			googleDTO.setBotName(botName);
			googleDTO.setRestaurantId(restaurantId);			
			googleDTO.setQuantityValue(quantity);
			try {
				 json = mapper.writeValueAsString(googleService.serveLex(googleDTO, context));			 
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				writer.write(json);  
		        writer.close();
				return;
		} else if (intentName.equalsIgnoreCase("SpicyLevel")) {
			GoogleSpicyLevelServiceImpl googleService = new GoogleSpicyLevelServiceImpl();
			GoogleDTO googleDTO = new GoogleDTO();
			googleDTO.setUserId(userId);
			googleDTO.setRequest(request);
			googleDTO.setIntentName(intentName);
			googleDTO.setBotName(botName);
			googleDTO.setRestaurantId(restaurantId);
			try {
				 json = mapper.writeValueAsString(googleService.serveLex(googleDTO, context));			 
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				writer.write(json);  
		        writer.close();
				return;
		} else if (intentName.equalsIgnoreCase("Delivery")) {
			GoogleDeliveryServiceImpl googleService = new GoogleDeliveryServiceImpl();
			GoogleDTO googleDTO = new GoogleDTO();
			googleDTO.setUserId(userId);
			googleDTO.setRequest(request);
			googleDTO.setIntentName(intentName);
			googleDTO.setBotName(botName);
			googleDTO.setRestaurantId(restaurantId);
			try {
				 json = mapper.writeValueAsString(googleService.serveLex(googleDTO, context));			 
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			System.out.println("json value"+ json);
				writer.write(googleService.serveLex(googleDTO, context));  
		        writer.close();
				return;
		} else if (intentName.equalsIgnoreCase("PickUp")) {
			GooglePickUpServiceImpl googleService = new GooglePickUpServiceImpl();
			GoogleDTO googleDTO = new GoogleDTO();
			googleDTO.setUserId(userId);
			googleDTO.setRequest(request);
			googleDTO.setIntentName(intentName);
			googleDTO.setBotName(botName);
			googleDTO.setRestaurantId(restaurantId);
			try {
				 json = mapper.writeValueAsString(googleService.serveLex(googleDTO, context));			 
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				writer.write(json);  
		        writer.close();
				return;
		} else if (intentName.equalsIgnoreCase("Confirm")) {
			GoogleConfirmOrderServiceImpl googleService = new GoogleConfirmOrderServiceImpl();
			GoogleDTO googleDTO = new GoogleDTO();
			googleDTO.setUserId(userId);
			googleDTO.setRequest(request);
			googleDTO.setIntentName(intentName);
			googleDTO.setBotName(botName);
			googleDTO.setRestaurantId(restaurantId);
			try {
				 json = mapper.writeValueAsString(googleService.serveLex(googleDTO, context));			 
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				writer.write(json);  
		        writer.close();
				return;
		} else if (intentName.equalsIgnoreCase("PhoneNumber")) {
			GooglePhoneNumberServiceImpl googleService = new GooglePhoneNumberServiceImpl();
			GoogleDTO googleDTO = new GoogleDTO();
			googleDTO.setUserId(userId);
			googleDTO.setRequest(request);
			googleDTO.setIntentName(intentName);
			googleDTO.setBotName(botName);
			googleDTO.setRestaurantId(restaurantId);
			try {
				 json = mapper.writeValueAsString(googleService.serveLex(googleDTO, context));			 
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				writer.write(json);  
		        writer.close();
				return;
		} else if (intentName.equalsIgnoreCase("user_info")) {
			GooglepermissionsServiceImpl googleService = new GooglepermissionsServiceImpl();
			GoogleDTO googleDTO = new GoogleDTO();
			googleDTO.setUserId(userId);
			googleDTO.setRequest(request);
			googleDTO.setIntentName(intentName);
			googleDTO.setFormattedAddress(formattedAddress);
			googleDTO.setPermissionsGranted(permissionsGranted);
			googleDTO.setBotName(botName);
			googleDTO.setRestaurantId(restaurantId);
			try {
				 json = mapper.writeValueAsString(googleService.serveLex(googleDTO, context));			 
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				writer.write(json);  
		        writer.close();
				return;
		} else if (intentName.equalsIgnoreCase("Cash")) {
			GoogleCashServiceImpl googleService = new GoogleCashServiceImpl();
			GoogleDTO googleDTO = new GoogleDTO();
			googleDTO.setUserId(userId);
			googleDTO.setRequest(request);
			googleDTO.setIntentName(intentName);
			googleDTO.setFormattedAddress(formattedAddress);
			googleDTO.setPermissionsGranted(permissionsGranted);
			googleDTO.setBotName(botName);
			googleDTO.setRestaurantId(restaurantId);
			try {
				 json = mapper.writeValueAsString(googleService.serveLex(googleDTO, context));			 
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				writer.write(googleService.serveLex(googleDTO, context));
			
		        writer.close();
				return;
		} else if (intentName.equalsIgnoreCase("TransactionRequirementsCheck")) {
			GoogleTransactionRequirementsCheckServiceImpl googleService = new GoogleTransactionRequirementsCheckServiceImpl();
			GoogleDTO googleDTO = new GoogleDTO();
			googleDTO.setUserId(userId);
			googleDTO.setRequest(request);
			googleDTO.setIntentName(intentName);
			googleDTO.setFormattedAddress(formattedAddress);
			googleDTO.setPermissionsGranted(permissionsGranted);
			googleDTO.setBotName(botName);
			googleDTO.setRestaurantId(restaurantId);
			try {
				 json = mapper.writeValueAsString(googleService.serveLex(googleDTO, context));			 
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				writer.write(googleService.serveLex(googleDTO, context));
			
		        writer.close();
				return;
		} else if (intentName.equalsIgnoreCase("TransactionDecision")) {
			String transactionCheckResult = (String)resultcheck.get("originalRequest").get("data").get("inputs").get(0).get("arguments").get(0).get("extension").get("resultType");
			GoogleTransactionDecision googleService = new GoogleTransactionDecision();
			GoogleDTO googleDTO = new GoogleDTO();
			googleDTO.setUserId(userId);
			googleDTO.setRequest(request);
			googleDTO.setIntentName(intentName);
			googleDTO.setFormattedAddress(formattedAddress);
			googleDTO.setPermissionsGranted(permissionsGranted);
			googleDTO.setTransactionCheckResult(transactionCheckResult);
			googleDTO.setBotName(botName);
			googleDTO.setRestaurantId(restaurantId);
			try {
				 json = mapper.writeValueAsString(googleService.serveLex(googleDTO, context));			 
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				writer.write(googleService.serveLex(googleDTO, context));  
		        writer.close();
				return;
		} else if (intentName.equalsIgnoreCase("TransactionsConfirmationDecision")) {
			String transactionCheckResult = (String)resultcheck.get("originalRequest").get("data").get("inputs").get(0).get("arguments").get(0).get("extension").get("resultType");
			GoogleTransactionsConfirmationDecision googleService = new GoogleTransactionsConfirmationDecision();
			GoogleDTO googleDTO = new GoogleDTO();
			googleDTO.setUserId(userId);
			googleDTO.setRequest(request);
			googleDTO.setIntentName(intentName);
			googleDTO.setFormattedAddress(formattedAddress);
			googleDTO.setPermissionsGranted(permissionsGranted);
			googleDTO.setTransactionCheckResult(transactionCheckResult);
			googleDTO.setBotName(botName);
			googleDTO.setRestaurantId(restaurantId);
			try {
				 json = mapper.writeValueAsString(googleService.serveLex(googleDTO, context));			 
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				writer.write(googleService.serveLex(googleDTO, context));  
		        writer.close();
				return;
		}
		

	}

}
