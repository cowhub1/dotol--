package com.example.board.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;

@Entity
@Data
public class FileAtch {
    @Id
    @GeneratedValue
    int id;
    String originalName;
    String saveName;

    @ManyToOne
    Board board;
}
