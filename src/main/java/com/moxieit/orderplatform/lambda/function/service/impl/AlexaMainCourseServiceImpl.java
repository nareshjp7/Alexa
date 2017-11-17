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

public class AlexaMainCourseServiceImpl implements AlexaService {

	@Override
	public BaseResponse serveLex(AlexaDTO alexaDTO, Context context) {
		AlexaResponse alexaResponse = new AlexaResponse();
		Response response = new Response();
		OutputSpeech outputSpeech = new OutputSpeech();
		outputSpeech.setSsml(
				"<speak>We have FishCurry cost 10 dillors , Paneer Butter Masala cost 7 dollors , Tadka Dal cost 7 dollors ,Chicken Chettinadu cost 9 dollors , Chicken Vindaloo cost 9 dollors, kadai paneer cost 7 dollors , Malai Kofta cost 7 dollors , Bhindi do Pyaza cost 7 dollors , Gutti Vankaya cost 7 dollors , Goat Curry cost 10 dollors , Butter Chicken cost 9 dollors , Pepper Chicken cost 9 dollors and Chicken curry cost 9 dollors What would you like.</speak>");
		outputSpeech.setType("SSML");
		Card card = new Card();
		card.setTitle("Audio");
		card.setType("Simple");
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
		return alexaResponse;
	}

}
