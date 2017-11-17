package com.moxieit.orderplatform.lambda.request;

public class Summary {

	private Number subtotal;

	private String shipping_cost;

	private Number total_tax;

	private Number total_cost;

	public Number getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(Number subtotal) {
		this.subtotal = subtotal;
	}

	public String getShipping_cost() {
		return shipping_cost;
	}

	public void setShipping_cost(String shipping_cost) {
		this.shipping_cost = shipping_cost;
	}

	public Number getTotal_tax() {
		return total_tax;
	}

	public void setTotal_tax(Number total_tax) {
		this.total_tax = total_tax;
	}

	public Number getTotal_cost() {
		return total_cost;
	}

	public void setTotal_cost(Number total_cost) {
		this.total_cost = total_cost;
	}

}
