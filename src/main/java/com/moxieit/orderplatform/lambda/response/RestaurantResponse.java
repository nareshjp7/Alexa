package com.moxieit.orderplatform.lambda.response;

public class RestaurantResponse {

	private String restaurantId;

	private String message;

	private String status;

	public RestaurantResponse(String restaurantId) {
		super();
		this.restaurantId = restaurantId;
	}

	public String getMessage() {
		return message;
	}

	public RestaurantResponse(String restaurantId, String message, String status) {
		super();
		this.restaurantId = restaurantId;
		this.message = message;
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(String restaurantId) {
		this.restaurantId = restaurantId;
	}

}
