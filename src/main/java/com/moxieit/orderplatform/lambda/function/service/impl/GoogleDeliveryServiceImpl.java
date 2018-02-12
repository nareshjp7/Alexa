package com.moxieit.orderplatform.lambda.function.service.impl;


import java.util.ArrayList;
import org.json.simple.JSONObject;
import com.amazonaws.services.lambda.runtime.Context;
import com.moxieit.orderplatform.function.service.api.GoogleDTO;
import com.moxieit.orderplatform.lambda.response.GoogleResponse;


public class GoogleDeliveryServiceImpl{
	
	@SuppressWarnings("unchecked")
	public String serveLex(GoogleDTO googleDTO, Context context) {
		
        JSONObject responseJson = new JSONObject();
        JSONObject responseJson1 = new JSONObject();
        JSONObject google = new JSONObject();
        JSONObject systemIntent = new JSONObject();
        JSONObject data = new JSONObject();
        
        Object noInputPrompts = new ArrayList<String>();
        Object permissions = new ArrayList<String>();
        ArrayList<String> obj = new ArrayList<String>();
        obj.add("DEVICE_PRECISE_LOCATION");
        obj.add("NAME");
        Object data1 = "data";

        try {

        	responseJson.put("speech", "To locate you, I will just need to get your street address from Google. Is that ok?");   
            data.put("@type", "type.googleapis.com/google.actions.v2.PermissionValueSpec");
            data.put("optContext", "For delivery");            
            data.put("permissions", obj);
            systemIntent.put("intent", "actions.intent.PERMISSION");
            systemIntent.put("data", data);
            responseJson1.put("expectUserResponse", true);
            responseJson1.put("isSsml", false);
            responseJson1.put("noInputPrompts", noInputPrompts);
            responseJson1.put("systemIntent", systemIntent);
            
            google.put("google", responseJson1);
            responseJson.put(data1, google);  
            System.out.println("myString "+ responseJson);

            
            //responseJson.put("body", responseBody.toString());  

        } catch(Exception pex) {
            responseJson.put("statusCode", "400");
            responseJson.put("exception", pex);
        }
		GoogleResponse googleResponse = new GoogleResponse();		  
		googleResponse.setSpeech(responseJson.toString());
		return responseJson.toString();
	        
	    }

	public static void main(String[] args) {
		GoogleDeliveryServiceImpl googleService = new GoogleDeliveryServiceImpl();
		GoogleDTO googleDTO = new GoogleDTO();
	
		Context context = null ;
		googleService.serveLex(googleDTO, context);
		}

}
