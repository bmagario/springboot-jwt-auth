package com.example.challenge.services;

import com.example.challenge.dto.UserMapper
import com.example.challenge.dto.UserRegistrationRequestDTO
import com.example.challenge.dto.UserRegistrationResponseDTOMapper
import com.example.challenge.dto.UserRegistrationResponseDTO
import com.example.challenge.models.Phone
import com.example.challenge.models.User
import com.example.challenge.repositories.UserRepository
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.AutoCleanup
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

@SpringBootTest
class UserServiceSpec extends Specification {

    @Subject
    UserService userService

    @MockBean
    UserRepository userRepository

    @MockBean
    PasswordEncoder passwordEncoder

    @AutoCleanup
    UserMapper userMapper = Mock((Map<String, Object>) null)

    private Map<String, Object> UserRegistrationResponseDTOMapper
    @AutoCleanup
    UserRegistrationResponseDTOMapper responseDTOMapper = Mock(UserRegistrationResponseDTOMapper)

    def setup() {
        userService = new UserService(
                userRepository, passwordEncoder, userMapper, responseDTOMapper, null)
    }

    @Unroll
    def "loadUserByUsername should return UserDetails when user exists"() {
        given:
        String username = "test@example.com"
        User user = new User(username, "password", "Test User", [new Phone("1234567890", "123")])
        user.id = 1L

        userRepository.findByEmail(username) >> Optional.of(user)

        when:
        UserDetails userDetails = userService.loadUserByUsername(username)

        then:
        userDetails.username == username
        userDetails.password == "password"
        userDetails.authorities.size() == 1
        userDetails.authorities[0].authority == "ROLE_USER"
    }

    @Unroll
    def "loadUserByUsername should throw UsernameNotFoundException when user does not exist"() {
        given:
        String username = "nonexistent@example.com"
        userRepository.findByEmail(username) >> Optional.empty()

        when:
        userService.loadUserByUsername(username)

        then:
        thrown(UsernameNotFoundException)
    }

    @Unroll
    def "registerUser should return UserRegistrationResponseDTO with token"() {
        given:
        UserRegistrationRequestDTO request = new UserRegistrationRequestDTO(
                "test@example.com", "password", "Test User", [new Phone("1234567890", "123")])

        User user = new User(request.email, request.password, request.name, request.phones)
        user.id = 1L

        userRepository.save(_) >> user

        when:
        UserRegistrationResponseDTO responseDTO = userService.registerUser(request)

        then:
        responseDTO.email == request.email
        responseDTO.name == request.name
        responseDTO.phones == request.phones
        responseDTO.token != null
        responseDTO.token.length() > 0
        responseDTO.authorities.size() == 1
        responseDTO.authorities[0].authority == "ROLE_USER"
    }
}
