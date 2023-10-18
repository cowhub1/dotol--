package com.example.board.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

import javax.servlet.http.HttpSession;

import com.example.board.model.User;
import com.example.board.repository.UserRepository;

@Controller
public class UserController {
	@Autowired
	UserRepository userRepository;

	@Autowired
	HttpSession session;

	@Autowired
	PasswordEncoder passwordEncoder;

	@GetMapping("/email-check")
	@ResponseBody
	public String emailCheck(@ModelAttribute User user) {
		String email = user.getEmail();

		User result = userRepository.findByEmail(email);

		if (result != null) { // 값 있음, 아이디가 있음, 가입 불가
			return "가입불가";
		} else { // 값 없음, 아이디가 없음, 가입 가능
			return "가입가능";
		}
	}

	@GetMapping("/signin")
	public String signin() {
		return "signin";
	}

	@PostMapping("/signin")
	public String signinPost(@ModelAttribute User user, Model model) {
		User dbUser = userRepository.findByEmail(user.getEmail());

		if (dbUser == null) {
			model.addAttribute("error", "이메일 또는 비밀번호가 일치하지 않습니다.");
			return "signin";
		}
		String encodedPwd = dbUser.getPwd();
		String userPwd = user.getPwd();
		boolean isMatch = passwordEncoder.matches(userPwd, encodedPwd);
		if (isMatch) {
			session.setAttribute("user_info", dbUser);
			return "redirect:/";
		} else {
			model.addAttribute("error", "이메일 또는 비밀번호가 일치하지 않습니다.");
			return "signin";
		}
	}

	@GetMapping("/signout")
	public String signout() {
		session.invalidate();
		return "redirect:/";
	}

	@GetMapping("/signup")
	public String signup() {
		return "signup";
	}

	@PostMapping("/signup")
	public String signupPost(@ModelAttribute User user) {
		String userPwd = user.getPwd();

		String encodePwd = passwordEncoder.encode(userPwd);
		user.setPwd(encodePwd);
		user.setCreDate(new Date());

		userRepository.save(user);
		return "redirect:/";
	}

	@GetMapping("/mypage")
	public String mypage(Model model, @RequestParam String email) {
		User opt = userRepository.findByEmail(email);
		model.addAttribute("user", opt);
		return "mypage";
	}

	@PostMapping("/mypage")
	public String updatePost(@ModelAttribute User user) {
		User sessionUser = (User) session.getAttribute("user_info");

		long id = sessionUser.getId();
		User dbUser = userRepository.findById(id);
		
		String userPwd = user.getPwd();
		String dbPwd = dbUser.getPwd();
		String encodedPwd = passwordEncoder.encode(userPwd);
		if (userPwd.equals(dbPwd)) {
			encodedPwd = userPwd;
		}
		user.setPwd(encodedPwd);
		user.setCreDate(new Date());
		userRepository.save(user);
		return "/signin";
	}

	@GetMapping("/loginnoti")
	public String loginnoti() {
		return "loginnoti";
	}
}