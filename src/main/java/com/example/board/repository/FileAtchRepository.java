package com.example.board.repository;

import com.example.board.model.Board;
import com.example.board.model.FileAtch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileAtchRepository extends JpaRepository<FileAtch, Integer> {
    List<FileAtch> findByBoard(Board board);
}