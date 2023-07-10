package com.example.challenge.dto;

import com.example.challenge.models.Phone;
import com.example.challenge.models.User;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class UserRegistrationResponseDTOMapper implements Function<User, UserRegistrationResponseDTO> {

    @Override
    public UserRegistrationResponseDTO apply(User user) {
        return UserRegistrationResponseDTO.builder()
            .created(user.getCreated())
            .lastLogin(user.getLastLogin())
            .isActive(user.isActive()).build();
    }
}
