package com.moxieit.orderplatform.lambda.response;

import java.util.List;

public class MainMenuCategoryResponse {

	List<MenuCategoryResponse> menuCategoryResponse;

	private String status;

	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<MenuCategoryResponse> getMenuCategoryResponse() {
		return menuCategoryResponse;
	}

	public void setMenuCategoryResponse(List<MenuCategoryResponse> menuCategoryResponse) {
		this.menuCategoryResponse = menuCategoryResponse;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
