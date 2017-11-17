package com.moxieit.orderplatform.lambda.request;

public class MenuItemsRequest extends CommonRequest {

	private String categoryId;

	private String itemId;

	private String isSpicy;

	private String price;

	private String itemType;

	private String itemName;

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getIsSpicy() {
		return isSpicy;
	}

	public void setIsSpicy(String isSpicy) {
		this.isSpicy = isSpicy;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

}
