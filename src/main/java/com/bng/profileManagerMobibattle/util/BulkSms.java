package com.bng.profileManagerMobibattle.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BulkSms {
	
	/*private static String apiTokenId;
	private static String apiTokenSecret;
	
	
	
	/*static {
		apiTokenId=Config.smsUserName;
		apiTokenSecret=Config.smsPassword;
		url=Config.smsUrl;
		
	}*/
	
	private final static Logger logger = LoggerFactory.getLogger(BulkSms.class);
	
	
/*
	private static String createJSON(String to,String message) {
		
		return "{\"to\":\""+to+"\",\"body\":\""+message+"\",\"encoding\":\"UNICODE\"}";
		
		
	}
*/	
	
	
	
	public static String sendSMS(String url,String to, String message) {
		
		try {
			URL obj;
			if(url!=null)
			  obj = new URL(url);
			else {
				logger.warn("Bulk SMS - url is nUll");
				return null;
			}
			
			/* TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
	             public java.security.cert.X509Certificate[] getAcceptedIssuers() {
	                 return null;
	             }
	             public void checkClientTrusted(X509Certificate[] certs, String authType) {
	             }
	             public void checkServerTrusted(X509Certificate[] certs, String authType) {
	             }
	         }
	     };*/

	     // Install the all-trusting trust manager
	    /* SSLContext sc = SSLContext.getInstance("SSL");
	     sc.init(null, trustAllCerts, new java.security.SecureRandom());
	     HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	    */ 
	     // Create all-trusting host name verifier
	    /* HostnameVerifier allHostsValid = new HostnameVerifier() {
	         public boolean verify(String hostname, SSLSession session) {
	             return true;
	         }
	     };*/

	     // Install the all-trusting host verifier
	    /* HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		 HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		 String userPassword = apiTokenId + ":" + apiTokenSecret;
		 String encoding = Base64.getEncoder().encodeToString(userPassword.getBytes());
		 con.setRequestProperty("Authorization", "Basic " + encoding);
		 String reqst=createJSON(to,message);
			*/
			
			//byte[] authEncBytes = Base64.getEncoder().encode(authString.getBytes());
			//String authStringEnc = new String(authEncBytes);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			 
			//con.setRequestProperty("Authorization", "Basic " + authStringEnc);
			con.setDoOutput(true);
			
			
			int responseCode = con.getResponseCode();
			logger.info("get Response Code :: " + responseCode);
			if (responseCode == HttpURLConnection.HTTP_OK || responseCode==HttpURLConnection.HTTP_CREATED) { //success
				BufferedReader in = new BufferedReader(new InputStreamReader(
						con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				
				
				
				String xmlFile = response.toString();
				
				// print result
				logger.info(xmlFile);
				return xmlFile;
				
			}
				
			else {
				return ""+responseCode;
			}
			
			
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ""+404;
		}
		
		
		
		
		
		
		
	}
	
	
	

}
