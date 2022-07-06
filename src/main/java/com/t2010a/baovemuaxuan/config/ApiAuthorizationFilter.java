package com.t2010a.baovemuaxuan.config;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.t2010a.baovemuaxuan.util.JwtUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class ApiAuthorizationFilter extends OncePerRequestFilter {

    private static final String[] IGNORE_PATHS = {"/api/v1/login", "/api/v1/register", "/api/v1/products"};

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // nếu người client gọi đến link đăng nhập, đăng ký và những link không cần check login
        String requestPath = request.getServletPath();
        if (Arrays.asList(IGNORE_PATHS).contains(requestPath)) {
            // cho qua
            filterChain.doFilter(request, response);
            return;
        }
        // trường hợp client không có request header theo format cần thiết
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // cho qua (không có dấu kiểm duyệt)
            filterChain.doFilter(request, response);
            return;
        }
        // lấy thông tin token từ trong request header
        try {
            // remove chữ Bearer
            String token = authorizationHeader.replace("Bearer", "").trim();
            // dịch ngược JWT
            DecodedJWT decodedJWT = JwtUtil.getDecodedJwt(token);
            // lấy thông tin username
            String username = decodedJWT.getSubject();
            // lấy thông tin role đăng nhập
            String role = decodedJWT.getClaim(JwtUtil.ROLE_CLAIM_KEY).asString();
            // tạo danh sách role
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(role));
            // lưu thông tin người dùng đăng nhập vào spring context
            // để cho các controller hay filter phía sau có thể sử dụng
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            // đưa request đi tiếp
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            //show error
            response.setStatus(HttpStatus.FORBIDDEN.value());
            Map<String, String> errors = new HashMap<>();
            errors.put("error", ex.getMessage());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().println(new Gson().toJson(errors));
        }
    }
}
