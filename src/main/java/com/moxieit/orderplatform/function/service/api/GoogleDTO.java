package com.moxieit.orderplatform.function.service.api;

public class GoogleDTO {
	
	public static final Object Response = null;
	private String intentName;
	private String userId;
	private String request;
	private String formattedAddress;
	private String userName;
	private String permissionsGranted;
	private String quantityValue;
	private String TransactionCheckResult;



	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getIntentName() {
		return intentName;
	}

	public void setIntentName(String intentName) {
		this.intentName = intentName;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public String getFormattedAddress() {
		return formattedAddress;
	}

	public void setFormattedAddress(String formattedAddress) {
		this.formattedAddress = formattedAddress;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPermissionsGranted() {
		return permissionsGranted;
	}

	public void setPermissionsGranted(String permissionsGranted) {
		this.permissionsGranted = permissionsGranted;
	}

	public String getQuantityValue() {
		return quantityValue;
	}

	public void setQuantityValue(String quantityValue) {
		this.quantityValue = quantityValue;
	}

	public String getTransactionCheckResult() {
		return TransactionCheckResult;
	}

	public void setTransactionCheckResult(String transactionCheckResult) {
		TransactionCheckResult = transactionCheckResult;
	}

	


}
