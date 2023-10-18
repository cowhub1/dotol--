package com.example.board.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.board.model.Board;

public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findTop5ByOrderByIdDesc();

    Page<Board> findByTitleContaining(String keyword, Pageable pageable);
}