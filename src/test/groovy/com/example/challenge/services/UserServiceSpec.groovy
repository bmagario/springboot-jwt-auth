package com.example.challenge.services

import com.example.challenge.config.JwtUtil
import com.example.challenge.dto.PhoneDTO
import com.example.challenge.dto.UserMapper
import com.example.challenge.dto.UserRegistrationRequestDTO
import com.example.challenge.dto.UserRegistrationResponseDTOMapper
import com.example.challenge.dto.UserRegistrationResponseDTO
import com.example.challenge.models.User
import com.example.challenge.repositories.UserRepository
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.core.userdetails.UserDetails
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

    @MockBean
    JwtUtil jwtUtil

    UserMapper userMapper = new UserMapper()
    UserRegistrationResponseDTOMapper responseDTOMapper = new UserRegistrationResponseDTOMapper()

    def setup() {
        userRepository = Mock()
        passwordEncoder = Mock()
        jwtUtil = Mock()
        userService = new UserService(
                userRepository, passwordEncoder, userMapper, responseDTOMapper, jwtUtil)
    }

    @Unroll
    def "loadUserByUsername should return UserDetails when user exists"() {
        given:
        String username = "test@example.com"
        String password = "secret"
        String name = "Test User"
        User user = new User()
        user.setId(1L)
        user.setName(name)
        user.setEmail(username)
        user.setPassword(password)

        userRepository.findByEmail(username) >> Optional.of(user)

        when:
        UserDetails userDetails = userService.loadUserByUsername(username)

        then:
        userDetails.username == username
        userDetails.password == password
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
        PhoneDTO phoneDto = new PhoneDTO()
        phoneDto.setNumber(1234567890L)
        phoneDto.setCountryCode("123")
        String username = "test@example.com"
        String password = "secret"
        String name = "Test User"
        UserRegistrationRequestDTO request = new UserRegistrationRequestDTO(name, username, password, [phoneDto])

        User user = new User()
        user.setId(1L)
        user.setName(request.name)
        user.setEmail(request.email)
        user.setPassword(request.password)

        userRepository.save(_) >> user
        jwtUtil.generateToken(user.getUsername()) >> "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJibWFnYXJpb0BnbWFpbC5jb20iLCJleHAiOjE2OTAyOTU5NTIsImlhdCI6MTY5MDIwOTU1Mn0.8SA24ZeQY50Wv3vEh5Bj5aga73EZ7PY5ZokTen3KKz8"

        when:
        UserRegistrationResponseDTO responseDTO = userService.registerUser(request)

        then:
        responseDTO.getCreated() != null
        responseDTO.getLastLogin() == null
        responseDTO.isActive()
        responseDTO.getToken() != null
        responseDTO.getToken().length() > 0
    }
}
