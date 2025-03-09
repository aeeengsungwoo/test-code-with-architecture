package com.example.demo.user.service.port;

import org.springframework.mail.javamail.JavaMailSender;

public interface MailSender {

    void send(String email, String title, String content);

}
