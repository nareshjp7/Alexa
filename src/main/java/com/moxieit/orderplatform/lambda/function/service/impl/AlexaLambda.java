package com.moxieit.orderplatform.lambda.function.service.impl;

import com.amazonaws.services.lambda.runtime.Context;
import com.google.gson.Gson;
import com.moxieit.orderplatform.function.service.api.AlexaDTO;
import com.moxieit.orderplatform.function.service.api.AlexaService;
import com.moxieit.orderplatform.lambda.response.AlexaResponse;
import com.moxieit.orderplatform.lambda.response.BaseResponse;
import com.moxieit.orderplatform.lambda.response.Card;
import com.moxieit.orderplatform.lambda.response.OutputSpeech;
import com.moxieit.orderplatform.lambda.response.Reprompt;
import com.moxieit.orderplatform.lambda.response.Response;

public class AlexaLambda implements AlexaService {

	@Override
	public BaseResponse serveLex(AlexaDTO input, Context context) {

		Gson gson = new Gson();
		String json = gson.toJson(input);
		System.out.println(json);

		AlexaResponse alexaResponse = new AlexaResponse();
		Response response = new Response();
		OutputSpeech outputSpeech = new OutputSpeech();
		/*
		 * outputSpeech.setText(
		 * "Today will provide you a new learning opportunity.  Stick with it and the possibilities will be endless. Can I help you with anything else?"
		 * );
		 */
		outputSpeech.setSsml("<speak>Would you like a Easy order or a Recent order or Start a new Order.</speak>");
		outputSpeech.setType("SSML");
		Card card = new Card();
		card.setTitle("Audio");
		card.setType("Simple");
		card.setContent("Would you like a Easy order or a Recent order or Start a new Order.");
		Reprompt reprompt = new Reprompt();
		OutputSpeech outputSpeech1 = new OutputSpeech();
		outputSpeech1.setText("Can I help you with anything else?");
		outputSpeech1.setType("PlainText");
		reprompt.setOutputSpeech(outputSpeech1);
		response.setShouldEndSession(true);
		response.setReprompt(reprompt);
		response.setCard(card);
		response.setOutputSpeech(outputSpeech);
		alexaResponse.setVersion("1.0");

		alexaResponse.setResponse(response);

		String json1 = gson.toJson(alexaResponse);
		System.out.println(json1);
		return alexaResponse;
	}

}
