package cn.cdyxtech.lab.util;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.emin.base.exception.EminException;

import io.jsonwebtoken.Jwts;

public class JWTUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(JWTUtil.class);
    public static final String HEADER_STRING = "Authorization";

    public static final long EXPIRATION_TIME = 24 * 3600_000L; // 1 天
    public static final String SECRET = "lab.token.ThisIsASecret";



    public static JSONObject validateToken(String token) {
        try {
            if (token != null) {
                // parse the token.
                Map<String, Object> body = Jwts.parser()
                        .setSigningKey(SECRET)
                        .parseClaimsJws(token)
                        .getBody();                
               return (JSONObject) JSON.toJSON(body);
            } else {
                throw new TokenValidationException("Missing token");
            }
        } catch (Exception e) {
            LOGGER.error("身份验证失败异常", e);
            //TODO 错误码
            throw new EminException("请重新登录");
        }
    }

    static class TokenValidationException extends RuntimeException {
        /**
		 * 
		 */
		private static final long serialVersionUID = -8004158875129021981L;

		public TokenValidationException(String msg) {
            super(msg);
        }
    }

    public static void main(String[] args) {
        JSONObject jsonObject = validateToken("eyJhbGciOiJIUzUxMiJ9.eyJyZWFsTmFtZSI6bnVsbCwiZWNtSWRzIjpbXSwibW9iaWxlIjoiMTU5MjgxNzM1MDAiLCJpZCI6MSwidXNlclR5cGUiOiIxIiwiZXhwIjoxNTQ3NzE2ODI5fQ.RXEJgPRoBf8Uq-bJ6JE2BRrEUrvSYhLuybmUFiezbfK6XTGHQWqZYm7OjaxHYww0e7iB56qjKJ-5aY0-V8agyQ");
        System.out.println(jsonObject.toJSONString());
    }
}
