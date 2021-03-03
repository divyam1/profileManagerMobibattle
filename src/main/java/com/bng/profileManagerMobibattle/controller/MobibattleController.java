package com.bng.profileManagerMobibattle.controller;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.auth0.jwt.JWT;
import com.bng.profileManagerMobibattle.pojo.CreateResourceRequest;
import com.bng.profileManagerMobibattle.pojo.DeleteAccountExternalRequest;
import com.bng.profileManagerMobibattle.pojo.GeneralResponse;
import com.bng.profileManagerMobibattle.pojo.User;
import com.bng.profileManagerMobibattle.service.FynderService;
import com.bng.profileManagerMobibattle.util.CoreEnums;
import com.bng.profileManagerMobibattle.util.ExternalServiceURIConstants;
import com.bng.profileManagerMobibattle.util.ResponseEnums;
import com.bng.profileManagerMobibattle.util.SecurityConstants;
import com.bng.profileManagerMobibattle.util.URIConstants;
import com.bng.profileManagerMobibattle.util.Utility;
import com.google.gson.JsonObject;

@RestController
public class MobibattleController {

	@Value("${fynder.baseUrl}")
	private String baseUrl;

	@Autowired
	private FynderService fynderService;

	private final static Logger logger = LoggerFactory.getLogger(MobibattleController.class);

