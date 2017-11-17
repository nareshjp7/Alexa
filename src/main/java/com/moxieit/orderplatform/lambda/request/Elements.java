package com.moxieit.orderplatform.lambda.request;

import com.moxieit.orderplatform.lambda.response.Buttons;

public class Elements {

	private String title;

	private String subtitle;

	private Number quantity;

	private Number price;

	private String currency;

	private String image_url;

	Buttons[] buttons;

	public Buttons[] getButtons() {
		return buttons;
	}

	public void setButtons(Buttons[] buttons) {
		this.buttons = buttons;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public Number getQuantity() {
		return quantity;
	}

	public void setQuantity(Number quantity) {
		this.quantity = quantity;
	}

	public Number getPrice() {
		return price;
	}

	public void setPrice(Number price) {
		this.price = price;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getImage_url() {
		return image_url;
	}

	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}

}
