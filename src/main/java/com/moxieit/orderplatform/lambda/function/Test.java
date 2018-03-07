package com.moxieit.orderplatform.lambda.function;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.moxieit.orderplatform.function.service.api.GoogleDTO;
import com.moxieit.orderplatform.lambda.function.service.impl.GoogleMenuItemServiceImpl;
import com.moxieit.orderplatform.lambda.function.service.impl.GoogleRecentOrderServiceImpl;
import com.moxieit.orderplatform.lambda.response.BaseResponse;

public class Test  {
	public BaseResponse serveLex(GoogleDTO googleDTO, Context context) {
	     String[] TYPES = { "SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT" };
		 String timestamp = "2018-02-20T10:35:17.21Z";
		 String openTime1 = "TUE - WED : 11:00AM - 10:00PM";
		 String openTime2 = "THU - SUN : 09:00AM - 10:00PM";
		 
		 Date date = new Date();
			Calendar calendar = Calendar.getInstance();
			//TimeZone timeZone = calendar.getTimeZone();
			calendar.setTime(date);
			int todayweek = calendar.get(Calendar.DAY_OF_WEEK);
			  System.out.println("todayweek "+todayweek);
			SimpleDateFormat formatter = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");
			String strDate = formatter.format(date);
			System.out.println("StartDate:" + strDate);
	        String usertimeZone = strDate ;	
	        formatter.setTimeZone(TimeZone.getTimeZone("IST"));
			usertimeZone = formatter.format(date);
		     //  System.out.println("my gmt time:" + usertimeZone);
	/*if(countryName.contains("US")) {
		formatter.setTimeZone(TimeZone.getTimeZone("GMT-5"));
		usertimeZone = formatter.format(date);
	       System.out.println("my gmt time:" + usertimeZone);
	}else if (countryName.contains("India")){
		
		formatter.setTimeZone(TimeZone.getTimeZone("IST"));
	        usertimeZone = formatter.format(date);
	       System.out.println("my india time:" + usertimeZone);
	} else	{
		
		 System.out.println("Restaurant Country:" + countryName);
	}*/
	
	int time1 = Integer.parseInt(usertimeZone.substring(17, 19));

	  System.out.println("my time time:" + time1);
	
		 String [] arrOfStr1 = openTime1.split(":", 2);
		 String [] arrOfStr2 = openTime2.split(":", 2);
		//   for (String a : arrOfStr)
	          
	            
	            String [] x1 = arrOfStr1[0].split("-", 2);
	          	int weekopen1 = Arrays.asList(TYPES).lastIndexOf(x1[0].toString().trim())+1;	
	           	int weekclose1 = Arrays.asList(TYPES).lastIndexOf(x1[1].toString().trim())+1;	
	           	
	            String [] x2 = arrOfStr2[0].split("-", 2);
	        	int weekopen2 = Arrays.asList(TYPES).lastIndexOf(x2[0].toString().trim())+1;	
	           	int weekclose2 = Arrays.asList(TYPES).lastIndexOf(x2[1].toString().trim())+1;
					
	            SimpleDateFormat displayFormat = new SimpleDateFormat("k");
	            SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mma");	
	            
	            String [] y1 = arrOfStr1[1].split("-", 2);		                   
	            int dayClose1 = 0;
	        	int dayOpen1 = 0;
	        	String [] y2 = arrOfStr2[1].split("-", 2);	
	        	int dayClose2 = 0;
		        int dayOpen2 = 0;
		        
	        	try {
	        		 dayOpen1 =Integer.parseInt(displayFormat.format(parseFormat.parse(y1[0])));
					 dayClose1 =Integer.parseInt(displayFormat.format(parseFormat.parse(y1[1])));
					 
					 dayOpen2 =Integer.parseInt(displayFormat.format(parseFormat.parse(y2[0])));
					 dayClose2 =Integer.parseInt(displayFormat.format(parseFormat.parse(y2[1])));
				  	
				} catch (NumberFormatException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (java.text.ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	        	  System.out.println("time1 "+time1);
	        	  System.out.println("dayOpen1 "+dayOpen1);
	        	  System.out.println("dayClose1 "+dayClose1);
	        	  System.out.println("weekopen1 "+weekopen1);
	        	  System.out.println("weekclose1 "+weekclose1);	        	
	        	  System.out.println("dayOpen2 "+dayOpen2);
	        	  System.out.println("dayClose2 "+dayClose2);
	        	  System.out.println("weekopen2 "+weekopen2);
	        	  System.out.println("weekclose2 "+weekclose2);
	        	  System.out.println("todayweek "+todayweek);
	        	  if (todayweek >= weekopen1 && todayweek <= weekclose1){
	        	if (time1 >= dayOpen1 && time1 < dayClose1) {
	    			System.out.println("Time1 is true1");
	    			
	    		}
	        	  } else if (todayweek >= weekopen2 && todayweek <= weekclose2){
	        		  if(time1 >= dayOpen2 && time1 < dayClose2){
	        			  System.out.println("Time2 is true2");
	        		  }
	        	  } else if(weekopen2 >= weekclose2  ){
	        		  if (weekclose2 >= todayweek || weekopen2 <= todayweek){
	        			  System.out.println("Time3 is true3");
	        		  }
	        	  }
		 System.out.println("my timestamp:" + timestamp);
			
	
			
		/*	TimeZone utc = TimeZone.getTimeZone("UTC");
			SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			f.setTimeZone(utc);
			GregorianCalendar cal = new GregorianCalendar(utc);
			try {
				cal.setTime(f.parse(timestamp));
				
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(cal.getTime());
			Date time = cal.getTime();
			String latestDate = formatter1.format(time);
			System.out.println("my latestDate:" + latestDate);*/
		
			return null;
	 }
	

		public static void main(String[] args) {
			Test googleService = new Test();
			GoogleDTO googleDTO = new GoogleDTO();
			googleDTO.setUserId("ABwppHHu5052upleEQrsWad_QHD4CayzF4mk24Gu1Pd3Dxn4JVd7jBSiTi3CXE7k00B7huIZJqu3QIwPAMiIh44b6Q");
			googleDTO.setRequest("coffee");
			googleDTO.setRestaurantId("23");
			googleDTO.setBotName("SITARA");
			Context context = null ;
			googleService.serveLex(googleDTO, context);
			}

}
