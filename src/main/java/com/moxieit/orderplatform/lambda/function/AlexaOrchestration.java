package com.moxieit.orderplatform.lambda.function;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.moxieit.orderplatform.DB.DBService;
import com.moxieit.orderplatform.function.service.api.AlexaDTO;
import com.moxieit.orderplatform.function.service.api.AlexaService;
import com.moxieit.orderplatform.lambda.function.service.impl.AlexaBiryaniServiceImpl;
import com.moxieit.orderplatform.lambda.function.service.impl.AlexaCancelServiceImpl;
import com.moxieit.orderplatform.lambda.function.service.impl.AlexaMenuItemServiceImpl;
import com.moxieit.orderplatform.lambda.function.service.impl.AlexaClosedOrderServiceImpl;
import com.moxieit.orderplatform.lambda.function.service.impl.AlexaConfirmOrderServiceImpl;
import com.moxieit.orderplatform.lambda.function.service.impl.AlexaDeliveryServiceImpl;
import com.moxieit.orderplatform.lambda.function.service.impl.AlexaDesertItemQuantityServiceimpl;
import com.moxieit.orderplatform.lambda.function.service.impl.AlexaRecentOrderServiceImpl;
import com.moxieit.orderplatform.lambda.function.service.impl.AlexaGetStartedServiceImpl;
import com.moxieit.orderplatform.lambda.function.service.impl.AlexaMenuCategoriesServiceImpl;
import com.moxieit.orderplatform.lambda.function.service.impl.AlexaNewOrderServiceImpl;
import com.moxieit.orderplatform.lambda.function.service.impl.AlexaQuantityServiceImpl;
import com.moxieit.orderplatform.lambda.function.service.impl.AlexaPickUpServiceImpl;
import com.moxieit.orderplatform.lambda.function.service.impl.AlexaSpicyServiceImpl;
import com.moxieit.orderplatform.lambda.response.BaseResponse;

public class AlexaOrchestration implements RequestHandler<Object, BaseResponse> {

