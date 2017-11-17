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

public class AlexaSideOrdersServiceImpl implements AlexaService {

	@Override
	public BaseResponse serveLex(AlexaDTO alexaDTO, Context context) {
		AlexaResponse alexaResponse = new AlexaResponse();
		Response response = new Response();
		OutputSpeech outputSpeech = new OutputSpeech();
		outputSpeech.setSsml(
				"<speak>We have Poori cost 2 dollors , Bhatura cost 2 dollors , Naan cost 2 dollors , Garlic Naan cost 2 dollors , Rice cost 1 dollor , Chapathi cost 1 dollor and Paratha cost 2 dollors  What would you like.</speak>");
		outputSpeech.setType("SSML");
		Card card = new Card();
		card.setTitle("Audio");
		card.setType("Simple");
		card.setContent("We have Poori cost 2 dollors , Bhatura cost 2 dollors , Naan cost 2 dollors , Garlic Naan cost 2 dollors , Rice cost 1 dollor , Chapathi cost 1 dollor and Paratha cost 2 dollors What would you like.");
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
