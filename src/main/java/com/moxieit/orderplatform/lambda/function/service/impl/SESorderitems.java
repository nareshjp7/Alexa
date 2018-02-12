package com.moxieit.orderplatform.lambda.function.service.impl;

import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.S;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import org.apache.commons.io.FileUtils;

import com.amazonaws.event.DeliveryMode.Check;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder;
import com.amazonaws.services.dynamodbv2.xspec.ScanExpressionSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.ListVerifiedEmailAddressesResult;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.VerifyEmailAddressRequest;
import com.amazonaws.util.StringUtils;
import com.moxieit.orderplatform.DB.DBService;
import com.moxieit.orderplatform.lambda.request.SesRequest;
import com.moxieit.orderplatform.lambda.response.SesResponce;

public class SESorderitems implements RequestHandler<String, String> {

	private static final String FROM = "no-reply@moxieit.com";
	private static final String SUBJECT = "Customer Order Confirmation";
	

	static int counter = 0;
	static int instanceCounter = 0;

	public SesResponce getSES(SesRequest sesRequest) throws IOException {
		StringBuilder BODY = new StringBuilder();
		SesResponce sesResponse = new SesResponce();
	
		String ordertableuuid = sesRequest.getOrdertableuuid();
		DynamoDB dynamoDB = DBService.getDBConnection();
		Table restaurantTable = dynamoDB.getTable("Restaurant");
		Table orderItemTable = dynamoDB.getTable("OrderItems");
		Table orderTable = dynamoDB.getTable("Order");
		Item userid = orderTable.getItem("uuid", ordertableuuid);
		String botName = userid.getString("botName");
		Item restaurantItem = restaurantTable.getItem("botName", botName);
		String botemailId = restaurantItem.getString("emailId");
		String phone_no = restaurantItem.getString("phone_no");
		String restaurantName1 = restaurantItem.getString("restaurantName");
		String street_1 = restaurantItem.getString("street_1");
		String city = restaurantItem.getString("city");
		String state = restaurantItem.getString("state");
		String zipCode = restaurantItem.getString("zipCode");
		String restaurantaddress = restaurantName1+","+street_1+","+city+","+state+","+zipCode;
		
		String restaurantName = null;
		
		if (botName != null) {
				restaurantName = restaurantItem.getString("restaurantName");
		} else
		{
			restaurantName = "OnlineOrder";
		}
		String address1 = userid.getString("address");
		String address2 = "NULL";
		if (address1.equals(address2)) {
			String pickUp = userid.getString("pickUp");
			address1 = "PickUp in "+pickUp;	
		}
		String userName = userid.getString("userName");
		if (userName == null){
			userName = "Customer";
		}
	
		String orderDate = userid.getString("orderDate");
		String orderFrom = userid.getString("orderFrom");
		String phoneNumber = userid.getString("phoneNumber");
		String totalBill = userid.getString("totalBill");
		Number totalBill1 = (Number) userid.get("totalBillWithTax");
		String totalBillWithTax = String.valueOf(totalBill1);
		Number tax = userid.getNumber("tax");
		String taxValue = String.valueOf(tax);		
		//String TO = botemailId;
		String[] TO = {botemailId};		
		
		ScanExpressionSpec xspec1 = new ExpressionSpecBuilder().withCondition(S("orderuuid").eq(ordertableuuid))
				.buildForScan();
		ItemCollection<ScanOutcome> scan1 = orderItemTable.scan(xspec1);
		Consumer<Item> action1 = new Consumer<Item>() {
			@Override
			public void accept(Item t) {					
						
				Table menuItemTable = dynamoDB.getTable("Menu_Items");
				Object categoryObject = t.getString("categoryId");
				Object itemObject = t.getString("menuItemId");
				Item menuItem = menuItemTable.getItem("categoryId", categoryObject, "itemId", itemObject);
				//String Image = menuItem.getString("image");				
				
				/*BODY.append("<tr><td class=\"thick-line text-center\">").append(menuItem.getString("itemName"))
				.append("</td><td class=\"thick-line text-center\">").append(t.getString("spiceyLevel"))
				.append("</td><td class=\"thick-line text-center\">").append(t.getString("quantity"))
				.append("</strong></td><td class=\"thick-line text-center\">$")
				.append(t.getString("itemCost")).append("</td></tr>");	*/
				
				BODY.append("<tr style='margin:0;vertical-align:baseline;height:20px;background:#f5f3f0; border-top:1px solid #fff'><td style='padding:15px;'>")
				.append(t.getString("quantity")).append(" x ").append(menuItem.getString("itemName"))
				/*.append("</td><td class=\"thick-line\">").append(t.getString("spiceyLevel"))*/
				.append("</td><td style='text-align:right;padding:15px;'>").append(t.getString("itemCost")).append(".00 USD")
						
				.append("</td></tr>");

				
				}
											
		};
		scan1.forEach(action1);		
		String itemsbody = BODY.toString();
	System.out.println(itemsbody);
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("file/ses.html").getFile());
		try {
			
			String readFileToString = FileUtils.readFileToString(file);
			String orderItemReplace = StringUtils.replace(readFileToString, "orderitem", itemsbody);
			String phoneNumberReplace = StringUtils.replace(orderItemReplace, "phoneNumber", phoneNumber);
			String restaddressadd = StringUtils.replace(phoneNumberReplace, "RestaurantAddress", restaurantaddress);
			String restphoneadd = StringUtils.replace(restaddressadd, "RestaurantPhone", phone_no);
			String emailadd = StringUtils.replace(restphoneadd, "RestaurantEmail", botemailId);
			String useraddress = StringUtils.replace(emailadd, "CustAddress", address1);
			String dateoforder = StringUtils.replace(useraddress, "OrderDate", orderDate);
			String sourceorder = StringUtils.replace(dateoforder, "orderFrom", orderFrom);
			String totalbillamount = StringUtils.replace(sourceorder, "TotalBillValue", totalBill);
			String totalbillwithtax = StringUtils.replace(totalbillamount, "TotalBillWithTax", totalBillWithTax);
			String userName1 = StringUtils.replace(totalbillwithtax, "Customer", userName);
			String taxamount = StringUtils.replace(userName1, "TaxValue", taxValue);
			String check = "false";
			String restaurantNameValue = null;
			if (botName != null) {
				restaurantNameValue = StringUtils.replace(taxamount, "RestaurantName", restaurantName);
				check = "true";
			}
			AmazonSimpleEmailService ses = new AmazonSimpleEmailServiceClient();
			verifyEmailAddress(ses, FROM);
			Destination destination = new Destination().withToAddresses(TO);
			Content subject = new Content().withData(SUBJECT);
			// Content textBody = new Content().withData(BODY);
			Content htmlContent = null;
			if (check.equals("true")) {
				htmlContent = new Content().withData(restaurantNameValue);
			} else {
				htmlContent = new Content().withData(taxamount);
			}			
			Body body = new Body().withHtml(htmlContent);
			Message message1 = new Message().withSubject(subject).withBody(body);
			SendEmailRequest request2 = new SendEmailRequest().withSource(FROM).withDestination(destination)
					.withMessage(message1);

			message1.setBody(body);

			try {
				System.out.println("Attempting to send an email through Amazon SES using the AWS SDK for Java.");

				AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
						.withRegion(Regions.US_EAST_1).build();

				// Send the email.
				client.sendEmail(request2);
				System.out.println("Email sent!");

			} catch (Exception ex) {
				System.out.println("The email was not sent.");
				System.out.println("Error message: " + ex.getMessage());
			}

			sesResponse.setStatus("success");
			sesResponse.setMessage("Successfully sent the Messages");

			// return sesResponse;

		} catch (Exception e) {
			// TODO: handle exception
		}
		return sesResponse;

	}

	private static void verifyEmailAddress(AmazonSimpleEmailService ses, String address) {
		ListVerifiedEmailAddressesResult verifiedEmails = ses.listVerifiedEmailAddresses();
		if (verifiedEmails.getVerifiedEmailAddresses().contains(address))
			return;

		ses.verifyEmailAddress(new VerifyEmailAddressRequest().withEmailAddress(address));
		System.out.println("Please check the email address " + address + " to verify it");
		System.exit(0);
	}

public static void main(String[] args) throws IOException {
		SESorderitems Sesorderitems = new SESorderitems();		
		SesRequest sesRequest = new SesRequest();
		 sesRequest.setOrdertableuuid("58d11ffc-4e70-4096-bbc4-40b9a2de7875");
	
		Sesorderitems.getSES(sesRequest);
		
	}

	@Override
	public String handleRequest(String input, Context context) {
		// TODO Auto-generated method stub
		return null;
	}
}
