package com.moxieit.orderplatform.lambda.response;

public class CommonResponse {

	private String message;

	private String type;

	private String status;

	public CommonResponse() {
		// TODO Auto-generated constructor stub
	}

	public CommonResponse(String message, String type) {
		super();
		this.message = message;
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
