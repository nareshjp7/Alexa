package com.moxieit.orderplatform.S3;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

public class S3Service {

	

	public static AmazonS3 getService() {
		String accessKey = "AKIAI3SQEJTGL5AKUPRQ";
		String secretKey = "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";
		BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(
			accessKey, secretKey);
		AmazonS3 s3Client = new AmazonS3Client(basicAWSCredentials);
		return s3Client;
	}
}
