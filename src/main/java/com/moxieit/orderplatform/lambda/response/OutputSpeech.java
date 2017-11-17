package com.moxieit.orderplatform.lambda.response;

public class OutputSpeech extends CommonAlexaResponse {

	private String text;

	private String ssml;

	public String getSsml() {
		return ssml;
	}

	public void setSsml(String ssml) {
		this.ssml = ssml;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
