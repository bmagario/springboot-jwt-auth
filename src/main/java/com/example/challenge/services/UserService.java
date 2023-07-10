package com.example.challenge.services;

import com.example.challenge.config.JwtUtil;
import com.example.challenge.dto.UserMapper;
import com.example.challenge.dto.UserRegistrationResponseDTOMapper;
import com.example.challenge.models.Phone;
import com.example.challenge.models.User;
import com.example.challenge.dto.UserRegistrationRequestDTO;
import com.example.challenge.dto.UserRegistrationResponseDTO;
import com.example.challenge.repositories.UserRepository;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder bcryptEncoder;

    private final UserMapper userMapper;
    private final UserRegistrationResponseDTOMapper userRegistrationResponseDTOMapper;
    private final JwtUtil jwtUtil;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return new org.springframework.security.core.userdetails.User(
                user.get().getUsername(),
                user.get().getPassword(),
                AuthorityUtils.createAuthorityList("ROLE_USER")
        );
    }

    public UserRegistrationResponseDTO registerUser(UserRegistrationRequestDTO request) {
        request.setPassword(bcryptEncoder.encode(request.getPassword()));
        User user = userMapper.apply(request);
        user = userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername());
        UserRegistrationResponseDTO responseDTO = userRegistrationResponseDTOMapper.apply(user);
        responseDTO.setToken(token);

        return responseDTO;
    }
}