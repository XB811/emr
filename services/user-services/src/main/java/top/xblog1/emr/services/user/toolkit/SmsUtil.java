package top.xblog1.emr.services.user.toolkit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

/**
 *
 */
@Slf4j
public final class SmsUtil {
    private static String httpGet(String url) {
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.exchange(url, HttpMethod.GET, null, String.class).getBody();
        return result;
    }


    public static void sendSms(String phoneNumber, String code) {
        String SPUG_SMS_URL ="https://push.spug.cc/send/AVEmpLJWm7g4";
        String param = "?code="+code+"&targets="+phoneNumber;
        String s = httpGet(SPUG_SMS_URL + param);
//        log.info(s);

    }
}
