package com.moxieit.orderplatform.lambda.response;

public class Price_list {

	private String label;
	private String amount;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		return "Price_list [label=" + label + ", amount=" + amount + "]";
	}

}
