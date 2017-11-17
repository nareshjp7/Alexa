package com.moxieit.orderplatform.lambda.request;

public class RestaurantRequestImpl extends CommonRequest {

	private String id;

	private String zipCode;

	private String street_1;

	private String city;

	private String state;

	private String country;

	private String phone_no;

	private String emailId;

	private String monToFriHours;

	private String satToSunHours;

	private String restaurantName;

	private String type;

	private String botName;

	private String pageName;

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getBotName() {
		return botName;
	}

	public void setBotName(String botName) {
		this.botName = botName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRestaurantName() {
		return restaurantName;
	}

	public void setRestaurantName(String restaurantName) {
		this.restaurantName = restaurantName;
	}

	public String getStreet_1() {
		return street_1;
	}

	public void setStreet_1(String street_1) {
		this.street_1 = street_1;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPhone_no() {
		return phone_no;
	}

	public void setPhone_no(String phone_no) {
		this.phone_no = phone_no;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMonToFriHours() {
		return monToFriHours;
	}

	public void setMonToFriHours(String monToFriHours) {
		this.monToFriHours = monToFriHours;
	}

	public String getSatToSunHours() {
		return satToSunHours;
	}

	public void setSatToSunHours(String satToSunHours) {
		this.satToSunHours = satToSunHours;
	}

	public String getPageName() {
		return pageName;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

}
