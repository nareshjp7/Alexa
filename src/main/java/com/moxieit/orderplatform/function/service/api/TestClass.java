package com.moxieit.orderplatform.function.service.api;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.moxieit.orderplatform.lambda.response.BaseResponse;
import com.amazonaws.services.lambda.runtime.Context; 
import com.amazonaws.services.lambda.runtime.LambdaLogger;


import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;



public class TestClass implements RequestStreamHandler {
    JSONParser parser = new JSONParser();


    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {

        LambdaLogger logger = context.getLogger();
        logger.log("Loading Java Lambda handler of ProxyWithStream");

       
        //BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream1));
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

        	
        
            responseJson.put("speech", "PLACEHOLDER_FOR_PERMISSION");   
            data.put("@type", "type.googleapis.com/google.actions.v2.PermissionValueSpec");
            data.put("optContext", "To pick you up");            
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

        logger.log(responseJson.toJSONString());
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");      
        writer.write(responseJson.toString());  
        writer.close();
        
    }
 
}