	@RequestMapping(value = URIConstants.CONFIGURATIONURI, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String getConfiguration(@RequestBody String request, @RequestHeader Map<String, String> headers) {
		System.out.println(request);
		String response = Utility.hitPost(baseUrl + ExternalServiceURIConstants.CONFIGURATIONURI, request, headers);
		logger.info("Configuration request:" + request + ", response:" + response);
		return response;
	}

	@RequestMapping(value = URIConstants.CHECKHEURI, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String getNumber(@RequestBody String request, @RequestHeader Map<String, String> headers) {
		String response = Utility.gson.toJson(fynderService.saveHE(request, headers));
		logger.info("check he request:" + request + ", response:" + response);
		return response;
	}

	@RequestMapping(value = URIConstants.SENDOTP, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String sendOTP(@RequestBody String request, @RequestHeader Map<String, String> headers) {
		String response = Utility.gson.toJson(fynderService.sendOTP(request, headers));
		logger.info("send otp request:" + request + ", response:" + response);
		return response;
	}

	@RequestMapping(value = URIConstants.LOGIN, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String register(@RequestBody String request, @RequestHeader Map<String, String> headers) {
		String response = fynderService.register(request, headers);
		logger.info("register external response:" + response);
		JsonObject jsonObject = Utility.gson.fromJson(response, JsonObject.class);
		if (jsonObject != null && jsonObject.get("status") != null
				&& jsonObject.get("status").getAsString().equalsIgnoreCase("success")) {
			String token = JWT.create().withSubject(jsonObject.get("uniqueId").getAsString())
					.sign(HMAC512(SecurityConstants.SECRET.getBytes()));
			jsonObject.addProperty("token", token);
		}
		logger.info("register request:" + request + ", response:" + jsonObject);
		return jsonObject.toString();
	}

	@RequestMapping(value = URIConstants.CREATEPROFILE, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String createProfile(@RequestBody String request, @RequestHeader Map<String, String> headers) {
		if (request != null && !request.isEmpty()) {
			User user = Utility.gson.fromJson(request, User.class);
			logger.info("user:" + user.getDeviceId() + ", unique:" + user.getUniqueId()+" true:: "+user.getRequestSource().equals(CoreEnums.RequestSourceWeb.toString()));
			if (fynderService.validateUser(user) && (user.getRequestSource() != null
					&& (user.getRequestSource().equals(CoreEnums.RequestSource.toString())||user.getRequestSource().equals(CoreEnums.RequestSourceWeb.toString())))) {
				String response = Utility.hitPost(baseUrl + ExternalServiceURIConstants.CREATEPROFILEURI, request,
						headers);
				logger.info("create profile request:" + request + ", response:" + response);
				return response;
			}
			GeneralResponse response = new GeneralResponse();
			response.setIsLogout(true);
			response.setStatus(CoreEnums.ResponseFailure.toString());
			response.setReason(ResponseEnums.InvalidRequestParameters
					.toString(headers != null ? headers.get("defaultlanguage") : ""));
			logger.info("create profile request:" + request + ", response:" + Utility.gson.toJson(response));
			return Utility.gson.toJson(response);
		}
		GeneralResponse response = new GeneralResponse();
		response.setStatus(CoreEnums.ResponseFailure.toString());
		response.setReason(
				ResponseEnums.InvalidRequestJson.toString(headers != null ? headers.get("defaultlanguage") : ""));
		logger.info("create profile request:" + request + ", response:" + Utility.gson.toJson(response));
		return Utility.gson.toJson(response);
	}

	@RequestMapping(value = URIConstants.INTEREST, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String createInterest(@RequestBody String request, @RequestHeader Map<String, String> headers) {
		if (request != null && !request.isEmpty()) {
			User user = Utility.gson.fromJson(request, User.class);
			if (fynderService.validateUser(user) && (user.getRequestSource() != null
					&& user.getRequestSource().equals(CoreEnums.RequestSource.toString()))) {
				String response = Utility.hitPost(baseUrl + ExternalServiceURIConstants.INTERESTURI, request, headers);
				logger.info("interest request:" + request + ", response:" + response);
				return response;
			}
			GeneralResponse response = new GeneralResponse();
			response.setIsLogout(true);
			response.setStatus(CoreEnums.ResponseFailure.toString());
			response.setReason(ResponseEnums.InvalidRequestParameters
					.toString(headers != null ? headers.get("defaultlanguage") : ""));
			logger.info("interest request:" + request + ", response:" + Utility.gson.toJson(response));
			return Utility.gson.toJson(response);
		}
		GeneralResponse response = new GeneralResponse();
		response.setStatus(CoreEnums.ResponseFailure.toString());
		response.setReason(
				ResponseEnums.InvalidRequestJson.toString(headers != null ? headers.get("defaultlanguage") : ""));
		logger.info("interest request:" + request + ", response:" + Utility.gson.toJson(response));
		return Utility.gson.toJson(response);

	}

	@RequestMapping(value = URIConstants.BROWSE, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String browse(@RequestBody String request, @RequestHeader Map<String, String> headers) {
		if (request != null && !request.isEmpty()) {
			User user = Utility.gson.fromJson(request, User.class);
			if (fynderService.validateUser(user) && (user.getRequestSource() != null
					&& user.getRequestSource().equals(CoreEnums.RequestSource.toString()))) {
				String response = Utility.hitPost(baseUrl + ExternalServiceURIConstants.BROWSE, request, headers);
				logger.info("browse request:" + request + ", response:" + response);
				return response;
			}
			GeneralResponse response = new GeneralResponse();
			response.setIsLogout(true);
			response.setStatus(CoreEnums.ResponseFailure.toString());
			response.setReason(ResponseEnums.InvalidRequestParameters
					.toString(headers != null ? headers.get("defaultlanguage") : ""));
			logger.info("browse request:" + request + ", response:" + Utility.gson.toJson(response));
			return Utility.gson.toJson(response);
		}
		GeneralResponse response = new GeneralResponse();
		response.setStatus(CoreEnums.ResponseFailure.toString());
		response.setReason(
				ResponseEnums.InvalidRequestJson.toString(headers != null ? headers.get("defaultlanguage") : ""));
		logger.info("browse request:" + request + ", response:" + Utility.gson.toJson(response));
		return Utility.gson.toJson(response);

	}

	@RequestMapping(value = URIConstants.BOOKMARK, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String bookmark(@RequestBody String request, @RequestHeader Map<String, String> headers) {
		if (request != null && !request.isEmpty()) {
			User user = Utility.gson.fromJson(request, User.class);
			if (fynderService.validateUser(user) && (user.getRequestSource() != null
					&& user.getRequestSource().equals(CoreEnums.RequestSource.toString()))) {
				String response = Utility.hitPost(baseUrl + ExternalServiceURIConstants.BOOKMARK, request, headers);
				logger.info("bookmark request:" + request + ", response:" + response);
				return response;
			}
			GeneralResponse response = new GeneralResponse();
			response.setIsLogout(true);
			response.setStatus(CoreEnums.ResponseFailure.toString());
			response.setReason(ResponseEnums.InvalidRequestParameters
					.toString(headers != null ? headers.get("defaultlanguage") : ""));
			logger.info("bookmark request:" + request + ", response:" + Utility.gson.toJson(response));
			return Utility.gson.toJson(response);
		}
		GeneralResponse response = new GeneralResponse();
		response.setStatus(CoreEnums.ResponseFailure.toString());
		response.setReason(
				ResponseEnums.InvalidRequestJson.toString(headers != null ? headers.get("defaultlanguage") : ""));
		logger.info("bookmark request:" + request + ", response:" + Utility.gson.toJson(response));
		return Utility.gson.toJson(response);

	}

	@RequestMapping(value = URIConstants.BLACKLIST, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String blackList(@RequestBody String request, @RequestHeader Map<String, String> headers) {
		if (request != null && !request.isEmpty()) {
			User user = Utility.gson.fromJson(request, User.class);
			if (fynderService.validateUser(user) && (user.getRequestSource() != null
					&& user.getRequestSource().equals(CoreEnums.RequestSource.toString()))) {
				String response = Utility.hitPost(baseUrl + ExternalServiceURIConstants.BLACKLIST, request, headers);
				logger.info("blacklist request:" + request + ", response:" + response);
				return response;
			}
			GeneralResponse response = new GeneralResponse();
			response.setIsLogout(true);
			response.setStatus(CoreEnums.ResponseFailure.toString());
			response.setReason(ResponseEnums.InvalidRequestParameters
					.toString(headers != null ? headers.get("defaultlanguage") : ""));
			logger.info("blacklist request:" + request + ", response:" + Utility.gson.toJson(response));
			return Utility.gson.toJson(response);
		}
		GeneralResponse response = new GeneralResponse();
		response.setStatus(CoreEnums.ResponseFailure.toString());
		response.setReason(
				ResponseEnums.InvalidRequestJson.toString(headers != null ? headers.get("defaultlanguage") : ""));
		logger.info("blacklist request:" + request + ", response:" + Utility.gson.toJson(response));
		return Utility.gson.toJson(response);

	}

	@RequestMapping(value = URIConstants.CREATERESOURCE, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String createResource(@RequestBody String request, @RequestHeader Map<String, String> headers) {
		if (request != null && !request.isEmpty()) {
			User user = Utility.gson.fromJson(request, User.class);
			if (fynderService.validateUser(user) && (user.getRequestSource() != null
					&& user.getRequestSource().equals(CoreEnums.RequestSource.toString()))) {
				CreateResourceRequest createResource = Utility.gson.fromJson(request, CreateResourceRequest.class);
				String response = Utility.hitPost(
						baseUrl + ExternalServiceURIConstants.CREATERESOURCE + "/" + user.getUniqueId(),
						Utility.gson.toJson(createResource), headers);
				logger.info("create resource request:" + request + ", response:" + response);
				return response;
			}
			GeneralResponse response = new GeneralResponse();
			response.setIsLogout(true);
			response.setStatus(CoreEnums.ResponseFailure.toString());
			response.setReason(ResponseEnums.InvalidRequestParameters
					.toString(headers != null ? headers.get("defaultlanguage") : ""));
			logger.info("create resource request:" + request + ", response:" + Utility.gson.toJson(response));
			return Utility.gson.toJson(response);
		}
		GeneralResponse response = new GeneralResponse();
		response.setStatus(CoreEnums.ResponseFailure.toString());
		response.setReason(
				ResponseEnums.InvalidRequestJson.toString(headers != null ? headers.get("defaultlanguage") : ""));
		logger.info("create resource request:" + request + ", response:" + Utility.gson.toJson(response));
		return Utility.gson.toJson(response);

	}

	@RequestMapping(value = URIConstants.UPLOADIMAGE, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String uploadImage(@PathVariable(value = "userId") String userId,
			@PathVariable(value = "uploadId") String uploadId, @RequestParam("file") MultipartFile file,
			@RequestParam("deviceId") String deviceId, @RequestParam("password") String password,
			@RequestHeader Map<String, String> headers) {
		User user = new User();
		user.setDeviceId(deviceId);
		user.setUniqueId(userId);
		if (fynderService.validateUser(user)) {
			logger.info("file size=" + file.getSize());
			String response = Utility.uploadImage(
					baseUrl + ExternalServiceURIConstants.CREATERESOURCE + "/" + userId + "/" + uploadId, password,
					file, headers);
			logger.info("upload resource request:deviceId-" + deviceId + ",userId-" + userId + ",password-" + password
					+ ", response:" + response);
			return response;
		}
		GeneralResponse response = new GeneralResponse();
		response.setIsLogout(true);
		response.setStatus(CoreEnums.ResponseFailure.toString());
		response.setReason(
				ResponseEnums.InvalidRequestParameters.toString(headers != null ? headers.get("defaultlanguage") : ""));
		logger.info("upload resource request:deviceId-" + deviceId + ",userId-" + userId + ",password-" + password
				+ ", response:" + Utility.gson.toJson(response));
		return Utility.gson.toJson(response);

	}

	@RequestMapping(value = URIConstants.UPLOADIMAGE, method = RequestMethod.DELETE)
	public String deleteImage(@PathVariable(value = "userId") String userId,
			@PathVariable(value = "uploadId") String uploadId, @RequestBody String request,
			@RequestHeader Map<String, String> headers) {
		if (request != null && !request.isEmpty()) {
			User user = Utility.gson.fromJson(request, User.class);
			user.setUniqueId(userId);
			if (fynderService.validateUser(user)) {
				String response = Utility.hitDelete(
						baseUrl + ExternalServiceURIConstants.CREATERESOURCE + "/" + userId + "/" + uploadId, headers);
				logger.info("delete resource request:" + userId + ":" + uploadId + ":" + user.getDeviceId() + ":"
						+ ", response:" + response);
				return response;
			}
			GeneralResponse response = new GeneralResponse();
			response.setIsLogout(true);
			response.setStatus(CoreEnums.ResponseFailure.toString());
			response.setReason(ResponseEnums.InvalidRequestParameters
					.toString(headers != null ? headers.get("defaultlanguage") : ""));
			logger.info("delete resource request:" + userId + ":" + uploadId + ":" + user.getDeviceId() + ":"
					+ ", response:" + Utility.gson.toJson(response));
			return Utility.gson.toJson(response);
		}
		GeneralResponse response = new GeneralResponse();
		response.setStatus(CoreEnums.ResponseFailure.toString());
		response.setReason(
				ResponseEnums.InvalidRequestJson.toString(headers != null ? headers.get("defaultlanguage") : ""));
		logger.info("delete resource request:" + userId + ":" + uploadId + ":empty deviceId:" + ", response:"
				+ Utility.gson.toJson(response));
		return Utility.gson.toJson(response);

	}

	@RequestMapping(value = URIConstants.UPLOADIMAGE, method = RequestMethod.GET)
	public ResponseEntity<byte[]> getImage(@PathVariable(value = "userId") String userId,
			@PathVariable(value = "uploadId") String uploadId, @RequestParam("deviceId") String deviceId,
			@RequestParam("userId") String thisUser, @RequestHeader(value = "Range", required = false) String range,
			@RequestParam(value = "type", required = false) String type, @RequestHeader Map<String, String> headers) {
		if (deviceId != null && !deviceId.isEmpty()) {
			User user = new User();
			user.setDeviceId(deviceId);
			user.setUniqueId(thisUser);
			if (fynderService.validateUser(user) && uploadId != null) {
				if (uploadId.startsWith("i") || uploadId.startsWith("t")) {
					ResponseEntity<byte[]> response = Utility.getImage(
							baseUrl + ExternalServiceURIConstants.CREATERESOURCE + "/" + userId + "/" + uploadId,
							uploadId, headers);
					logger.info("uploadId=" + uploadId + "response=" + response);
					return response;
				} else if (uploadId.startsWith("v")) {
					logger.info("range header:" + range);
					ResponseEntity<byte[]> response = Utility
							.getVideo(baseUrl + ExternalServiceURIConstants.CREATERESOURCE + "/video/" + userId + "/"
									+ uploadId + "?type=" + type, uploadId, range, type, headers);
					logger.info("uploadId=" + uploadId + "response=" + response);
					return response;
				}
			}
			GeneralResponse response = new GeneralResponse();
			response.setIsLogout(true);
			response.setStatus(CoreEnums.ResponseFailure.toString());
			response.setReason(ResponseEnums.InvalidRequestParameters
					.toString(headers != null ? headers.get("defaultlanguage") : ""));
			logger.info("get resource request:thisUser-" + thisUser + "," + userId + ":" + uploadId + ":" + deviceId
					+ ":" + ", response:" + Utility.gson.toJson(response));
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
					.body(Utility.gson.toJson(response).getBytes());
		}
		GeneralResponse response = new GeneralResponse();
		response.setIsLogout(true);
		response.setStatus(CoreEnums.ResponseFailure.toString());
		response.setReason(
				ResponseEnums.InvalidRequestParameters.toString(headers != null ? headers.get("defaultlanguage") : ""));
		logger.info("get resource request:thisUser-" + thisUser + "," + userId + ":" + uploadId + ":" + deviceId + ":"
				+ ", response:" + Utility.gson.toJson(response));
		return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
				.body(Utility.gson.toJson(response).getBytes());
	}

	@RequestMapping(value = URIConstants.SETTING, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String updateSetting(@RequestBody String request, @RequestHeader Map<String, String> headers) {
		if (request != null && !request.isEmpty()) {
			User user = Utility.gson.fromJson(request, User.class);
			if (fynderService.validateUser(user) && (user.getRequestSource() != null
					&& user.getRequestSource().equals(CoreEnums.RequestSource.toString()))) {
				String response = Utility.hitPost(baseUrl + ExternalServiceURIConstants.SETTING, request, headers);
				logger.info("setting request:" + request + ",response:" + response);
				return response;
			}
		}
		GeneralResponse response = new GeneralResponse();
		response.setIsLogout(true);
		response.setStatus(CoreEnums.ResponseFailure.toString());
		response.setReason(
				ResponseEnums.InvalidRequestParameters.toString(headers != null ? headers.get("defaultlanguage") : ""));
		logger.info("setting request:" + request + ",response:" + Utility.gson.toJson(response));
		return Utility.gson.toJson(response);
	}

	@RequestMapping(value = URIConstants.DEVICETOKEN, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String updateDeviceToken(@RequestBody String request, @RequestHeader Map<String, String> headers) {
		if (request != null && !request.isEmpty()) {
			User user = Utility.gson.fromJson(request, User.class);
			if (fynderService.validateUser(user) && (user.getRequestSource() != null
					&& user.getRequestSource().equals(CoreEnums.RequestSource.toString()))) {
				String response = Utility.hitPost(baseUrl + ExternalServiceURIConstants.DEVICETOKEN, request, headers);
				logger.info("device token request:" + request + ",response:" + response);
				return response;
			}
		}
		GeneralResponse response = new GeneralResponse();
		response.setIsLogout(true);
		response.setStatus(CoreEnums.ResponseFailure.toString());
		response.setReason(
				ResponseEnums.InvalidRequestParameters.toString(headers != null ? headers.get("defaultlanguage") : ""));
		logger.info("device token request:" + request + ",response:" + Utility.gson.toJson(response));
		return Utility.gson.toJson(response);
	}

	@RequestMapping(value = URIConstants.GETPROFILE, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String getProfile(@RequestBody String request, @RequestHeader Map<String, String> headers) {
		if (request != null && !request.isEmpty()) {
			User user = Utility.gson.fromJson(request, User.class);
			if (fynderService.validateUser(user) && (user.getRequestSource() != null
					&& user.getRequestSource().equals(CoreEnums.RequestSource.toString()))) {
				String response = Utility.hitPost(baseUrl + ExternalServiceURIConstants.GETPROFILE, request, headers);
				logger.info("fetch profile request:" + request + ",response:" + response);
				return response;
			}
		}
		GeneralResponse response = new GeneralResponse();
		response.setIsLogout(true);
		response.setStatus(CoreEnums.ResponseFailure.toString());
		response.setReason(
				ResponseEnums.InvalidRequestParameters.toString(headers != null ? headers.get("defaultlanguage") : ""));
		logger.info("fetch profile request:" + request + ",response:" + Utility.gson.toJson(response));
		return Utility.gson.toJson(response);
	}

	@RequestMapping(value = URIConstants.REPORT, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String reportProfile(@RequestBody String request, @RequestHeader Map<String, String> headers) {
		if (request != null && !request.isEmpty()) {
			User user = Utility.gson.fromJson(request, User.class);
			if (fynderService.validateUser(user) && (user.getRequestSource() != null
					&& user.getRequestSource().equals(CoreEnums.RequestSource.toString()))) {
				String response = Utility.hitPost(baseUrl + ExternalServiceURIConstants.REPORT, request, headers);
				logger.info("report profile request:" + request + ",response:" + response);
				return response;
			}
		}
		GeneralResponse response = new GeneralResponse();
		response.setIsLogout(true);
		response.setStatus(CoreEnums.ResponseFailure.toString());
		response.setReason(
				ResponseEnums.InvalidRequestParameters.toString(headers != null ? headers.get("defaultlanguage") : ""));
		logger.info("report profile request:" + request + ",response:" + Utility.gson.toJson(response));
		return Utility.gson.toJson(response);
	}

	@RequestMapping(value = URIConstants.DELETEACCOUNT, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String deleteAccount(@RequestBody String request, @RequestHeader Map<String, String> headers) {
		if (request != null && !request.isEmpty()) {
			User user = Utility.gson.fromJson(request, User.class);
			if (fynderService.validateUser(user) && (user.getRequestSource() != null
					&& user.getRequestSource().equals(CoreEnums.RequestSource.toString()))) {
				DeleteAccountExternalRequest requestObj = Utility.gson.fromJson(request,
						DeleteAccountExternalRequest.class);
				requestObj.setIsMute(true);
				requestObj.setIsVisible(false);
				String response = Utility.hitPost(baseUrl + ExternalServiceURIConstants.SETTING,
						Utility.gson.toJson(requestObj), headers);
				logger.info(
						"delete account setting request:" + Utility.gson.toJson(requestObj) + ",response:" + response);
				return response;
			}
		}
		GeneralResponse response = new GeneralResponse();
		response.setIsLogout(true);
		response.setStatus(CoreEnums.ResponseFailure.toString());
		response.setReason(
				ResponseEnums.InvalidRequestParameters.toString(headers != null ? headers.get("defaultlanguage") : ""));
		logger.info("delete account setting request:" + request + ",response:" + Utility.gson.toJson(response));
		return Utility.gson.toJson(response);
	}

	@RequestMapping(value = URIConstants.CONNECT, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String connection(@RequestBody String request, @RequestHeader Map<String, String> headers) {
		if (request != null && !request.isEmpty()) {
			User user = Utility.gson.fromJson(request, User.class);
			if (fynderService.validateUser(user) && (user.getRequestSource() != null
					&& user.getRequestSource().equals(CoreEnums.RequestSource.toString()))) {
				String response = Utility.hitPost(baseUrl + ExternalServiceURIConstants.CONNECT, request, headers);
				logger.info("connection request:" + request + ",response:" + response);
				return response;
			}
		}
		GeneralResponse response = new GeneralResponse();
		response.setIsLogout(true);
		response.setStatus(CoreEnums.ResponseFailure.toString());
		response.setReason(
				ResponseEnums.InvalidRequestParameters.toString(headers != null ? headers.get("defaultlanguage") : ""));
		logger.info("connection request:" + request + ",response:" + response);
		return Utility.gson.toJson(response);
	}

	@RequestMapping(value = URIConstants.REPORTREASONS, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String getReportReasons(@RequestBody String request, @RequestHeader Map<String, String> headers) {
		if (request != null && !request.isEmpty()) {
			User user = Utility.gson.fromJson(request, User.class);
			if (fynderService.validateUser(user) && (user.getRequestSource() != null
					&& user.getRequestSource().equals(CoreEnums.RequestSource.toString()))) {
				String response = Utility.hitPost(baseUrl + ExternalServiceURIConstants.REPORTREASONS, request,
						headers);
				logger.info("get report reasons request:" + request + ",response:" + response);
				return response;
			}
		}
		GeneralResponse response = new GeneralResponse();
		response.setIsLogout(true);
		response.setStatus(CoreEnums.ResponseFailure.toString());
		response.setReason(
				ResponseEnums.InvalidRequestParameters.toString(headers != null ? headers.get("defaultlanguage") : ""));
		logger.info("get report reasons request:" + request + ",response:" + response);
		return Utility.gson.toJson(response);
	}

	@RequestMapping(value = URIConstants.ATTACHNUMBER, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String attachNumber(@RequestBody String request, @RequestHeader Map<String, String> headers) {
		if (request != null && !request.isEmpty()) {
			User user = Utility.gson.fromJson(request, User.class);
			if (fynderService.validateUser(user) && (user.getRequestSource() != null
					&& user.getRequestSource().equals(CoreEnums.RequestSource.toString()))) {
				String response = Utility.hitPost(baseUrl + ExternalServiceURIConstants.ATTACHNUMBER, request, headers);
				logger.info("attach number request:" + request + ",response:" + response);
				return response;
			}
		}
		GeneralResponse response = new GeneralResponse();
		response.setIsLogout(true);
		response.setStatus(CoreEnums.ResponseFailure.toString());
		response.setReason(
				ResponseEnums.InvalidRequestParameters.toString(headers != null ? headers.get("defaultlanguage") : ""));
		logger.info("attach number request:" + request + ",response:" + response);
		return Utility.gson.toJson(response);
	}

	@RequestMapping(value = URIConstants.REFFERAL, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String saveRefferal(@RequestBody String request, @RequestHeader Map<String, String> headers) {
		if (request != null && !request.isEmpty()) {
			User user = Utility.gson.fromJson(request, User.class);
			if (fynderService.validateUser(user) && (user.getRequestSource() != null
					&& user.getRequestSource().equals(CoreEnums.RequestSource.toString()))) {
				String response = Utility.hitPost(baseUrl + ExternalServiceURIConstants.REFFERAL, request, headers);
				logger.info("refferal request:" + request + ",response:" + response);
				return response;
			}
		}
		GeneralResponse response = new GeneralResponse();
		response.setIsLogout(true);
		response.setStatus(CoreEnums.ResponseFailure.toString());
		response.setReason(
				ResponseEnums.InvalidRequestParameters.toString(headers != null ? headers.get("defaultlanguage") : ""));
		logger.info("refferal request:" + request + ",response:" + response);
		return Utility.gson.toJson(response);
	}

	@RequestMapping(value = URIConstants.REFFERALINK, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String getRefferalLink(@RequestBody String request, @RequestHeader Map<String, String> headers) {
		if (request != null && !request.isEmpty()) {
			User user = Utility.gson.fromJson(request, User.class);
			if (fynderService.validateUser(user) && (user.getRequestSource() != null
					&& user.getRequestSource().equals(CoreEnums.RequestSource.toString()))) {
				String response = Utility.hitPost(baseUrl + ExternalServiceURIConstants.REFFERALLINK, request, headers);
				logger.info("refferal link request:" + request + ",response:" + response);
				return response;
			}
		}
		GeneralResponse response = new GeneralResponse();
		response.setIsLogout(true);
		response.setStatus(CoreEnums.ResponseFailure.toString());
		response.setReason(
				ResponseEnums.InvalidRequestParameters.toString(headers != null ? headers.get("defaultlanguage") : ""));
		logger.info("refferal link request:" + request + ",response:" + response);
		return Utility.gson.toJson(response);
	}

	@RequestMapping(value = URIConstants.EVENTLOG, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String eventLog(@RequestBody String request, @RequestHeader Map<String, String> headers) {
		if (request != null && !request.isEmpty()) {
			User user = Utility.gson.fromJson(request, User.class);
			if (user.getRequestSource() != null && user.getRequestSource().equals(CoreEnums.RequestSource.toString())) {
				String response = Utility.hitPost(baseUrl + ExternalServiceURIConstants.EVENTLOG, request, headers);
				logger.info("event log request:" + request + ",response:" + response);
				return response;
			}
		}
		GeneralResponse response = new GeneralResponse();
		response.setIsLogout(true);
		response.setStatus(CoreEnums.ResponseFailure.toString());
		response.setReason(
				ResponseEnums.InvalidRequestParameters.toString(headers != null ? headers.get("defaultlanguage") : ""));
		logger.info("event log request:" + request + ",response:" + response);
		return Utility.gson.toJson(response);
	}

	@RequestMapping(value = URIConstants.CHECKSUBSCRIPTION, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String checkSubscription(@RequestBody String request, @RequestHeader Map<String, String> headers) {
		if (request != null && !request.isEmpty()) {
			User user = Utility.gson.fromJson(request, User.class);
			if (fynderService.validateUser(user) && (user.getRequestSource() != null
					&& user.getRequestSource().equals(CoreEnums.RequestSource.toString()))) {
				String response = Utility.hitPost(baseUrl + ExternalServiceURIConstants.CHECKSUBSCRIPTION, request,
						headers);
				logger.info("check subscription request:" + request + ",response:" + response);
				return response;
			}
		}
		GeneralResponse response = new GeneralResponse();
		response.setIsLogout(true);
		response.setStatus(CoreEnums.ResponseFailure.toString());
		response.setReason(
				ResponseEnums.InvalidRequestParameters.toString(headers != null ? headers.get("defaultlanguage") : ""));
		logger.info("check subscription request:" + request + ",response:" + response);
		return Utility.gson.toJson(response);
	}

	@RequestMapping(value = URIConstants.SUBSCRIBE, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String subscription(@RequestBody String request, @RequestHeader Map<String, String> headers) {
		if (request != null && !request.isEmpty()) {
			User user = Utility.gson.fromJson(request, User.class);
			if (fynderService.validateUser(user) && (user.getRequestSource() != null
					&& user.getRequestSource().equals(CoreEnums.RequestSource.toString()))) {
				String response = Utility.hitPost(baseUrl + ExternalServiceURIConstants.SUBSCRIBE, request, headers);
				logger.info("subscription request:" + request + ",response:" + response);
				return response;
			}
		}
		GeneralResponse response = new GeneralResponse();
		response.setIsLogout(true);
		response.setStatus(CoreEnums.ResponseFailure.toString());
		response.setReason(
				ResponseEnums.InvalidRequestParameters.toString(headers != null ? headers.get("defaultlanguage") : ""));
		logger.info("subscription request:" + request + ",response:" + response);
		return Utility.gson.toJson(response);
	}

	@RequestMapping(value = URIConstants.UNSUBSCRIBE, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String unsubscription(@RequestBody String request, @RequestHeader Map<String, String> headers) {
		if (request != null && !request.isEmpty()) {
			User user = Utility.gson.fromJson(request, User.class);
			if (fynderService.validateUser(user) && (user.getRequestSource() != null
					&& user.getRequestSource().equals(CoreEnums.RequestSource.toString()))) {
				String response = Utility.hitPost(baseUrl + ExternalServiceURIConstants.UNSUBSCRIBE, request, headers);
				logger.info("unsubscription request:" + request + ",response:" + response);
				return response;
			}
		}
		GeneralResponse response = new GeneralResponse();
		response.setIsLogout(true);
		response.setStatus(CoreEnums.ResponseFailure.toString());
		response.setReason(
				ResponseEnums.InvalidRequestParameters.toString(headers != null ? headers.get("defaultlanguage") : ""));
		logger.info("unsubscription request:" + request + ",response:" + response);
		return Utility.gson.toJson(response);
	}

	@RequestMapping(value = URIConstants.UPLOADLOG, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String uploadLog(@RequestParam("file") MultipartFile file, @RequestParam("filename") String fileName,
			@RequestHeader Map<String, String> headers) {
		if (fileName != null && !fileName.isEmpty() && file != null && file.getSize() > 0) {
			String response = Utility.uploadImage(baseUrl + ExternalServiceURIConstants.LOG, fileName, file, headers);
			logger.info("log upload request:fileName-" + fileName + "file size-" + file.getSize() + ", response:"
					+ response);
			return response;
		}
		GeneralResponse response = new GeneralResponse();
		response.setStatus(CoreEnums.ResponseFailure.toString());
		response.setReason(
				ResponseEnums.InvalidRequestParameters.toString(headers != null ? headers.get("defaultlanguage") : ""));
		logger.info(
				"log upload request:fileName-" + fileName + "file size-" + file.getSize() + ", response:" + response);
		return Utility.gson.toJson(response);
	}

	@RequestMapping(value = URIConstants.PACKS, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String packs(@RequestBody String request, @RequestHeader Map<String, String> headers) {
		if (request != null && !request.isEmpty()) {
			User user = Utility.gson.fromJson(request, User.class);
			if (fynderService.validateUser(user) && (user.getRequestSource() != null
					&& user.getRequestSource().equals(CoreEnums.RequestSource.toString()))) {
				String response = Utility.hitPost(baseUrl + ExternalServiceURIConstants.PACKS, request, headers);
				logger.info("get packs request:" + request + ",response:" + response);
				return response;
			}
		}
		GeneralResponse response = new GeneralResponse();
		response.setIsLogout(true);
		response.setStatus(CoreEnums.ResponseFailure.toString());
		response.setReason(
				ResponseEnums.InvalidRequestParameters.toString(headers != null ? headers.get("defaultlanguage") : ""));
		logger.info("get packs request:" + request + ",response:" + response);
		return Utility.gson.toJson(response);
	}

	@RequestMapping(value = URIConstants.CALLBACK, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String callback(@RequestBody String request, @RequestHeader Map<String, String> headers) {
		if (request != null && !request.isEmpty()) {
			String response = Utility.hitPost(baseUrl + ExternalServiceURIConstants.CALLBACK, request, headers);
			logger.info("callback request:" + request + ",response:" + response);
			return response;
		}
		GeneralResponse response = new GeneralResponse();
		response.setStatus(CoreEnums.ResponseFailure.toString());
		response.setReason(
				ResponseEnums.InvalidRequestParameters.toString(headers != null ? headers.get("defaultlanguage") : ""));
		logger.info("callback request:" + request + ",response:" + response);
		return Utility.gson.toJson(response);
	}

	@RequestMapping(value = URIConstants.ANDROIDAPPCALLBACK, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String androidAppcallback(@RequestBody String request, @RequestHeader Map<String, String> headers) {
		if (request != null && !request.isEmpty()) {
			String response = Utility.hitPost(baseUrl + ExternalServiceURIConstants.ANDROIDAPPCALLBACK, request,
					headers);
			logger.info("android app callback request:" + request + ",response:" + response);
			return response;
		}
		GeneralResponse response = new GeneralResponse();
		response.setStatus(CoreEnums.ResponseFailure.toString());
		response.setReason(
				ResponseEnums.InvalidRequestParameters.toString(headers != null ? headers.get("defaultlanguage") : ""));
		logger.info("android app callback request:" + request + ",response:" + response);
		return Utility.gson.toJson(response);
	}

	@RequestMapping(value = URIConstants.APPLECALLBACK, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String appleCallback(@RequestBody String request, @RequestHeader Map<String, String> headers) {
		if (request != null && !request.isEmpty()) {
			String response = Utility.hitPost(baseUrl + ExternalServiceURIConstants.APPLECALLBACK, request, headers);
			logger.info("callback request:" + request + ",response:" + response);
			return response;
		}
		GeneralResponse response = new GeneralResponse();
		response.setStatus(CoreEnums.ResponseFailure.toString());
		response.setReason(
				ResponseEnums.InvalidRequestParameters.toString(headers != null ? headers.get("defaultlanguage") : ""));
		logger.info("callback request:" + request + ",response:" + response);
		return Utility.gson.toJson(response);
	}

	@RequestMapping(value = URIConstants.APPLEAPPCALLBACK, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String appleAppCallback(@RequestBody String request, @RequestHeader Map<String, String> headers) {
		if (request != null && !request.isEmpty()) {
			String response = Utility.hitPost(baseUrl + ExternalServiceURIConstants.APPLEAPPCALLBACK, request, headers);
			logger.info("callback request:" + request + ",response:" + response);
			return response;
		}
		GeneralResponse response = new GeneralResponse();
		response.setStatus(CoreEnums.ResponseFailure.toString());
		response.setReason(
				ResponseEnums.InvalidRequestParameters.toString(headers != null ? headers.get("defaultlanguage") : ""));
		logger.info("callback request:" + request + ",response:" + response);
		return Utility.gson.toJson(response);
	}

	@RequestMapping(value = URIConstants.VEIRYFSUB, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String verifysubscription(@RequestBody String request, @RequestHeader Map<String, String> headers) {
		if (request != null && !request.isEmpty()) {
			User user = Utility.gson.fromJson(request, User.class);
			if (fynderService.validateUser(user) && (user.getRequestSource() != null
					&& user.getRequestSource().equals(CoreEnums.RequestSource.toString()))) {
				String response = Utility.hitPost(baseUrl + ExternalServiceURIConstants.VEIRYFSUB, request, headers);
				logger.info("subscription request:" + request + ",response:" + response);
				return response;
			}
		}
		GeneralResponse response = new GeneralResponse();
		response.setIsLogout(true);
		response.setStatus(CoreEnums.ResponseFailure.toString());
		response.setReason(
				ResponseEnums.InvalidRequestParameters.toString(headers != null ? headers.get("defaultlanguage") : ""));
		logger.info("subscription request:" + request + ",response:" + response);
		return Utility.gson.toJson(response);
	}

	@RequestMapping(value = URIConstants.COUPONHISTORY, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String referralCouponHistory(@RequestBody String request, @RequestHeader Map<String, String> headers) {
		if (request != null && !request.isEmpty()) {
			User user = Utility.gson.fromJson(request, User.class);
			if (fynderService.validateUser(user) && (user.getRequestSource() != null
					&& user.getRequestSource().equals(CoreEnums.RequestSource.toString()))) {
				String response = Utility.hitPost(ExternalServiceURIConstants.COUPONHISTORY, request, headers);
				logger.info("refferal coupon history request:" + request + ",response:" + response);
				return response;
			}
		}
		GeneralResponse response = new GeneralResponse();
		response.setIsLogout(true);
		response.setStatus(CoreEnums.ResponseFailure.toString());
		response.setReason(
				ResponseEnums.InvalidRequestParameters.toString(headers != null ? headers.get("defaultlanguage") : ""));
		logger.info("refferal coupon history request:" + request + ",response:" + response);
		return Utility.gson.toJson(response);
	}

	@RequestMapping(value = URIConstants.SCRATCHCOUPON, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String scratchCoupon(@RequestBody String request, @RequestHeader Map<String, String> headers) {
		if (request != null && !request.isEmpty()) {
			User user = Utility.gson.fromJson(request, User.class);
			if (fynderService.validateUser(user) && (user.getRequestSource() != null
					&& user.getRequestSource().equals(CoreEnums.RequestSource.toString()))) {
				String response = Utility.hitPost(ExternalServiceURIConstants.SCRATCHCOUPON, request, headers);
				logger.info("scratch coupon request:" + request + ",response:" + response);
				return response;
			}
		}
		GeneralResponse response = new GeneralResponse();
		response.setIsLogout(true);
		response.setStatus(CoreEnums.ResponseFailure.toString());
		response.setReason(
				ResponseEnums.InvalidRequestParameters.toString(headers != null ? headers.get("defaultlanguage") : ""));
		logger.info("scratch coupon request:" + request + ",response:" + response);
		return Utility.gson.toJson(response);
	}

	@RequestMapping(value = URIConstants.COUPONOPTIONS, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String couponOptions(@RequestBody String request, @RequestHeader Map<String, String> headers) {
		if (request != null && !request.isEmpty()) {
			User user = Utility.gson.fromJson(request, User.class);
			if (fynderService.validateUser(user) && (user.getRequestSource() != null
					&& user.getRequestSource().equals(CoreEnums.RequestSource.toString()))) {
				String response = Utility.hitPost(ExternalServiceURIConstants.COUPONOPTIONS, request, headers);
				logger.info("coupon options request:" + request + ",response:" + response);
				return response;
			}
		}
		GeneralResponse response = new GeneralResponse();
		response.setIsLogout(true);
		response.setStatus(CoreEnums.ResponseFailure.toString());
		response.setReason(
				ResponseEnums.InvalidRequestParameters.toString(headers != null ? headers.get("defaultlanguage") : ""));
		logger.info("coupon options request:" + request + ",response:" + response);
		return Utility.gson.toJson(response);
	}

	@RequestMapping(value = URIConstants.REDEEMBALANCE, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String redeemBalance(@RequestBody String request, @RequestHeader Map<String, String> headers) {
		if (request != null && !request.isEmpty()) {
			User user = Utility.gson.fromJson(request, User.class);
			if (fynderService.validateUser(user) && (user.getRequestSource() != null
					&& user.getRequestSource().equals(CoreEnums.RequestSource.toString()))) {
				String response = Utility.hitPost(ExternalServiceURIConstants.REDEEMBALANCE, request, headers);
				logger.info("redeem balance request:" + request + ",response:" + response);
				return response;
			}
		}
		GeneralResponse response = new GeneralResponse();
		response.setIsLogout(true);
		response.setStatus(CoreEnums.ResponseFailure.toString());
		response.setReason(
				ResponseEnums.InvalidRequestParameters.toString(headers != null ? headers.get("defaultlanguage") : ""));
		logger.info("redeem balance request:" + request + ",response:" + response);
		return Utility.gson.toJson(response);
	}

	@RequestMapping(value = URIConstants.REFERRALCOUPONREWARD, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String referralCouponReward(@RequestBody String request, @RequestHeader Map<String, String> headers) {
		if (request != null && !request.isEmpty()) {
			User user = Utility.gson.fromJson(request, User.class);
			if (fynderService.validateUser(user) && (user.getRequestSource() != null
					&& user.getRequestSource().equals(CoreEnums.RequestSource.toString()))) {
				String response = Utility.hitPost(ExternalServiceURIConstants.REFERRALCOUPONREWARD, request, headers);
				logger.info("referral coupon reward request:" + request + ",response:" + response);
				return response;
			}
		}
		GeneralResponse response = new GeneralResponse();
		response.setIsLogout(true);
		response.setStatus(CoreEnums.ResponseFailure.toString());
		response.setReason(
				ResponseEnums.InvalidRequestParameters.toString(headers != null ? headers.get("defaultlanguage") : ""));
		logger.info("referral coupon reward request:" + request + ",response:" + response);
		return Utility.gson.toJson(response);
	}

	@RequestMapping(value = URIConstants.INSERTREFERRERDATA, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String insertReferrerData(@RequestBody String request, @RequestHeader Map<String, String> headers) {
		if (request != null && !request.isEmpty()) {
			User user = Utility.gson.fromJson(request, User.class);
			if (user.getRequestSource() != null
					&& user.getRequestSource().equals(CoreEnums.RequestSource.toString())) {
				String response = Utility.hitPost(ExternalServiceURIConstants.INSERTREFERRERDATA, request, headers);
				logger.info("insert referrer data request:" + request + ",response:" + response);
				return response;
			}
		}
		GeneralResponse response = new GeneralResponse();
		response.setIsLogout(true);
		response.setStatus(CoreEnums.ResponseFailure.toString());
		response.setReason(
				ResponseEnums.InvalidRequestParameters.toString(headers != null ? headers.get("defaultlanguage") : ""));
		logger.info("insert referrer data request:" + request + ",response:" + response);
		return Utility.gson.toJson(response);
	}

	@RequestMapping(value = URIConstants.ELIGIBLEFORCOUPONS, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String isEligibleForCoupons(@RequestBody String request, @RequestHeader Map<String, String> headers) {
		if (request != null && !request.isEmpty()) {
			User user = Utility.gson.fromJson(request, User.class);
			if (fynderService.validateUser(user) && (user.getRequestSource() != null
					&& user.getRequestSource().equals(CoreEnums.RequestSource.toString()))) {
				String response = Utility.hitPost(ExternalServiceURIConstants.ELIGIBLEFORCOUPONS, request, headers);
				logger.info("is eligible for coupons request:" + request + ",response:" + response);
				return response;
			}
		}
		GeneralResponse response = new GeneralResponse();
		response.setIsLogout(true);
		response.setStatus(CoreEnums.ResponseFailure.toString());
		response.setReason(
				ResponseEnums.InvalidRequestParameters.toString(headers != null ? headers.get("defaultlanguage") : ""));
		logger.info("is eligible for coupons  request:" + request + ",response:" + response);
		return Utility.gson.toJson(response);
	}

	@RequestMapping(value = URIConstants.HOME, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String getHome(@RequestBody String request, @RequestHeader Map<String, String> headers) {
		if (request != null && !request.isEmpty()) {
			logger.info("get home request Headers:" + Utility.gson.toJson(headers));
			
			User user = Utility.gson.fromJson(request, User.class);
			if (fynderService.validateUser(user) && (user.getRequestSource() != null
					&&( user.getRequestSource().equals(CoreEnums.RequestSource.toString())||user.getRequestSource().equals(CoreEnums.RequestSourceWeb.toString())))) {
				String response = Utility.hitPost(baseUrl + ExternalServiceURIConstants.HOME, request, headers);
				logger.info("get home request:" + request + ",response:" + response);
				return response;
			}
		}
		GeneralResponse response = new GeneralResponse();
		response.setIsLogout(true);
		response.setStatus(CoreEnums.ResponseFailure.toString());
		response.setReason(
				ResponseEnums.InvalidRequestParameters.toString(headers != null ? headers.get("defaultlanguage") : ""));
		logger.info("get home  request:" + request + ",response:" + response);
		return Utility.gson.toJson(response);
	}
	@RequestMapping(value = URIConstants.ENDGAME, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String getEndGame(@RequestBody String request, @RequestHeader Map<String, String> headers) {
		if (request != null && !request.isEmpty()) {
			logger.info("get end game request Headers:" + Utility.gson.toJson(headers));
			
			User user = Utility.gson.fromJson(request, User.class);
			if (fynderService.validateUser(user) && (user.getRequestSource() != null
					&&( user.getRequestSource().equals(CoreEnums.RequestSource.toString())||user.getRequestSource().equals(CoreEnums.RequestSourceWeb.toString())))) {
				String response = Utility.hitPost(baseUrl + ExternalServiceURIConstants.ENDGAME, request, headers);
				logger.info("get end game request:" + request + ",response:" + response);
				return response;
			}
		}
		GeneralResponse response = new GeneralResponse();
		response.setIsLogout(true);
		response.setStatus(CoreEnums.ResponseFailure.toString());
		response.setReason(
				ResponseEnums.InvalidRequestParameters.toString(headers != null ? headers.get("defaultlanguage") : ""));
		logger.info("get end game  request:" + request + ",response:" + response);
		return Utility.gson.toJson(response);
	}
	@RequestMapping(value = URIConstants.LEADERBOARD, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String getLeaderboard(@RequestBody String request, @RequestHeader Map<String, String> headers) {
		if (request != null && !request.isEmpty()) {
			logger.info("get leaderboard request Headers:" + Utility.gson.toJson(headers));
			
			User user = Utility.gson.fromJson(request, User.class);
			if (fynderService.validateUser(user) && (user.getRequestSource() != null
					&&( user.getRequestSource().equals(CoreEnums.RequestSource.toString())||user.getRequestSource().equals(CoreEnums.RequestSourceWeb.toString())))) {
				String response = Utility.hitPost(baseUrl + ExternalServiceURIConstants.LEADERBOARD, request, headers);
				logger.info("get leaderboard request:" + request + ",response:" + response);
				return response;
			}
		}
		GeneralResponse response = new GeneralResponse();
		response.setIsLogout(true);
		response.setStatus(CoreEnums.ResponseFailure.toString());
		response.setReason(
				ResponseEnums.InvalidRequestParameters.toString(headers != null ? headers.get("defaultlanguage") : ""));
		logger.info("get leaderboard  request:" + request + ",response:" + response);
		return Utility.gson.toJson(response);
	}

	@RequestMapping(value = URIConstants.SANDBOX, method = RequestMethod.GET, produces = "application/json;charset=utf-8")
	public String sandBox(@RequestParam String code, @RequestParam String state, @RequestParam String correlation_id,
			@RequestHeader Map<String, String> headers) {
		logger.info("sandbox request:" + code + "," + state + "," + correlation_id + ",response:ok");
		return "ok";
	}

	@RequestMapping(value = "/heartbeat", method = RequestMethod.GET)
	public String heartbeat() {
		return Utility.hitGet(baseUrl + ExternalServiceURIConstants.HEARTBEAT, null);
	}
}
