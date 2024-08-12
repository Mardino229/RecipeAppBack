package com.nidas.recipesapp.config;

import com.nidas.recipesapp.Exception.EmailAlreadyExistsException;
import com.nidas.recipesapp.model.Chief;
import com.nidas.recipesapp.model.Jwt;
import com.nidas.recipesapp.model.JwtRefresh;
import com.nidas.recipesapp.repository.JwtRefeshRepository;
import com.nidas.recipesapp.repository.JwtRepository;
import com.nidas.recipesapp.service.ChiefService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

@Transactional
@AllArgsConstructor
@Service
public class JwtService {

    public static final String BEARER = "accessToken";
    public static final String REFRESH = "refreshToken";
    private static final Logger log = LoggerFactory.getLogger(JwtService.class);
    private final JwtRefeshRepository jwtRefeshRepository;
    private ChiefService userService;
    private JwtRepository jwtRepository;

    public Map<String, String> generate(String username) {
        Chief user = userService.loadUserByUsername(username);
        final Map<String, String> jwtMap = new java.util.HashMap<>(generateJwtToken(user));
        disable(username);
        JwtRefresh jwtRefresh = JwtRefresh
                .builder()
                .refreshToken(UUID.randomUUID().toString())
                .creation(Instant.now())
                .expiration(Instant.now().plusMillis(30*60*1000))
                .build();
        Jwt build = Jwt
                .builder()
                .token(jwtMap.get(BEARER))
                .inactive(false)
                .expire(false)
                .chief(user)
                .build();
        Jwt save = jwtRepository.save(build);
        JwtRefresh a = jwtRefeshRepository.save(jwtRefresh);
        save.setJwtRefresh(a);
        jwtRepository.save(save);
        jwtMap.put(REFRESH, jwtRefresh.getRefreshToken());
        jwtMap.put("roles", user.getRole().getType().toString());
        return jwtMap;
    }

    private Map<String, String> generateJwtToken(Chief user) {

        final long currentTime = System.currentTimeMillis();
        final long expirationTime = currentTime + 60 * 1000;

        final Map<String, Object> claim = Map.of(
                "nom", user.getUsername(),
                Claims.EXPIRATION, new Date(expirationTime),
                Claims.SUBJECT, user.getEmail()
        );

        final String bearer = Jwts.builder()
                .setIssuedAt(new Date(currentTime))
                .setExpiration(new Date(expirationTime))
                .setSubject(user.getEmail())
                .setClaims(claim)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
        return Map.of("accessToken", bearer);
    }

    private Key getKey() {
        String ENCRYPTION_KEY = "db0e89fb65989c4b9fdb48b3f60f09b0329b54fe0f67f9e78efe0f2de2146343";
        final byte[] decode = Decoders.BASE64.decode(ENCRYPTION_KEY);
        return Keys.hmacShaKeyFor(decode);
    }

    public String extractUsername(String token) {
        return getClaims(token, Claims::getSubject);

    }

    public Boolean isTokenExpired(String token) {

        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    private Date getExpirationDateFromToken(String token) {
        return getClaims(token, Claims::getExpiration);
    }

    private <T> T getClaims(String token, Function<Claims, T> function) {

        Claims claims = getAllClaims(token);
        return function.apply(claims);
    }

    private Claims getAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Jwt tokenbyvalue(String value) {
        return  jwtRepository.findByTokenAndInactiveAndExpire(value, false, false).orElseThrow();
    }

    public void disable(String email){
        Stream<Jwt> byChiefEmail = jwtRepository.findByChief_Email(email);
        byChiefEmail.forEach(jwt -> {
            jwt.setExpire(true);
            jwt.setInactive(true);
            jwtRepository.save(jwt);
        });
    }

    public void logout(String refreshToken) {

        JwtRefresh byRefreshToken = jwtRefeshRepository.findByRefreshToken(refreshToken).orElseThrow(() -> new EmailAlreadyExistsException("Token invalid"));
        byRefreshToken.setExpire(true);
        jwtRefeshRepository.save(byRefreshToken);

        Jwt jwt = jwtRepository.findByJwtRefresh_RefreshToken(refreshToken).orElseThrow();
        jwt.setExpire(true);
        jwt.setInactive(true);
        jwtRepository.save(jwt);
    }

    @Scheduled(cron = "0 * * * * *")
    public void removeToken(){
        log.info("Suppression des tokens {}", Instant.now());
        jwtRepository.deleteAllByExpireAndInactive(true, true);
    }

    public Map<String, String> refreshToken(String refreshTokenRequest) {

        Jwt jwt = jwtRepository.findByJwtRefresh_RefreshToken(refreshTokenRequest).orElseThrow(
                () -> new EmailAlreadyExistsException("Token invalide")
        );
        if (jwt.getJwtRefresh().isExpire() || jwt.getJwtRefresh().getExpiration().isBefore(Instant.now())){
            throw new EmailAlreadyExistsException("refresh invalide");
        }

        disable(jwt.getChief().getEmail());
        return  generate(jwt.getChief().getEmail());
    }
}
