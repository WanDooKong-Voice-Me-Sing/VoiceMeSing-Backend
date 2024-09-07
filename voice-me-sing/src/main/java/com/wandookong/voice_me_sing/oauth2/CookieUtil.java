package com.wandookong.voice_me_sing.oauth2;

import jakarta.servlet.http.Cookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    public Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);

//        if (key.equals("access")) cookie.setPath("/");
//        else if (key.equals("refresh")) cookie.setPath("/reissue");

        cookie.setPath("/");
        cookie.setHttpOnly(false);
        cookie.setMaxAge(60*60*60);
        cookie.setSecure(true);
        cookie.setAttribute("SameSite", "None");

        System.out.println(key + " cookie created");

        return cookie;
    }
}
