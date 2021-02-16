package com.bng.profileManagerMobibattle.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.bng.profileManagerMobibattle.pojo.GeneralResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Utility {

	public static final Gson gson = new GsonBuilder().serializeNulls().create();
	public static  SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
	
	public static String hitPost(String url, String body,Map<String, String> headers) {
		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			if(headers!=null && !headers.isEmpty()) {
				for(Map.Entry<String, String> entry: headers.entrySet()) {
					con.setRequestProperty(entry.getKey(), entry.getValue());
				}
			}
			con.setRequestProperty("Content-Type", "application/json");
			con.setDoOutput(true);
			OutputStream os = con.getOutputStream();
			os.write(body.getBytes());
			os.flush();
			os.close();
			int responseCode = con.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) { // success
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				return new String(response.toString().getBytes(StandardCharsets.UTF_8),StandardCharsets.UTF_8);
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String uploadImage(String url, String password, MultipartFile file,Map<String, String> headers) {
		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			String boundary = Long.toHexString(System.currentTimeMillis());
			if(headers!=null && !headers.isEmpty()) {
				for(Map.Entry<String, String> entry: headers.entrySet()) {
					con.setRequestProperty(entry.getKey(), entry.getValue());
				}
			}
			con.setRequestProperty("Content-Type", "multipart/form-data;charset=UTF-8; boundary=" + boundary);
			con.setRequestProperty("password", password);
			con.setDoOutput(true);
			OutputStream os = con.getOutputStream();
			byte[] boundaryBytes = ("--" + boundary + "\r\n").getBytes(StandardCharsets.UTF_8);
			byte[] finishBoundaryBytes = ("--" + boundary + "--").getBytes(StandardCharsets.UTF_8);
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, "UTF-8"), true);
			os.write(boundaryBytes);
			writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"")
					.append("\r\n");
			writer.append("\r\n").flush();
			os.write(file.getBytes());
			writer.append("\r\n").flush();
			os.write(finishBoundaryBytes);
			os.flush();
			os.close();
			int responseCode = con.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) { // success
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				return new String(response.toString().getBytes(StandardCharsets.UTF_8),StandardCharsets.UTF_8);
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String hitGet(String url,Map<String, String> headers) {
		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			if(headers!=null && !headers.isEmpty()) {
				for(Map.Entry<String, String> entry: headers.entrySet()) {
					con.setRequestProperty(entry.getKey(), entry.getValue());
				}
			}
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
			int responseCode = con.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				System.out.println(response.toString());
				return new String(response.toString().getBytes(StandardCharsets.UTF_8),StandardCharsets.UTF_8);
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ResponseEntity<byte[]> getImage(String url, String uploadId,Map<String, String> headers) {
		try {
			URL obj = new URL(url);
			//InputStreamResource in = new InputStreamResource(obj.openStream());
			URLConnection conn = obj.openConnection();
			if(headers!=null && !headers.isEmpty()) {
				for(Map.Entry<String, String> entry: headers.entrySet()) {
					conn.setRequestProperty(entry.getKey(), entry.getValue());
				}
			}
			String contentType = "";
			if (uploadId.trim().startsWith("i") || uploadId.trim().startsWith("t"))
				contentType = "image/jpeg";
			return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, contentType)
					.body(IOUtils.toByteArray(conn.getInputStream()));

		} catch (Exception e) {
			e.printStackTrace();
			GeneralResponse response = new GeneralResponse();
			response.setStatus("FAILURE");
			response.setReason(ResponseEnums.FILEGETCOULDNOTSTREAMFAIL.toString(headers!=null?headers.get("defaultlanguage"):""));
			response.setIsLogout(false);
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
					.body(Utility.gson.toJson(response).getBytes(StandardCharsets.UTF_8));
		}
	}

	public static ResponseEntity<byte[]> getVideo(String url, String uploadId, String range, String type,Map<String, String> headers) {
		try {
			URL obj = new URL(url);
			URLConnection conn = obj.openConnection();
			if(headers!=null && !headers.isEmpty()) {
				for(Map.Entry<String, String> entry: headers.entrySet()) {
					conn.setRequestProperty(entry.getKey(), entry.getValue());
				}
			}
			if (range != null && !range.trim().isEmpty())
				conn.setRequestProperty("Range", range);
			
			if(type==null || type.trim().isEmpty() || type.trim().equalsIgnoreCase("stream")) {
				return ResponseEntity
						.status(HttpStatus.PARTIAL_CONTENT)
						.header(HttpHeaders.CONTENT_TYPE, conn.getHeaderField(HttpHeaders.CONTENT_TYPE))
						.header(HttpHeaders.CONTENT_RANGE, conn.getHeaderField(HttpHeaders.CONTENT_RANGE))
						.header(HttpHeaders.ACCEPT_RANGES, conn.getHeaderField(HttpHeaders.ACCEPT_RANGES))
						.header(HttpHeaders.CONTENT_LENGTH, conn.getHeaderField(HttpHeaders.CONTENT_LENGTH))
						.body(IOUtils.toByteArray(conn.getInputStream()));
			}
			
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_TYPE, conn.getHeaderField(HttpHeaders.CONTENT_TYPE))
					.body(IOUtils.toByteArray(conn.getInputStream()));
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			GeneralResponse response = new GeneralResponse();
			response.setStatus("FAILURE");
			response.setReason(ResponseEnums.VIDEOCOULDNOTSTREAMFAIL.toString(headers!=null?headers.get("defaultlanguage"):""));
			response.setIsLogout(false);
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
					.body(Utility.gson.toJson(response).getBytes(StandardCharsets.UTF_8));
		}
	}

	private static Boolean isValidRange(String range) {
		if (range == null || range.trim().isEmpty())
			return false;

		if (range.startsWith("bytes=")) {
			String ranges[] = range.split("=");
			if (ranges != null && ranges.length == 2) {
				String lowerAndUpper[] = ranges[1].split("-");
				if (lowerAndUpper != null && lowerAndUpper.length == 2) {
					try {
						Long.parseLong(lowerAndUpper[0]);
						Long.parseLong(lowerAndUpper[1]);
						return true;
					} catch (Exception e) {
						System.out.println("Range not of right format:" + e.getMessage());
					}
				}
			}
		}
		return false;
	}

	public static String hitDelete(String url,Map<String, String> headers) {
		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("DELETE");
			if(headers!=null && !headers.isEmpty()) {
				for(Map.Entry<String, String> entry: headers.entrySet()) {
					con.setRequestProperty(entry.getKey(), entry.getValue());
				}
			}
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
			int responseCode = con.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				return new String(response.toString().getBytes(StandardCharsets.UTF_8),StandardCharsets.UTF_8);
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getRandomNumber() {
		StringBuffer number = new StringBuffer();
		Random rand = new Random();

		for (int i = 0; i < 10; i++) {
			number.append(Integer.toString(rand.nextInt(10)));
		}
		return number.toString();
	}

}
