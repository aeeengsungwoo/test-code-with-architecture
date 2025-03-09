package com.example.demo.service;

import com.example.demo.mock.FakeMailSender;
import com.example.demo.user.service.CertificationService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest
public class CertificationServiceTest {
    @Test
    public void 이메일과_컨텐츠가_제대로_만들어져서_보내지는지_테스트한다()  {
        //given -> Fake 객체를 만들어서 테스트
        FakeMailSender fakeMailSender = new FakeMailSender();
        CertificationService certificationService = new CertificationService(fakeMailSender);
        //when
        certificationService.send("kok202@naver.com", 1, "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        //then
        assertThat(fakeMailSender.email).isEqualTo("kok202@naver.com");
        assertThat(fakeMailSender.title).isEqualTo("Please certify your email address");
        assertThat(fakeMailSender.content).isEqualTo("Please click the following link to certify your email address: http://localhost:8080/api/users/1/verify?certificationCode=aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    }

}
