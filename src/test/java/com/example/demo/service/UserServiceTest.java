package com.example.demo.service;

import com.example.demo.common.exception.CertificationCodeNotMatchedException;
import com.example.demo.common.exception.ResourceNotFoundException;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.UserCreateDto;
import com.example.demo.user.infrastructure.UserEntity;
import com.example.demo.user.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.TestPropertySource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

@TestPropertySource("classpath:test-application.properties")
@SpringBootTest

public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockBean
    private JavaMailSender mailSender;

    @BeforeEach
    void setUp() {
        String insertData = """
            INSERT INTO users (id, email, nickname, address, certification_code, status, last_login_at)
            VALUES (1, 'kok202@naver.com', 'kok202', 'Seoul', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'ACTIVE', 0);

            INSERT INTO users (id, email, nickname, address, certification_code, status, last_login_at)
            VALUES (2, 'kok303@naver.com', 'kok303', 'Busan', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'PENDING', 0);
        """;
        jdbcTemplate.execute(insertData);
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute("DELETE FROM users");
    }

    @Test
    void getIdByEmail은_ACTIVE상태인_유저를_찾아올_수_있다(){
        //given
        String email = "kok202@naver.com";

        //when
        UserEntity result = userService.getByEmail(email);

        //then
        assertThat(result.getNickname()).isEqualTo("kok202");

    }

    @Test
    void getIdByEmail은_PENDING상태인_유저를_찾아올_수_없다(){
        //given
        String email = "kok303@naver.com";

        //when

        //then
        assertThatThrownBy(() -> {
            UserEntity result = userService.getByEmail(email);
        }).isInstanceOf(ResourceNotFoundException.class);

    }

    @Test
    void getById는_ACTIVE상태인_유저를_찾아올_수_있다(){
        //given

        //when
        UserEntity result = userService.getById(1);

        //then
        assertThat(result.getNickname()).isEqualTo("kok202");

    }

    @Test
    void getById는_PENDING상태인_유저를_찾아올_수_없다(){
        //given

        //when

        //then
        assertThatThrownBy(() -> {
            UserEntity result = userService.getById(2);
        }).isInstanceOf(ResourceNotFoundException.class);

    }

    @Test
    void UserCreateDto를_이용하여_유저를_생성할_수_있다(){
        //given
        UserCreateDto userCreateDto = UserCreateDto.builder()
                .email("kok202@naver.com")
                .address("Seoul")
                .nickname("kok202-k")
                .build();
        //실제 메일 전송하는 부분을 테스트할 수 없기 때문에, 메일 전송 메서드가 호출되어도 테스트 환경에서는 아무것도 하지 않는다.
        BDDMockito.doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        //when
        UserEntity result = userService.create(userCreateDto);

        //then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(UserStatus.PENDING);

    }

    @Test
    void PENDING_상태의_사용자는_인증_코드를_발급받으면_액티브_상태로_변경된다() {
        // given: PENDING 상태의 사용자를 설정
        userService.verifyEmail(2, "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");

        // then: 사용자의 상태가 ACTIVE로 변경되었는지 확인
        UserEntity userEntity = userService.getById(2);
        assertThat(userEntity.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void PENDING_상태의_사용자는_잘못된_인증_코드를_발급받으면_예외를_던진다() {
        // when: 잘못된 인증 코드를 사용
        assertThatThrownBy(() -> userService.verifyEmail(2, "aaaaaa-aaaa-aaaa-aaaaaa-aaaaaaac"))
        .isInstanceOf(CertificationCodeNotMatchedException.class);
    }
}
