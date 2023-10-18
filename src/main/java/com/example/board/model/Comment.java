package com.example.board.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;

@Entity
@Data
public class Comment {
    @Id @GeneratedValue
    int id;

    String content;
    String writer;
    Date creDate;

    @ManyToOne Board board;
}
