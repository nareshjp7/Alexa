package com.moxieit.orderplatform.lambda.response;

public class DailogActionResponse {

	private String type;
	private String fulfillmentState;
	Message message;

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFulfillmentState() {
		return fulfillmentState;
	}

	public void setFulfillmentState(String fulfillmentState) {
		this.fulfillmentState = fulfillmentState;
	}

}
