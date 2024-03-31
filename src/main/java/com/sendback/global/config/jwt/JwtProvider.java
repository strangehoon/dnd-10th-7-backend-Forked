package com.sendback.global.config.jwt;

import com.sendback.domain.auth.dto.SocialUserInfo;
import com.sendback.domain.auth.dto.Token;
import com.sendback.domain.user.dto.SigningUser;
import com.sendback.global.config.redis.RedisService;
import com.sendback.global.exception.type.TokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import static com.sendback.domain.auth.exception.AuthExceptionType.*;
import static com.sendback.domain.user.exception.UserExceptionType.EXPIRED_SIGN_TOKEN;
import static com.sendback.domain.user.exception.UserExceptionType.INVALID_SIGN_TOKEN;

@Getter
@Component
@RequiredArgsConstructor
public class JwtProvider {
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.access-token-expire-time}")
    private long ACCESS_TOKEN_EXPIRE_TIME;
    @Value("${jwt.refresh-token-expire-time}")
    private long REFRESH_TOKEN_EXPIRE_TIME;

    @Value("${jwt.sign-token-expire-time}")
    private long SIGN_TOKEN_EXPIRE_TIME;

    private final RedisService redisService;

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION);
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER)){
            return bearerToken.substring(7);
        }
        return null;
    }

    public Token issueToken(Long userId) {
        Token token = new Token(generateToken(userId, true), generateToken(userId, false));
        redisService.put(userId, token.refreshToken(), REFRESH_TOKEN_EXPIRE_TIME);
        return token;
    }

    public String generateSignToken(SocialUserInfo socialUserInfo, String type) {
        Date now = new Date(System.currentTimeMillis());
        final Date expiration = new Date(now.getTime() + SIGN_TOKEN_EXPIRE_TIME);
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .claim("id", socialUserInfo.id())
                .claim("email", socialUserInfo.email())
                .claim("socialname", socialUserInfo.socialname())
                .claim("profileImageUrl", socialUserInfo.profileImageUrl())
                .claim("type", type)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    private String generateToken(Long userId, boolean isAccessToken) {
        Date now = new Date(System.currentTimeMillis());
        final Date expiration = new Date(now.getTime() + (isAccessToken ? ACCESS_TOKEN_EXPIRE_TIME : REFRESH_TOKEN_EXPIRE_TIME));
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setSubject(String.valueOf(userId))
                .claim("type", isAccessToken ? "ACCESS_TOKEN" : "REFRESH_TOKEN")
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public SigningUser getSignUserInfo(String signToken) {
        Claims body = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(signToken)
                .getBody();
        String id = (String)body.get("id");
        String socialname = (String)body.get("socialname");
        String profileImageUrl = (String)body.get("profileImageUrl");
        String email = (String)body.get("email");
        String type = (String)body.get("type");
        return new SigningUser(id, socialname, profileImageUrl, email, type);
    }

    public void validateSignToken(String signToken){
        try {
            getJwtParser().parseClaimsJws(signToken);
        } catch (ExpiredJwtException e) {
            throw new TokenException(EXPIRED_SIGN_TOKEN);
        } catch (Exception e){
            throw new TokenException(INVALID_SIGN_TOKEN);
        }
    }

    public void validateAccessToken(String accessToken) {
        try {
            getJwtParser().parseClaimsJws(accessToken);
        } catch (ExpiredJwtException e) {
            throw new TokenException(EXPIRED_ACCESS_TOKEN);
        } catch (Exception e) {
            throw new TokenException(INVALID_ACCESS_TOKEN_VALUE);
        }
    }

    public Long getSubject(String token) {
        return Long.valueOf(getJwtParser().parseClaimsJws(token)
                .getBody()
                .getSubject());
    }

    private JwtParser getJwtParser() {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build();
    }

    private Key getSigningKey() {
        String encoded = Base64.getEncoder().encodeToString(secretKey.getBytes());
        return Keys.hmacShaKeyFor(encoded.getBytes());
    }

    public Long parseRefreshToken(String token) {
        try {
            Claims claims = getJwtParser().parseClaimsJws(token).getBody();
            return Long.parseLong(claims.getSubject());
        }
        catch (ExpiredJwtException e) {
            throw new TokenException(EXPIRED_REFRESH_TOKEN);
        }
        catch (Exception ex) {
            throw new TokenException(INVALID_REFRESH_TOKEN_VALUE);
        }

    }
}