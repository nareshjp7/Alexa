package com.moxieit.orderplatform.lambda.function.service.impl;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.amazonaws.services.lambda.runtime.Context;
import com.moxieit.orderplatform.function.service.api.AlexaDTO;
import com.moxieit.orderplatform.function.service.api.AlexaService;
import com.moxieit.orderplatform.lambda.response.AlexaResponse;
import com.moxieit.orderplatform.lambda.response.BaseResponse;
import com.moxieit.orderplatform.lambda.response.Card;
import com.moxieit.orderplatform.lambda.response.OutputSpeech;
import com.moxieit.orderplatform.lambda.response.Reprompt;
import com.moxieit.orderplatform.lambda.response.Response;

public class AlexaGetStartedServiceImpl implements AlexaService {

	@Override
	public BaseResponse serveLex(AlexaDTO alexaDTO, Context context) {
		/*Calendar calendar = Calendar.getInstance();
		//DateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss E K a z ");
		DateFormat formatter1 = new SimpleDateFormat("k");
					
		
		int userTime = Integer.parseInt(formatter1.format(calendar.getTime()));
		System.out.println("userTime "+userTime);
			
		if(userTime>11 && userTime<17){*/
			   		
		
		AlexaResponse alexaResponse = new AlexaResponse();
		Response response = new Response();
		OutputSpeech outputSpeech = new OutputSpeech();	
		outputSpeech.setSsml("<speak>Hi, Would you like to place a Recent order or Start a new Order.</speak>");
		outputSpeech.setType("SSML");
		Card card = new Card();
		card.setTitle("Audio");
		card.setType("Simple");
		card.setContent("Hi, Would you like to place a Recent order or Start a new Order.");
		Reprompt reprompt = new Reprompt();
		OutputSpeech outputSpeech1 = new OutputSpeech();
		outputSpeech1.setText("Please provide your choice of Recent order or Start a new Order");
		outputSpeech1.setType("PlainText");
		reprompt.setOutputSpeech(outputSpeech1);
		response.setReprompt(reprompt);
		response.setShouldEndSession(false);
		
		response.setCard(card);
		response.setOutputSpeech(outputSpeech);
		alexaResponse.setVersion("1.0");

		alexaResponse.setResponse(response);

		return alexaResponse;
		/*} else {
			AlexaResponse alexaResponse = new AlexaResponse();
			Response response = new Response();
			OutputSpeech outputSpeech = new OutputSpeech();	
			outputSpeech.setSsml("<speak>Hi, Restaurant was closed now, you can order food between 11AM to 10PM.Thank you.</speak>");
			outputSpeech.setType("SSML");
			Card card = new Card();
			card.setTitle("Audio");
			card.setType("Simple");
			card.setContent("Hi, Restaurant was closed now, you can order food between 11AM to 10PM.Thank you.");
			Reprompt reprompt = new Reprompt();
			OutputSpeech outputSpeech1 = new OutputSpeech();
			outputSpeech1.setText("Thank you.");
			outputSpeech1.setType("PlainText");
			reprompt.setOutputSpeech(outputSpeech1);
			response.setReprompt(reprompt);
			response.setShouldEndSession(true);
			
			response.setCard(card);
			response.setOutputSpeech(outputSpeech);
			alexaResponse.setVersion("1.0");

			alexaResponse.setResponse(response);

			return alexaResponse;

		}*/
	}

	/*public static void main(String[] args) {
		AlexaGetStartedServiceImpl FbLoginServiveImpl = new AlexaGetStartedServiceImpl();
		Context context = null;
		AlexaDTO alexaDTO= new AlexaDTO();
		
		alexaDTO.setUserId("AFHFYXC47SSN4DSEXYTINPFRSEGCVEN3X5RX5BNA342NRCA73VZX4G43FCQF5TI7L5RFEJD6HHQ2VXPLUSQSSHRJSIBOBZIIFYUWUFOR7Z7PVLX27NJYJYURDFOMJHVWWYE2MPXASCBCNRQUNSJPOYQKAS7IBXAYDKIWIBUIKE3WISM6OD25DFXX344QDDPLOVIUGZCVU2Y5A3A");
		FbLoginServiveImpl.serveLex(alexaDTO, context);
		}*/
}
