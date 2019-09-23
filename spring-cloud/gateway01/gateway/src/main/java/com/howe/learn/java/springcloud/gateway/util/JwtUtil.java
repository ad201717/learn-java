package com.howe.learn.java.springcloud.gateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class JwtUtil {

    public static final String HEADER_AUTH = "x-token";
    private static final byte[] SECRET = "xxx".getBytes();
    private static final String TOKEN_PREFIX = "token";

    public static String generateToken(String user) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", new Random().nextInt());
        map.put("user", user);
        String jwt = Jwts.builder()
                .setSubject("user info").setClaims(map)
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
        return TOKEN_PREFIX + jwt;
    }

    public static Map<String, String> validateToken(String token) {
        if (null != token) {
            HashMap<String, String> map = new HashMap<>();
            Map<String, Object> body = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                    .getBody();
            String id = String.valueOf(body.get("id"));
            String user = String.valueOf(body.get("user"));
            map.put("id", id);
            map.put("user", user);
            if (StringUtils.isEmpty(user)) {
                throw new RuntimeException("user is error, please check");
            }
            return map;
        } else {
            throw new RuntimeException("token is empty, please check");
        }
    }
}