	@Override
	public BaseResponse handleRequest(Object alexaRequest, Context context) {
		// TODO Auto-generated method stub

		// LambdaLogger logger = context.getLogger();
		System.out.println("Input :" + alexaRequest);
		// logger.log("Input : " +lexRequest);
		DynamoDB dynamoDB = DBService.getDBConnection();
		Table restaurantTable = dynamoDB.getTable("Restaurant");
		Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
		map = (Map<String, Map<String, String>>) alexaRequest;
		Map<String, String> map1 = new HashMap<String, String>();
		map1 = (Map<String, String>) alexaRequest;
		Map<String, Map> map2 = new HashMap<String, Map>();
		map2 = (Map<String, Map>) alexaRequest;
		Map<String, Map<String, Map<String, Map<String, String>>>> map3 = new HashMap<String, Map<String, Map<String, Map<String, String>>>>();
		map3 = (Map<String, Map<String, Map<String, Map<String, String>>>>) alexaRequest;
		Map<String, Map<String, Map<String, Map<String,Map<String,Map<String, String>>>>>> map4 = new HashMap<String, Map<String, Map<String, Map<String,Map<String,Map<String, String>>>>>>();
		map4 = (Map<String, Map<String, Map<String, Map<String,Map<String,Map<String, String>>>>>>) alexaRequest;
		Map<String, Map<String, Map<String, String>>> alexaMap = new HashMap<String, Map<String,Map<String, String>>>();
		alexaMap = (Map<String, Map<String, Map<String, String>>>) alexaRequest;
		Map<String, Map<String, Map<String,Map<String,Map<String, String>>>>> alexaMap1 = new HashMap<String, Map<String,Map<String,Map<String, Map<String, String>>>>>();
		alexaMap1 = (Map<String, Map<String, Map<String,Map<String,Map<String, String>>>>>) alexaRequest;
		String LaunchRequest = (String) map.get("request").get("type");
		String alexaUserId = (String) alexaMap.get("session").get("user").get("userId");
		String applicationId = (String) alexaMap.get("session").get("application").get("applicationId");
		//String request = (String) map3.get("intent").get("slots").get("numberValues").get("value");
		String[] alexaUserIdSplit = alexaUserId.split("\\.");
		System.out.println(alexaUserIdSplit[2]);
		System.out.println(alexaUserIdSplit[3]);
		alexaUserId = alexaUserIdSplit[3];
		boolean alexaNew = (boolean) map2.get("session").get("new");
		String intentName = "";
		String request = "";
		String request1 = "";
		String request2 = "";
		String consentToken = "";
		String deviceId = "";
		try {
			intentName = alexaMap.get("request").get("intent").get("name");
		} catch (NullPointerException e) {
			System.out.println("Alexa IntentName is null for Launch Request");
		}
		try {
			request = (String) alexaMap1.get("request").get("intent").get("slots").get("InputValues").get("value");
		} catch (NullPointerException e) {
			System.out.println("Alexa Input Request is null");
		}
		try {
			request1 = (String) alexaMap1.get("request").get("intent").get("slots").get("PhoneNumberslot").get("value");
		} catch (NullPointerException e) {
			System.out.println("PhonoeNumber Request is null");
		}	
		try {
			request2 = (String) alexaMap1.get("request").get("intent").get("slots").get("Quantityslot").get("value");
		} catch (NullPointerException e) {
			System.out.println("Quantityslot Request is null");
		}
		try {
			consentToken = (String) alexaMap1.get("context").get("System").get("user").get("permissions").get("consentToken");
		} catch (NullPointerException e) {
			System.out.println("consentToken is null");
		}
		try {
			deviceId = (String) map3.get("context").get("System").get("device").get("deviceId");
		} catch (NullPointerException e) {
			System.out.println("DeviceId is null");
		}
		 
		
		System.out.println(request);
		if (LaunchRequest.equalsIgnoreCase("LaunchRequest")) {
			AlexaService alexaService = new AlexaGetStartedServiceImpl();
			AlexaDTO alexaDTO = new AlexaDTO();
			alexaDTO.setApplicationId(applicationId);
			alexaDTO.setUserId(alexaUserId);
			alexaDTO.setRequest(request);
			alexaDTO.setLaunchRequest(LaunchRequest);
			alexaDTO.setDeviceId(deviceId);
			alexaDTO.setConsentToken(consentToken);
			return alexaService.serveLex(alexaDTO, context);
		} else if (intentName.contains("NewOrder")) {
			AlexaService alexaService = new AlexaNewOrderServiceImpl();
			AlexaDTO alexaDTO = new AlexaDTO();
			alexaDTO.setApplicationId(applicationId);
			alexaDTO.setUserId(alexaUserId);
			alexaDTO.setRequest(request);
			return alexaService.serveLex(alexaDTO, context);
		} else if (intentName.equalsIgnoreCase("RecentOrder")) {
			AlexaService alexaService = new AlexaRecentOrderServiceImpl();
			AlexaDTO alexaDTO = new AlexaDTO();
			alexaDTO.setApplicationId(applicationId);
			alexaDTO.setUserId(alexaUserId);
			alexaDTO.setRequest(request);
			alexaDTO.setDeviceId(deviceId);
			alexaDTO.setConsentToken(consentToken);
			return alexaService.serveLex(alexaDTO, context);
		} else if (intentName.equalsIgnoreCase("Delivery")) {
			AlexaService alexaService = new AlexaDeliveryServiceImpl();
			AlexaDTO alexaDTO = new AlexaDTO();
			alexaDTO.setApplicationId(applicationId);
			alexaDTO.setUserId(alexaUserId);
			alexaDTO.setRequest(request);
			alexaDTO.setDeviceId(deviceId);
			alexaDTO.setConsentToken(consentToken);
			return alexaService.serveLex(alexaDTO, context);
		} else if (intentName.equalsIgnoreCase("PickUp")) {
			AlexaService alexaService = new AlexaPickUpServiceImpl();
			AlexaDTO alexaDTO = new AlexaDTO();
			alexaDTO.setApplicationId(applicationId);
			alexaDTO.setUserId(alexaUserId);
			alexaDTO.setRequest(request);
			return alexaService.serveLex(alexaDTO, context);
		
		} else if (intentName.equalsIgnoreCase("Menu")) {
			AlexaService alexaService = new AlexaMenuCategoriesServiceImpl();
			AlexaDTO alexaDTO = new AlexaDTO();
			alexaDTO.setApplicationId(applicationId);
			alexaDTO.setUserId(alexaUserId);
			alexaDTO.setRequest(request);
			return alexaService.serveLex(alexaDTO, context);
		
		} else if (intentName.equalsIgnoreCase("Biryani") || intentName.equalsIgnoreCase("SideOrders")  
				|| intentName.equalsIgnoreCase("Desserts") || intentName.equalsIgnoreCase("MainCourse")) {
			AlexaService alexaService = new AlexaBiryaniServiceImpl();
			AlexaDTO alexaDTO = new AlexaDTO();
			alexaDTO.setApplicationId(applicationId);
			alexaDTO.setUserId(alexaUserId);
			alexaDTO.setRequest(request);
			alexaDTO.setIntentName(intentName);
			return alexaService.serveLex(alexaDTO, context);
		
		} else if (intentName.equalsIgnoreCase("MenuItem")) {
			AlexaService alexaService = new AlexaMenuItemServiceImpl();
			AlexaDTO alexaDTO = new AlexaDTO();
			alexaDTO.setApplicationId(applicationId);
			alexaDTO.setUserId(alexaUserId);
			alexaDTO.setRequest(request);
			return alexaService.serveLex(alexaDTO, context);
		
		} else if (request.equalsIgnoreCase("DessertsItems")) {
			AlexaService alexaService = new AlexaDesertItemQuantityServiceimpl();
			AlexaDTO alexaDTO = new AlexaDTO();
			alexaDTO.setApplicationId(applicationId);
			alexaDTO.setUserId(alexaUserId);
			alexaDTO.setRequest(request);
			return alexaService.serveLex(alexaDTO, context);
		
		} else if (intentName.equalsIgnoreCase("Quantity")) {
			AlexaService alexaService = new AlexaQuantityServiceImpl();
			AlexaDTO alexaDTO = new AlexaDTO();
			alexaDTO.setApplicationId(applicationId);
			alexaDTO.setUserId(alexaUserId);
			alexaDTO.setRequest(request2);
			alexaDTO.setDeviceId(deviceId);
			alexaDTO.setConsentToken(consentToken);
			return alexaService.serveLex(alexaDTO, context);
		
		} else if (intentName.equalsIgnoreCase("SpicyLevel")) {
			AlexaService alexaService = new AlexaSpicyServiceImpl();
			AlexaDTO alexaDTO = new AlexaDTO();
			alexaDTO.setApplicationId(applicationId);
			alexaDTO.setUserId(alexaUserId);
			alexaDTO.setRequest(request);
			return alexaService.serveLex(alexaDTO, context);
		/*} else if (request.equalsIgnoreCase("SpicyLevel") || request.equalsIgnoreCase("DessertsItemsQuantity")) {
			AlexaService alexaService = new AlexaClosedOrderServiceImpl();
			AlexaDTO alexaDTO = new AlexaDTO();
			alexaDTO.setApplicationId(applicationId);
			alexaDTO.setUserId(alexaUserId);
			alexaDTO.setRequest(request);
			return alexaService.serveLex(alexaDTO, context);*/
		}	else if (intentName.equalsIgnoreCase("AMAZON.StopIntent") || intentName.equalsIgnoreCase("AMAZON.CancelIntent") ||
				intentName.equalsIgnoreCase("AMAZON.HelpIntent")) {
			AlexaService alexaService = new AlexaCancelServiceImpl();
			AlexaDTO alexaDTO = new AlexaDTO();
			alexaDTO.setApplicationId(applicationId);
			alexaDTO.setUserId(alexaUserId);
			alexaDTO.setRequest(request);
			alexaDTO.setIntentName(intentName);
			return alexaService.serveLex(alexaDTO, context);
		}else if (intentName.equalsIgnoreCase("CheckOut")) {
			AlexaService alexaService = new AlexaClosedOrderServiceImpl();
			AlexaDTO alexaDTO = new AlexaDTO();
			alexaDTO.setApplicationId(applicationId);
			alexaDTO.setUserId(alexaUserId);
			alexaDTO.setRequest(request);
			return alexaService.serveLex(alexaDTO, context);
		} else if (intentName.equalsIgnoreCase("Confirm")) {
			AlexaService alexaService = new AlexaConfirmOrderServiceImpl();
			AlexaDTO alexaDTO = new AlexaDTO();
			alexaDTO.setApplicationId(applicationId);
			alexaDTO.setUserId(alexaUserId);
			alexaDTO.setRequest(request);
			return alexaService.serveLex(alexaDTO, context);
		}
		
	return null;
	}
}
