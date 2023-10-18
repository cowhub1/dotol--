package com.example.board.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.board.model.Board;
import com.example.board.repository.BoardRepository;

@Controller
public class HomeController {

	@Autowired
	private BoardRepository boardRepository;

	@GetMapping({ "/", "/home" })
	public String index(Model model) {
		List<Board> latestPosts = boardRepository.findTop5ByOrderByIdDesc();
		model.addAttribute("latestPosts", latestPosts);
		return "index";
	}

}
