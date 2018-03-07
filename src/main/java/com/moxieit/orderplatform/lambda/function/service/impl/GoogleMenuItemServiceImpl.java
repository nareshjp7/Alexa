package com.moxieit.orderplatform.lambda.function.service.impl;

import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.S;

import java.util.UUID;
import java.util.function.Consumer;

import org.apache.commons.lang3.text.WordUtils;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.Page;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder;
import com.amazonaws.services.dynamodbv2.xspec.ScanExpressionSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.moxieit.orderplatform.DB.DBService;
import com.moxieit.orderplatform.function.service.api.AlexaDTO;
import com.moxieit.orderplatform.function.service.api.GoogleDTO;
import com.moxieit.orderplatform.lambda.response.BaseResponse;
import com.moxieit.orderplatform.lambda.response.GoogleResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GoogleMenuItemServiceImpl extends AbstractGoogleOrderServiceImpl {

	@Override
	public BaseResponse serveLex(GoogleDTO googleDTO, Context context) {
		// TODO Auto-generated method stub
		DynamoDB dynamoDB = DBService.getDBConnection();
		String botName = googleDTO.getBotName();
		String restaurantId = googleDTO.getRestaurantId();
		Table orderItemTable = dynamoDB.getTable("OrderItems");
		Table menuItemsTable = dynamoDB.getTable("Menu_Items");
		String itemPrice=null;
		String categoryId=null;
		String itemId=null;
		String itemName=null;
		Boolean isSpicy=null;	
		 ArrayList<String> obj = new ArrayList<String>();
		String MenuItemName = WordUtils.capitalize(googleDTO.getRequest());
		System.out.println("my request of item name: "+MenuItemName);
		try {
			HashMap<String, Object> valueMap = new HashMap<String, Object>();
			valueMap.put(":v_id", restaurantId);
			valueMap.put(":letter1", restaurantId);
			AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
			
		
				
			ScanRequest scanRequest = new ScanRequest()
			    .withTableName("Menu_Items").withAttributesToGet("itemName","price","categoryId","itemId","isSpicy");
			
			ScanResult result = client.scan(scanRequest);
		
			for (Map<String, AttributeValue> item : result.getItems()){
				
				
				String x = item.get("itemName").getS();
				String categoryid = item.get("categoryId").getS();
								
				 if(x.replaceAll("\\s+","").equalsIgnoreCase(MenuItemName.replaceAll("\\s+","")) ||
						 MenuItemName.toLowerCase().replaceAll("\\s+","").contains(x.replaceAll("\\s+","").toLowerCase())){
					 System.out.println("my categoryid: "+categoryid);
					 if(categoryid.startsWith(restaurantId)){
						 						
						 obj.add(item.get("itemName").getS().replaceAll("\\s+",""));
					
					// itemName = item.get("itemName").getS().replaceAll("\\s+","");				
						itemPrice=(String) item.get("price").getN();					
						categoryId=(String) item.get("categoryId").getS();					
						itemId=(String) item.get("itemId").getS();						
						isSpicy= (Boolean) item.get("isSpicy").getBOOL();
								
						//break;
					 }
				 }
				
				 
			}
			  String longestString = getLongestString(obj);
			  itemName = longestString;
	
			
			/*ScanExpressionSpec xspec = new ExpressionSpecBuilder().withCondition(S("itemName").in(MenuItemName))
					.buildForScan();
			
			System.out.println("my xspec: "+xspec);
			ItemCollection<ScanOutcome> scan = menuItemsTable.scan(xspec);
			System.out.println("my scan: "+scan);
			Item menuItem = null;
			Page<Item, ScanOutcome> firstPage = scan.firstPage();	
			System.out.println("my firstPage: "+firstPage);
			if (firstPage.iterator().hasNext()) {
				System.out.println("my firstPage iterator: "+firstPage);
				String MenuItemName1 = WordUtils.capitalize(googleDTO.getRequest());
				//Item menuItem1 = firstPage.iterator().forEachRemaining(action);next();
				String item =(String) menuItem.get("itemName");
				System.out.println("my trim string: "+item.trim());
				System.out.println((String) menuItem.get("itemName"));
				
				menuItem = firstPage.iterator().next();
				itemPrice=(String) menuItem.get("price");
				categoryId=(String) menuItem.get("categoryId");
				itemId=(String) menuItem.get("itemId");
				itemName=(String) menuItem.get("itemName");
				isSpicy= (Boolean) menuItem.get("isSpicy");
				
			}*/
			
			Item order = getOrder(googleDTO.getUserId());
			System.out.println("order: "+order);
			String orderuuid = order.getString("uuid");
			System.out.println("1st order: "+order);
			String uuid = UUID.randomUUID().toString();
			Item orderItem = new Item();
			orderItem.withString("uuid", uuid).withString("orderuuid", orderuuid).withString("itemName", itemName)
			.withNumber("creationDate", System.currentTimeMillis()).withString("categoryId", categoryId).withString("menuItemId", itemId)	
			.withString("userId", googleDTO.getUserId()).withNumber("quantity", 0).withString("itemCost", itemPrice).withBoolean("isSpicy", isSpicy)
			.withString("itemQuantityAdd", "false");
			orderItemTable.putItem(orderItem);
			GoogleResponse googleResponse = new GoogleResponse();
			googleResponse.setSpeech("How much quantity do you want for this order.");	
			System.out.println("How much quantity do you want for this order.");
			return googleResponse;
		} catch (Exception e){
			StringBuilder BODY = new StringBuilder();
			ScanExpressionSpec xspec4 = new ExpressionSpecBuilder().withCondition(S("itemName").contains(MenuItemName))
					.buildForScan();
			ItemCollection<ScanOutcome> scan4 = menuItemsTable.scan(xspec4);

			Consumer<Item> action4 = new Consumer<Item>() {
				@Override
				public void accept(Item t3) {
					
					String item =(String) t3.get("itemName");						
				BODY.append(item).append(", ");						

				}

			};
			scan4.forEach(action4);

			String itemsbody = BODY.toString();
			System.out.println(itemsbody);
			GoogleResponse googleResponse = new GoogleResponse();
			googleResponse.setSpeech("Sorry. I am not sure, please speck from the items "+itemsbody+" or speck menu.");		
			
			return googleResponse;
		}
	
	}

	public static String getLongestString(ArrayList<String> array) {
	      int maxLength = 0;
	      String longestString = null;
	      for (String s : array) {
	          if (s.length() > maxLength) {
	              maxLength = s.length();
	              longestString = s;
	          }
	      }
	      return longestString;
	  }

	public static void main(String[] args) {
		GoogleMenuItemServiceImpl googleService = new GoogleMenuItemServiceImpl();
		GoogleDTO googleDTO = new GoogleDTO();
		googleDTO.setUserId("ABwppHHu5052upleEQrsWad_QHD4CayzF4mk24Gu1Pd3Dxn4JVd7jBSiTi3CXE7k00B7huIZJqu3QIwPAMiIh44b6Q");
		googleDTO.setRequest("order garlic naan");
		googleDTO.setRestaurantId("23");
		googleDTO.setBotName("SITARA");
		Context context = null ;
		googleService.serveLex(googleDTO, context);
		}
}
