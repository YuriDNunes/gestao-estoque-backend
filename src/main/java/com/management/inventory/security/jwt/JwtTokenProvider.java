package com.management.inventory.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.management.inventory.security.dto.TokenResponseDTO;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Date;
import java.util.List;

@Service
public class JwtTokenProvider {

    @Value("${security.jwt.token.secret-key}")
    private String jwtSecret;

    @Value("${security.jwt.token.expire-length}")
    private long jwtExpiration;

    private UserDetailsService userDetailsService;

    Algorithm algorithm = null;

    public JwtTokenProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostConstruct
    protected void init(){
        this.algorithm = Algorithm.HMAC256(jwtSecret.getBytes());
    }

    private String buildAccessToken(String email, List<String> roles, Date now, Date validity){
        String issuersUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        return JWT.create()
                .withClaim("roles", roles)
                .withIssuedAt(now)
                .withExpiresAt(validity)
                .withSubject(email)
                .withIssuer(issuersUrl)
                .sign(algorithm);
    }

    private String buildRefreshToken(String email, Date now){
        Date refreshTokenValidity = new Date(now.getTime() + (jwtExpiration * 3));
        return JWT.create()
                .withIssuedAt(now)
                .withExpiresAt(refreshTokenValidity)
                .withSubject(email)
                .sign(algorithm);
    }

    public TokenResponseDTO generateToken(String username, List<String> roles){

        Date now = new Date();
        Date validity =new Date(now.getTime() + jwtExpiration);
        String accessToken = buildAccessToken(username, roles, now, validity);
        String refreshToken = buildRefreshToken(username, now);
        return new TokenResponseDTO(username, true, now, validity, accessToken, refreshToken);
    }

    //Extrai o token
    public String resolveToken(HttpServletRequest request){
        String bearerToken =request.getHeader("Authorization");

        if(StringUtils.hasLength(bearerToken) && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring("Bearer ".length());
        } else {
            return ""; // Criar exception personalizada para retornar jwt invalido, InvalidJwtAuthenticationException
        }
    }

    //Decodifica/verifica o token
    private DecodedJWT decodedToken(String token) {
        JWTVerifier verifier = JWT.require(this.algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(token);
        return decodedJWT;
    }

    //Validação
    public boolean validateToken(String token){
        try{
            //Decodifica
            DecodedJWT decodedJWT = decodedToken(token);
            //Verifica se esta expirado
            if(decodedJWT.getExpiresAt().before(new Date())){
                return false;
            }
            return true;
        } catch (Exception e) {
            return false; // Criar exception personalizada para retornar jwt invalido, InvalidJwtAuthenticationException
        }
    }

    //Autentica o token
    public Authentication getAuthentication(String token){
        DecodedJWT decodedJWT = decodedToken(token);
        UserDetails userDetails = this.userDetailsService
                .loadUserByUsername(decodedJWT.getSubject());

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

}
