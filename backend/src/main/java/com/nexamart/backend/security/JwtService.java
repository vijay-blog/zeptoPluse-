package com.nexamart.backend.security;

import com.nexamart.backend.config.AppProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
  private final AppProperties props;
  public JwtService(AppProperties props){this.props=props;}
  private SecretKey key(){return Keys.hmacShaKeyFor(props.getJwtSecret().getBytes(StandardCharsets.UTF_8));}
  public String accessToken(Long id,String identifier,String role){return build(id,identifier,role,props.getAccessTokenMinutes()*60_000L,"access");}
  public String refreshToken(Long id,String identifier,String role){return build(id,identifier,role,props.getRefreshTokenDays()*86_400_000L,"refresh");}
  private String build(Long id,String identifier,String role,long ttl,String type){Instant now=Instant.now();return Jwts.builder().claims(Map.of("uid",id,"role",role,"type",type)).subject(identifier).issuedAt(Date.from(now)).expiration(new Date(now.toEpochMilli()+ttl)).signWith(key()).compact();}
  public Claims parse(String token){return Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).getPayload();}
  public boolean validAccess(String token){try{return "access".equals(parse(token).get("type",String.class)) && parse(token).getExpiration().after(new Date());}catch(Exception e){return false;}}
  public boolean validRefresh(String token){try{return "refresh".equals(parse(token).get("type",String.class)) && parse(token).getExpiration().after(new Date());}catch(Exception e){return false;}}
}
