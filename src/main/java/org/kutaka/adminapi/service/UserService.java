package org.kutaka.adminapi.service;

import org.kutaka.adminapi.model.User;
import org.kutaka.adminapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
  @Autowired
  UserRepository userRepository;

  @Override
  public User loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("username not found"));
  }

  public User findById(String id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new UsernameNotFoundException("user id not found"));
  }
}