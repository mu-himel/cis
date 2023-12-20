package com.aes.erp.authentication;

import com.aes.erp.authentication.dto.ClaimResponseDto;
import com.aes.erp.authentication.entity.CustomUserDetails;
import com.aes.erp.config.Constants;
import com.aes.erp.exception.AesException;
import com.aes.erp.inventory.entity.Organization;
import com.aes.erp.inventory.entity.OrganizationStatus;
import com.aes.erp.inventory.service.OrganizationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Class documentation Comments to be added
 * */
@Service
public class JwtUtil {
    @Autowired
    private OrganizationService organizationService;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {

        return Jwts.parser().setSigningKey(Constants.SECRET_KEY).parseClaimsJws(token).getBody();

    }
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(CustomUserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails);
    }

    private String createToken(Map<String, Object> claims, CustomUserDetails userDetails) {
        return Jwts.builder().setClaims(claims)
                .claim("id",userDetails.getId())
                .claim("userinfo",userDetails.getUserInfoDto())
                .claim("authorities",userDetails.getAuthorities())
                .setSubject(userDetails.getUsername()).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + Constants.TOKEN_EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, Constants.SECRET_KEY).compact();
    }

    public  Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        Boolean valid = isTokenExpired(token);
        return (username.equals(userDetails.getUsername()) && !valid);
    }

    public ClaimResponseDto extractId(String token) {
        token = token.replaceAll("Bearer ", "");
        Claims c = extractAllClaims(token);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(extractAllClaims(token),ClaimResponseDto.class);
    }

    public boolean validateOrganization(Long orgId){
        return organizationService.isOrganizationExistAndEnabled(orgId);
    }
    public Organization getOrganization(Long id){
        Organization organization = organizationService.getOrganizationById(id);
        if(organization.getStatus().equals(OrganizationStatus.DISABLED))throw new AesException("Organization Status is Disabled");
        return organization;
    }
}
