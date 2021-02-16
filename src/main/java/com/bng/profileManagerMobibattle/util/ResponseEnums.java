package com.bng.profileManagerMobibattle.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public enum ResponseEnums {
	
	FILEGETCOULDNOTSTREAMFAIL("Sorry we could not process the requested image/video at the moment due to some error. Please try again in some time.","တောင်းဆိုထားသောဗီဒီယိုအား လတ်တလောတွင် ကြည့်၍မရနိုင်သေးပါ၊ ကျေးဇူးပြု၍ ထပ်မံကြိုးစားပေးပါ "),
	VIDEOCOULDNOTSTREAMFAIL("Sorry we could not process/download your requested video at the moment due to some error. Please try again in some time.","တောင်းဆိုထားသောဗီဒီယိုအား လတ်တလောတွင် ကြည့်၍/ ဒေါင်းလုပ်လုပ်၍ မရနိုင်သေးပါ ကျေးဇူးပြု၍ ထပ်မံကြိုးစားပေးပါ "),
	INVALIDJSONFAIL("Something went wrong. Please try again.","တစ်စုံတခု မှားယွင်းနေပါသည်၊ ကျေးဇူးပြု၍ ထပ်မံကြိုးစားပေးပါ"),
	SENDOTPFAIL("Sorry we were unable to send OTP at the moment on your registered number. Tap here to resend the OTP.","သင်စာရင်းသွင်းထားသော ဖုန်းနံပါတ်သို့ တစ်ခါသုံးစကားဝှက် ပို့၍မရနိုင်သေးပါ၊ တစ်ခါသုံးစကားဝှက်အား ထပ်ပို့ပေးရန် ဤနေရာသို့နှိပ်ပါ"),
	REGISTRATIONINPROGRESSFAIL("Sorry this number is already registered on another device. Please try again with a different number.","ဤနံပါတ်မှာ တခြားစက်ဖြင့် စာရင်းသွင်းပြီးသားဖြစ်နေပါသည်၊ မတူသောနံပါတ်ဖြင့် ထပ်မံစာရင်းသွင်းပေးပါ"),
	MAXATTEMPTSREACHED("We are sorry to anounce that we are going to block your account for some time as you have reached your maximum attempts to register with this number.","ဝမ်းနည်းပါတယ်၊ ဤနံပါတ်ဖြင့်စာရင်းသွင်းနိုင်ရန် အကြိမ်ရေ ပြည့်သွားပါသဖြင့် သင့်အကောင့်ကို ပိတ်ဆို့တော့မည် ဖြစ်ပါသည်"),
	DEVICEBLOCKED("Hey, this device is already blocked. Please try again using some other device.","ဤစက်အား ပိတ်ဆို့ထားပြီးဖြစ်ပါသည်၊ ကျေးဇူးပြု၍ တခြားစက်ဖြင့် ထပ်မံဝင်ရောက်ပေးပါ"),
	INVALIDOPCO("Please try to login with MPT number","ကျေးဇူးပြုပြီး MPT နံပါတ်နဲ့ login လုပ်ပါါ"),
	OTPSENT("OTP has been sent to your registered mobile number","OTP ကိုသင်၏မှတ်ပုံတင်ထားသောနံပါတ်သို့ပေးပို့ပြီးပါပြီ"),
	NUMBERDEVICEBLOCKED("number and deviceID is blocked","နံပါတ်နှင့် deviceID ကိုပိတ်ဆို့ထားသည်"),
	InvalidRequestParameters("Since you have logged in from a different device, you have been logged out from this one!!. Please login again from this device.","သင်သည်အခြားကိရိယာမှ logged in ဝင်သောကြောင့်သင်ဤ device မှသင်ထွက်လိုက်ပါပြီ !! ။ ကျေးဇူးပြုပြီးဒီကိရိယာမှထပ်မံဝင်ရောက်ပါ"),
	InvalidRequestJson("Invalid Request Json","မမှန်ကန်သောတောင်းဆိုမှု"),
	USERNOTFOUND("User not found","အသုံးပြုသူကိုရှာမတွေ့ပါှု"),
	USERISBLOCKED("User is blocked","အသုံးပြုသူအားပိတ်ပင်ထားသည်"),
	OTPEXPIRED("OTP expired","စကား၀ှက်သက်တမ်းကုန်သွားပြီ"),
	INVALIDDEVICEID("Invalid deviceId","မမှန်ကန်သောစက်ပစ္စည်း"),
	WRONGOTP("Wrong OTP entered","စကား၀ှက်မှားယွင်းနေပါသည်");
	
	private String enResponse;
	private String brResponse;

	private ResponseEnums(String enResponse, String brResponse) {
		this.enResponse = enResponse;
		this.brResponse = brResponse;
	}
	
	public String toString(String language) {
		if(language==null)
			return Base64.getEncoder().encodeToString(enResponse.getBytes(StandardCharsets.UTF_8));
		switch(language) {
		case "en":
			return Base64.getEncoder().encodeToString(enResponse.getBytes(StandardCharsets.UTF_8));
		case "mm":
			return Base64.getEncoder().encodeToString(brResponse.getBytes(StandardCharsets.UTF_8));
		default:
			return Base64.getEncoder().encodeToString(enResponse.getBytes(StandardCharsets.UTF_8));
		}
	}

}
