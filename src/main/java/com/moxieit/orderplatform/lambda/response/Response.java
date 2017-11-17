package com.moxieit.orderplatform.lambda.response;

public class Response {

	OutputSpeech outputSpeech;

	Card card;

	Reprompt reprompt;

	private boolean shouldEndSession;

	public boolean isShouldEndSession() {
		return shouldEndSession;
	}

	public void setShouldEndSession(boolean shouldEndSession) {
		this.shouldEndSession = shouldEndSession;
	}

	public OutputSpeech getOutputSpeech() {
		return outputSpeech;
	}

	public void setOutputSpeech(OutputSpeech outputSpeech) {
		this.outputSpeech = outputSpeech;
	}

	public Card getCard() {
		return card;
	}

	public void setCard(Card card) {
		this.card = card;
	}

	public Reprompt getReprompt() {
		return reprompt;
	}

	public void setReprompt(Reprompt reprompt) {
		this.reprompt = reprompt;
	}

}
