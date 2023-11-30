package com.aes.erp.authentication.filter;

import com.aes.erp.authentication.CustomUserDetailsService;
import com.aes.erp.authentication.JwtUtil;
import com.aes.erp.authentication.OrganizationPrincipal;
import com.aes.erp.exception.AesException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Class documentation Comments to be added
 * */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private CustomUserDetailsService myUserDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");
        final String orgId = request.getHeader("orgId");
        String username = null;
        String jwt = null;
        try {
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            username = jwtUtil.extractUsername(jwt);
        }
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.myUserDetailsService.loadUserByUsername(username);
            if(jwtUtil.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        else if(orgId != null && SecurityContextHolder.getContext().getAuthentication() == null){
            if(!jwtUtil.validateOrganization(Long.parseLong(orgId))){
                throw new AesException("Organization ID is not Registered in our system");
            }
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    new OrganizationPrincipal(orgId), null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

            filterChain.doFilter(request, response);
        }catch (Exception ex){
//            ex.printStackTrace();
            Map<String,String> map = new HashMap<>();
            map.put("message", ex.getMessage());
            ObjectMapper objectMapper = new ObjectMapper();
            response.getWriter().print(objectMapper.writeValueAsString(map));
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }
}
