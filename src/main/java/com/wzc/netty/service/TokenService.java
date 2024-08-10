package com.wzc.netty.service;


import com.wzc.netty.pojo.dto.UserDetailsDTO;
import io.jsonwebtoken.Claims;

import javax.servlet.http.HttpServletRequest;

public interface TokenService {

    String createToken(UserDetailsDTO userDetailsDTO);

    String createToken(String subject);

    String getSubject(String token);

    boolean verifyToken(String token);

    UserDetailsDTO getUserDetailDTO(HttpServletRequest request);



}