package com.moxieit.orderplatform.lambda.response;

public class MenuItemResponse {

	private String itemName;
	private String image;
	private String itemId;
	private String categoryId;
	private Number price;
	private String itemType;
	private String message;
	private String status;
	private String isSpicy;
	private String monToFri;
	private String satToSun;
	public MenuItemResponse() {
		// TODO Auto-generated constructor stub
	}

	public MenuItemResponse(String message, String status) {
		super();
		this.message = message;
		this.status = status;

	}

	public String getIsSpicy() {
		return isSpicy;
	}

	public void setIsSpicy(String isSpicy) {
		this.isSpicy = isSpicy;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public Number getPrice() {
		return price;
	}

	public void setPrice(Number price) {
		this.price = price;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	@Override
	public String toString() {
		return "MenuItemResponse [itemName=" + itemName + ", image=" + image + ", itemId=" + itemId + ", categoryId="
				+ categoryId + ", price=" + price + ", itemType=" + itemType + ", isSpicy=" + isSpicy + ", message=" + message + "]";
	}

	public String getMonToFri() {
		return monToFri;
	}

	public void setMonToFri(String monToFri) {
		this.monToFri = monToFri;
	}

	public String getSatToSun() {
		return satToSun;
	}

	public void setSatToSun(String satToSun) {
		this.satToSun = satToSun;
	}

}
