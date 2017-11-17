package com.moxieit.orderplatform.function.service.api;

public class AlexaDTO {

	private String applicationId;
	private String intentName;

	private String userId;

	private String request;
	private String launchRequest;
	
	private String consentToken;
	
	private String deviceId;
	
	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public String getConsentToken() {
		return consentToken;
	}

	public void setConsentToken(String consentToken) {
		this.consentToken = consentToken;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getIntentName() {
		return intentName;
	}

	public void setIntentName(String intentName) {
		this.intentName = intentName;
	}

	public String getLaunchRequest() {
		return launchRequest;
	}

	public void setLaunchRequest(String launchRequest) {
		this.launchRequest = launchRequest;
	}

}
