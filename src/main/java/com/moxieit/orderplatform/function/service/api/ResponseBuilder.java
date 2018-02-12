package com.moxieit.orderplatform.function.service.api;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.*;

import org.json.simple.JSONObject;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import com.google.gson.Gson.*;
import com.google.gson.JsonObject;
import com.moxieit.orderplatform.lambda.response.GoogleResponse;



public class ResponseBuilder {
	/*
	
    public static RootResponse tellResponse(String message) {
        RootResponse rootResponse = new RootResponse();
        rootResponse.expectUserResponse = false;
        rootResponse.final_response = new FinalResponse();
        rootResponse.final_response.speech_response = new SpeechResponse(message, null);
        return rootResponse;
    }

    public static GoogleResponse askResponse(String message) {
        return askResponse(message, null, null);
    }

   
    public static GoogleResponse askResponse(String message, String[] noInputPrompts) {
        return askResponse(message, null, noInputPrompts);
    }


    public static GoogleResponse askResponse(String message, String conversationToken, String[] noInputPrompts) {
        GoogleResponse rootResponse = new GoogleResponse();
       
        rootResponse.setConversation_token(conversationToken);
        rootResponse.setExpected_inputs(new ArrayList<ExpectedInputs>());

        ExpectedInputs expectedInput = new ExpectedInputs();
        expectedInput.input_prompt = new InputPrompt();
        expectedInput.input_prompt.initial_prompts = Collections.singletonList(new SpeechResponse(message, null));

        if (noInputPrompts != null && noInputPrompts.length > 0) {
            expectedInput.input_prompt.no_input_prompts = new ArrayList<SpeechResponse>();
            for (String noInputPrompt : noInputPrompts) {
                expectedInput.input_prompt.no_input_prompts.add(new SpeechResponse(noInputPrompt, null));
            }

        }

        expectedInput.possible_intents = new ArrayList<ExpectedIntent>();
        expectedInput.possible_intents.add(new ExpectedIntent("orderfood"));

        rootResponse.getExpected_inputs().add(expectedInput);
        return rootResponse;
    }


    public static GoogleResponse askForPermissionResponse(String permissionContext, SupportedPermissions... permissions) {
        List<String> permissionsStr = new ArrayList<String>();
        for (SupportedPermissions permission : permissions) {
            permissionsStr.add(permission.name());
        }

        return askForPermissionResponse(permissionContext, permissionsStr);
    }

  


    public static GoogleResponse askForPermissionResponse(String permissionContext, Collection<String> permissions) throws IllegalArgumentException {
        if (permissionContext == null || permissionContext.length() == 0) {
            throw new IllegalArgumentException("permissionContext argument cannot be null");
        }

        if (permissions == null || permissions.isEmpty()) {
            throw new IllegalArgumentException("At least one permission needed.");
        }

        try {
            for (String permission : permissions) {
                SupportedPermissions.valueOf(permission);
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Assistant permission must be one of [NAME, DEVICE_PRECISE_LOCATION, DEVICE_COARSE_LOCATION]");
        }

        GoogleResponse rootResponse1 = new GoogleResponse();
        GoogleResponse rootResponse2 = new GoogleResponse();
        RootResponse rootResponse = new RootResponse();
        rootResponse.expectUserResponse = true;
        rootResponse.isSsml = false;
        rootResponse.noInputPrompts = new ArrayList<String>();
        rootResponse2.speech = "To locate you, I’ll just need to get your street address from Google. Is that ok?";
       
        
        PermissionValueSpec permissionValueSpec = new PermissionValueSpec();
        permissionValueSpec.optContext = permissionContext;
        Serializable type = "type.googleapis.com/google.actions.v2.PermissionValueSpec";
        System.out.println("serialazize type value" + type);
      
     
        permissionValueSpec.type = "type.googleapis.com/google.actions.v2.PermissionValueSpec";
     
       
        String x = permissionValueSpec.toString();
        Gson g = new Gson(); 
        String y = g.toJson(permissionValueSpec);
        
        String decodedString= null;
        InputValueSpec permissionValueSpec1 = g.fromJson(y , InputValueSpec.class);
        System.out.println("decodedString "+ permissionValueSpec1);
        		
        System.out.println("test "+ y);
     
        

        InputValueSpec inputValueSpec = new InputValueSpec();
        inputValueSpec.data = permissionValueSpec1;
        inputValueSpec.intent = "actions.intent.PERMISSION";
        ExpectedIntent expectedIntent = new ExpectedIntent(StandardIntents.PERMISSION);
        expectedIntent.input_value_spec = inputValueSpec;

        ExpectedInputs expectedInput = new ExpectedInputs();
      //  rootResponse.systemIntent = new ArrayList<ExpectedIntent>();
      
        rootResponse.systemIntent = inputValueSpec;

       // rootResponse.expected_inputs = new ArrayList<ExpectedInputs>();
       // rootResponse.expected_inputs.add(expectedInput);
        rootResponse1.setGoogle(rootResponse);
      
        rootResponse2.setData(rootResponse1);
        System.out.println("rootResponse2 "+ rootResponse2);
        return rootResponse2;
    }*/
}
