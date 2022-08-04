package org.kutaka.adminapi.controller;

import org.kutaka.adminapi.dto.LoginDTO;
import org.kutaka.adminapi.dto.TokenDTO;
import org.kutaka.adminapi.helper.JwtHelper;
import org.kutaka.adminapi.model.User;
import org.kutaka.adminapi.repository.UserRepository;
import org.kutaka.adminapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/auth")
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;
  @Autowired
  UserRepository userRepository;
  @Autowired
  JwtHelper jwtHelper;
  @Autowired
  PasswordEncoder passwordEncoder;
  @Autowired
  UserService userService;

  @PostMapping("/login")
  @Transactional
  public ResponseEntity<?> login(@RequestBody LoginDTO dto) {
    Authentication authentication = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));
    SecurityContextHolder.getContext().setAuthentication(authentication);
    User user = (User) authentication.getPrincipal();
    String accessToken = jwtHelper.generateAccessToken(user);

    return ResponseEntity.ok(new TokenDTO(accessToken));
  }
}