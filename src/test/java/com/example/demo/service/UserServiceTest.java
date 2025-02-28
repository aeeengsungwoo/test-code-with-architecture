package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.UserEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@TestPropertySource("classpath:test-application.properties")
@SpringBootTest

public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

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

}
