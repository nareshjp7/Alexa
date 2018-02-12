package com.moxieit.orderplatform.lambda.function.service.impl;

import com.amazonaws.services.lambda.runtime.Context;
import com.moxieit.orderplatform.function.service.api.AlexaDTO;
import com.moxieit.orderplatform.function.service.api.AlexaService;
import com.moxieit.orderplatform.lambda.response.AlexaResponse;
import com.moxieit.orderplatform.lambda.response.BaseResponse;
import com.moxieit.orderplatform.lambda.response.Card;
import com.moxieit.orderplatform.lambda.response.OutputSpeech;
import com.moxieit.orderplatform.lambda.response.Reprompt;
import com.moxieit.orderplatform.lambda.response.Response;

public class AlexaCancelServiceImpl implements AlexaService {

	public BaseResponse serveLex(AlexaDTO alexaDTO, Context context) {
		AlexaResponse alexaResponse = new AlexaResponse();
		Response response = new Response();
		OutputSpeech outputSpeech = new OutputSpeech();
		String intentName = alexaDTO.getIntentName();
		Card card = new Card();
		if (intentName.equalsIgnoreCase("AMAZON.StopIntent")){
		outputSpeech.setSsml(
				"<speak>Don't You Worry. I'll be back.</speak>");
		outputSpeech.setType("SSML");
	
		card.setTitle("Stop Order");
		card.setType("Simple");
		card.setContent("Don't You Worry. I'll be back.");
		response.setShouldEndSession(true);		
		response.setCard(card);
		response.setOutputSpeech(outputSpeech);
		alexaResponse.setVersion("1.0");
		alexaResponse.setResponse(response);
		return alexaResponse;
		} else if (intentName.equalsIgnoreCase("AMAZON.CancelIntent")){
			outputSpeech.setSsml(
					"<speak>No problem. Request cancelled.</speak>");
			outputSpeech.setType("SSML");
			
			card.setTitle("Cancel Order");
			card.setType("Simple");
			card.setContent("No problem. Request cancelled.");
			response.setShouldEndSession(true);		
			response.setCard(card);
			response.setOutputSpeech(outputSpeech);
			alexaResponse.setVersion("1.0");
			alexaResponse.setResponse(response);
			return alexaResponse;
		} else{
			outputSpeech.setSsml(
					"<speak>You can place an order by saying 'NewOrder' or 'RecentOrder' and you can get menu by saying 'Menu'. You can also say stop or exit to quit.</speak>");
			outputSpeech.setType("SSML");
			
			card.setTitle("How to Place Order?");
			card.setType("Simple");
			card.setContent("You can place an order by saying 'NewOrder' or 'RecentOrder' and you can get menu by saying 'Menu'. You can also say stop or exit to quit.");
			Reprompt reprompt = new Reprompt();
			OutputSpeech outputSpeech1 = new OutputSpeech();
			outputSpeech1.setText("What would you like to do?");
			outputSpeech1.setType("PlainText");
			reprompt.setOutputSpeech(outputSpeech1);
			response.setReprompt(reprompt);
			response.setShouldEndSession(false);		
			response.setCard(card);
			response.setOutputSpeech(outputSpeech);
			alexaResponse.setVersion("1.0");
			alexaResponse.setResponse(response);
			return alexaResponse;
		}
				
		
	}
}
