package com.moxieit.orderplatform.lambda.request;

import com.moxieit.orderplatform.lambda.response.Slots;

public class CurrentIntent {

	private String name;
	Slots slots;
	private String confirmationStatus;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Slots getSlots() {
		return slots;
	}

	public void setSlots(Slots slots) {
		this.slots = slots;
	}

	public String getConfirmationStatus() {
		return confirmationStatus;
	}

	public void setConfirmationStatus(String confirmationStatus) {
		this.confirmationStatus = confirmationStatus;
	}

}
