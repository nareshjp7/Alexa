package com.moxieit.orderplatform.DB;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;

public class DBService {

	private String regionName;

	public String getRegion() {
		return regionName;
	}

	public void setRegion(String regionName) {
		this.regionName = regionName;
	}

	public DBService() {
	}

	public static DynamoDB getDBConnection() {
		AmazonDynamoDBClient amazonDynamoDBClient = getDynamoDBClient();
		DynamoDB dynamoDB = new DynamoDB(amazonDynamoDBClient);
		return dynamoDB;
	}

	public static AmazonDynamoDBClient getDynamoDBClient() {
		BasicAWSCredentials basicAWSCredentials = getAWSCredentials();
		AmazonDynamoDBClient amazonDynamoDBClient = new AmazonDynamoDBClient();
		return amazonDynamoDBClient;
	}

	private static BasicAWSCredentials getAWSCredentials() {
		String accessKey = "AKIAIDKQPYHF6A7JGL6Q";
		String secretKey = "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";
		BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(accessKey, secretKey);
		return basicAWSCredentials;
	}

}
