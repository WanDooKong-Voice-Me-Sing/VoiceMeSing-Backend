package com.wandookong.voice_me_sing.util;

import jakarta.servlet.http.Cookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    public Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);

        if (key.equals("access")) cookie.setHttpOnly(false);
        else if (key.equals("refresh")) cookie.setHttpOnly(true);

        cookie.setPath("/");
//        cookie.setHttpOnly(false);
        cookie.setMaxAge(60 * 60 * 60);
        cookie.setSecure(true);
        cookie.setAttribute("SameSite", "None");

        System.out.println(key + " cookie created");

        return cookie;
    }

    public Cookie createExpiredCookie(String key) {
        Cookie cookie = new Cookie(key, null);

        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setAttribute("SameSite", "None");

        return cookie;
    }
}
