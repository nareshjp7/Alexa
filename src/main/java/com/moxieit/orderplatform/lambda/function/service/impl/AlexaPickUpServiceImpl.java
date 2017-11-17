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

public class AlexaPickUpServiceImpl implements AlexaService {

	@Override
	public BaseResponse serveLex(AlexaDTO alexaDTO, Context context) {
		AlexaResponse alexaResponse = new AlexaResponse();
		Response response = new Response();
		OutputSpeech outputSpeech = new OutputSpeech();
		outputSpeech
				.setSsml("<speak>Thank you for your order.Please collect you order within 30 minuites and If you want to add More Items speak Menu if not you can speak Checkout.</speak>");
		outputSpeech.setType("SSML");
		Card card = new Card();
		card.setTitle("Audio");
		card.setType("Simple");
		card.setContent("Thank you for your order.Please collect you order within 30 minuites and If you want to add More Items speak Menu if not you can speak Checkout.");
		Reprompt reprompt = new Reprompt();
		OutputSpeech outputSpeech1 = new OutputSpeech();
		outputSpeech1.setText("Please select your option.");
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
