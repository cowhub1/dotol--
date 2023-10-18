package com.example.board.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import com.example.board.model.Board;
import com.example.board.model.Comment;
import com.example.board.model.FileAtch;
import com.example.board.model.User;
import com.example.board.repository.BoardRepository;
import com.example.board.repository.CommentRepository;
import com.example.board.repository.FileAtchRepository;

@Controller
public class BoardController {
	@Autowired
	BoardRepository boardRepository;

	@Autowired
	CommentRepository commentRepository;

	@Autowired
	FileAtchRepository fileAtchRepository;

	@Autowired
	HttpSession session;

	@GetMapping("/board/image")
	public String image() {
		return "/board/image";
	}

	@GetMapping("/board/delete/{id}")
	public String boardDelete(@PathVariable("id") long id) {
		User loggedUser = (User) session.getAttribute("user_info");
		String loggedName = loggedUser.getEmail();
		Optional<Board> dbBoard = boardRepository.findById(id);
		String savedName = dbBoard.get().getUserId();

		if (savedName.equals(loggedName)) {
			Board board = new Board();
			board.setId(id);
			boardRepository.deleteById(id);
			return "redirect:/board/list";
		} else {

			return "redirect:/board/view?id=" + id;
		}
	}

	@GetMapping("/board/update/{id}")
	public String boardUpdate(Model model, @PathVariable("id") long id) {
		Optional<Board> data = boardRepository.findById(id);
		Board board = data.get();
		model.addAttribute("board", board);
		return "board/update";
	}

	@PostMapping("/board/update/{id}")
	public String boardUpdate(
			@ModelAttribute Board board,
			@RequestParam("file") MultipartFile mFile,
			@PathVariable("id") long id) {

		User user = (User) session.getAttribute("user_info");
		if (user == null) {
			return "redirect:/login";
		}
		String userId = user.getEmail();

		Optional<Board> data = boardRepository.findById(id);

		if (data.isPresent()) {
			Board existingBoard = data.get();

			if (!userId.equals(existingBoard.getUserId())) {
				// 사용자가 게시물의 작성자가 아닌 경우 처리할 내용
				return "redirect:/board/view?id=" + id; // 예: 게시물 보기 페이지로 리디렉션
			}

			// 게시물 수정 권한이 있는 경우, 업데이트 진행
			existingBoard.setTitle(board.getTitle());
			existingBoard.setContent(board.getContent());

			// 파일 저장 로직 시작
			String originalFilename = mFile.getOriginalFilename();
			FileAtch fileAtch = new FileAtch();
			fileAtch.setOriginalName(originalFilename);
			fileAtch.setSaveName(originalFilename);
			fileAtch.setBoard(existingBoard);

			fileAtchRepository.save(fileAtch);

			String filename = mFile.getOriginalFilename();

			File file = new File("C:/files/" + filename);
			try {
				mFile.transferTo(file);
			} catch (IllegalStateException | IOException e) {
				e.printStackTrace();
			}
			// 파일 저장 로직 끝

			boardRepository.save(existingBoard);
		}

		return "redirect:/board/" + id;
	}

	@GetMapping("/board/{id}")
	public String boardView(Model model, @PathVariable("id") long id) {
		Optional<Board> data = boardRepository.findById(id);
		Board board = data.get();
		model.addAttribute("board", board);
		return "board/view";
	}

	@GetMapping("/board/list")
	public String boardList(Model model,
			@RequestParam(required = false) String keyword,
			@RequestParam(defaultValue = "1") int P) {
		Sort sort = Sort.by(Order.desc("id"));
		Pageable pageable = PageRequest.of(P - 1, 10, sort);
		// Page<Board> list = boardRepository.findAll(pageable);

		Page<Board> page;
		if (keyword != null && !keyword.isEmpty()) {
			page = boardRepository.findByTitleContaining(keyword, pageable);
		} else {
			page = boardRepository.findAll(pageable);
		}

		List<Board> list = page.getContent();

		model.addAttribute("list", list);

		int startPageGroup = ((P - 1) / 10) * 10;

		int totalPages = page.getTotalPages();

		// calculate end page
		int startPage = Math.max(1, startPageGroup + 1);
		int endPage = Math.min(totalPages, startPageGroup + 10);

		model.addAttribute("startPage", startPage);
		model.addAttribute("endPage", endPage);
		model.addAttribute("prevGroupStart", Math.max(1, startPage - 10));
		model.addAttribute("nextGroupStart", Math.min(totalPages, startPage + 10));
		model.addAttribute("totalPages", totalPages);

		return "board/list";
	}

	@GetMapping("/board/write")
	public String boardWrite() {

		return "board/write";
	}

	@PostMapping("/board/write")
	@Transactional(rollbackFor = { ArithmeticException.class })
	public String boardWrite(
			@ModelAttribute Board board,
			@RequestParam("file") MultipartFile[] mFiles) {

		Board saveBoard = boardRepository.save(board);

		User user = (User) session.getAttribute("user_info");
		String userId = user.getEmail();
		board.setUserId(userId);

		for (MultipartFile mFile : mFiles) {
			String originalFilename = mFile.getOriginalFilename();

			FileAtch fileAtch = new FileAtch();
			fileAtch.setOriginalName(originalFilename);
			fileAtch.setSaveName(originalFilename);
			fileAtch.setBoard(saveBoard);

			fileAtchRepository.save(fileAtch);

			String filename = mFile.getOriginalFilename();

			File file = new File("C:/files/" + filename);
			try {
				mFile.transferTo(file);
			} catch (IllegalStateException | IOException e) {
				e.printStackTrace();
			}
		}

		boardRepository.save(board);

		return "redirect:/board/list";
	}

	@PostMapping("/board/comment")
	public String comment(@ModelAttribute Comment comment, @RequestParam int boardId) {
		User user = (User) session.getAttribute("user_info");
		String name;

		if (user != null) {
			name = user.getName(); // 로그인한 사용자의 이름을 가져옴
		} else {
			name = "Anonymous"; // 로그인하지 않은 경우에는 "Anonymous"
		}

		comment.setWriter(name);
		comment.setWriter(name);
		comment.setCreDate(new Date());

		Board board = new Board();
		board.setId(boardId);
		comment.setBoard(board);

		commentRepository.save(comment);

		return "redirect:/board/view?id=" + boardId;
	}

	@GetMapping("/board/comment/remove")
	public String commentRemove(@ModelAttribute Comment comment, @RequestParam int boardId) {
		// 1번 new Comment(), setId()
		// 2번 @ModelAttribute Comment comment
		commentRepository.delete(comment);
		return "redirect:/board/view?id=" + boardId;
	}

	@GetMapping("/board/fileAtch/remove")
	public String fileAtchRemove(@ModelAttribute FileAtch fileAtch, @RequestParam int boardId) {
		// 1번 new Comment(), setId()
		// 2번 @ModelAttribute Comment comment
		fileAtchRepository.delete(fileAtch);
		return "redirect:/board/view?id=" + boardId;
	}

	@GetMapping("/board/view")
	public String view(Model model, @RequestParam long id) {
		Optional<Board> opt = boardRepository.findById(id);
		model.addAttribute("board", opt.get());
		return "board/view";
	}

}
