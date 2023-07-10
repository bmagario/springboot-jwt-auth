package com.example.challenge.dto;

import com.example.challenge.models.Phone;
import com.example.challenge.models.User;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class UserMapper implements Function<UserRegistrationRequestDTO, User> {

    @Override
    public User apply(UserRegistrationRequestDTO userRegistrationRequestDTO) {
        User user = new User();
        user.setName(userRegistrationRequestDTO.getName());
        user.setEmail(userRegistrationRequestDTO.getEmail());
        user.setPassword(userRegistrationRequestDTO.getPassword());
        user.setPhones(
                        userRegistrationRequestDTO.getPhones().stream().map(
                                phoneDTO -> Phone.builder()
                                        .cityCode(phoneDTO.getCityCode())
                                        .countryCode(phoneDTO.getCountryCode())
                                        .number(phoneDTO.getNumber())
                                        .build()
                        ).collect(Collectors.toList())
                );
        return user;
    }
}
