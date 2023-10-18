package com.example.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.board.email.Mailer;
import com.example.board.email.SMTPAuthenticator;

@Controller
public class ContactController {
	@GetMapping("/contact")
	public String contact() {
		return "contact/contact";
	}

	@PostMapping("/contact")
	public String contact(String email, String subject, String content) {
		Mailer mailer = new Mailer();
		mailer.sendMail(
			"seorab@naver.com", // 수신 이메일(관리자)
			"[" + email + "]" + subject, // [작성자 이메일]제목
			content, // 본문
			new SMTPAuthenticator()); // 인증
		return "redirect:/contact";
	}
}