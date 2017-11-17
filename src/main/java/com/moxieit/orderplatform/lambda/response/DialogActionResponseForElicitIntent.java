package com.moxieit.orderplatform.lambda.response;

public class DialogActionResponseForElicitIntent {

	private String type;
	private Message message;
	private ResponseCard responseCard;
	private String fulfillmentState;
	private String intentName;
	private Slots slots;
	private String slotToElicit;

	public Slots getSlots() {
		return slots;
	}

	public String getSlotToElicit() {
		return slotToElicit;
	}

	public void setSlotToElicit(String slotToElicit) {
		this.slotToElicit = slotToElicit;
	}

	public String getIntentName() {
		return intentName;
	}

	public void setIntentName(String intentName) {
		this.intentName = intentName;
	}

	public String getFulfillmentState() {
		return fulfillmentState;
	}

	public void setFulfillmentState(String fulfillmentState) {
		this.fulfillmentState = fulfillmentState;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public ResponseCard getResponseCard() {
		return responseCard;
	}

	public void setResponseCard(ResponseCard responseCard) {
		this.responseCard = responseCard;
	}

	public void setSlots(Slots slots) {
		this.slots = slots;
	}

	@Override
	public String toString() {
		return "DialogActionResponseForElicitIntent [type=" + type + ", message=" + message + ", responseCard="
				+ responseCard + ", fulfillmentState=" + fulfillmentState + ", intentName=" + intentName + ", slots="
				+ slots + ", slotToElicit=" + slotToElicit + "]";
	}

}
