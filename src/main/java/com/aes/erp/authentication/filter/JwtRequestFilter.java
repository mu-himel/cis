package com.aes.erp.authentication.filter;

import com.aes.erp.authentication.CustomUserDetailsService;
import com.aes.erp.authentication.JwtUtil;
import com.aes.erp.authentication.OrganizationPrincipal;
import com.aes.erp.authentication.dto.ClaimResponseDto;
import com.aes.erp.exception.AesException;
import com.aes.erp.inventory.entity.Organization;
import com.aes.erp.inventory.service.OrganizationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
import java.util.*;

/**
 * Class documentation Comments to be added
 * */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private CustomUserDetailsService myUserDetailsService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        Organization organization = null;
        Long orgId = null;

        if((request.getHeader("orgId")!=null)){
            orgId =Long.parseLong(request.getHeader("orgId"));
            organization = organizationService.getOrganizationById(orgId);
        }

        if(orgId != null && organization == null){
            throw new AesException("Sorry! Organization not registered");
        }

        String username = null;
        String jwt = null;
        ClaimResponseDto claimResponseDto = null;
        try {
            if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwt = authorizationHeader.substring(7);
                username = jwtUtil.extractUsername(jwt);
                claimResponseDto = jwtUtil.extractId(jwt);
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
            else if(orgId != null && organization!=null && SecurityContextHolder.getContext().getAuthentication() == null){
                if(!jwtUtil.validateOrganization(orgId)){
                    throw new AesException("Organization ID is not Registered in our system");
                }
                Organization _organization = jwtUtil.getOrganization(orgId);
                if(_organization.getRole() == null || !_organization.getRole().getRoleName().equals("ORGANIZATION")){
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    throw new AesException("Organization doesn't have necessary role permission");
                }
                Set<GrantedAuthority> authorities = new HashSet<>();
                authorities.add(new SimpleGrantedAuthority(organization.getRole().getRoleName()));
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        new OrganizationPrincipal(orgId, organization.getName()), null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            request.setAttribute("loggedInUser",claimResponseDto);
            String uri = (request.getHeader("uri")!=null)? request.getHeader("uri") : null;
            if(uri!=null){
                request.setAttribute("uri", uri);
            }
            

            filterChain.doFilter(request, response);
        }catch (Exception ex){
            // ex.printStackTrace();
            Map<String,String> map = new HashMap<>();
            map.put("message", ex.getMessage());
            ObjectMapper objectMapper = new ObjectMapper();
            response.getWriter().print(objectMapper.writeValueAsString(map));
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }
}
