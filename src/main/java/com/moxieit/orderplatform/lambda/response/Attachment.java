package com.moxieit.orderplatform.lambda.response;

public class Attachment {

	private String type;

	Payload payload;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Payload getPayload() {
		return payload;
	}

	public void setPayload(Payload payload) {
		this.payload = payload;
	}

}
