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

public class AlexaNewOrderServiceImpl implements AlexaService {

	@Override
	public BaseResponse serveLex(AlexaDTO alexaDTO, Context context) {
		AlexaResponse alexaResponse = new AlexaResponse();
		Response response = new Response();
		OutputSpeech outputSpeech = new OutputSpeech();
		/*
		 * outputSpeech.setText(
		 * "Today will provide you a new learning opportunity.  Stick with it and the possibilities will be endless. Can I help you with anything else?"
		 * );
		 */
		outputSpeech.setSsml("<speak>Great Let's GetStarted You can tell what would you like to order or I can tell you about my MENU What would you like.</speak>");
		outputSpeech.setType("SSML");
		Card card = new Card();
		card.setTitle("Audio");
		card.setType("Simple");
		card.setContent(
				"Great Let's GetStarted You can tell what would you like to order or I can tell you about my MENU What would you like.");
		Reprompt reprompt = new Reprompt();
		OutputSpeech outputSpeech1 = new OutputSpeech();
		outputSpeech1.setText("Please provide your choice or I can tell you about my MENU What would you like.");
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
