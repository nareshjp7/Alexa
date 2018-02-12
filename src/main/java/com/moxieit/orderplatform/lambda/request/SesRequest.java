package com.moxieit.orderplatform.lambda.request;

public class SesRequest {

	private String phoneNumber;

	private String 	ordertableuuid;
	

	public String getOrdertableuuid() {
		return ordertableuuid;
	}

	public void setOrdertableuuid(String ordertableuuid) {
		this.ordertableuuid = ordertableuuid;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
}
