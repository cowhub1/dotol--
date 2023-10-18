package com.example.board.controller;

import java.util.List;

import com.example.board.model.Point;
import com.example.board.repository.PointRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MapController {
   @Autowired
   PointRepository pointRepository;


   @GetMapping("/map")
   public String map(Model model) {
      Sort sort = Sort.by(Order.desc("id"));
      List<Point> points = pointRepository.findAll(sort);
      model.addAttribute("points", points);
      return "map/map";
   }

   @PostMapping("/map")
   public String mapPost(@ModelAttribute Point point) {
      pointRepository.save(point);
      return "redirect:/map";
   }
   //페이지네이션해보기
   @GetMapping("/map/search")
   public String search(Model model,
      @RequestParam(defaultValue = "1") int P) {
      Sort sort = Sort.by(Order.desc("id"));
      Pageable pageable = PageRequest.of(P - 1, 10, sort);
      
      Page<Point> page = pointRepository.findAll(pageable);
      List<Point> points = page.getContent();

      model.addAttribute("points", points);

      int totalPages = page.getTotalPages();

      // calculate start and end page
      int startPageGroup = ((P - 1) / 10) * 10;
      int startPage = Math.max(1, startPageGroup + 1);
      int endPage = Math.min(totalPages, startPageGroup + 10);

      model.addAttribute("startPage", startPage);
      model.addAttribute("endPage", endPage);

      // calculate previous and next group starting pages
      model.addAttribute("prevGroupStart", Math.max(1, startPage - 10));
      model.addAttribute("nextGroupStart", Math.min(totalPages, startPage + 10));

      model.addAttribute("totalPages", totalPages);

      return "map/search";
   }
   // 기존꺼
   // @GetMapping("/map/search")
   // public String search(Model model) {
   //    Sort sort = Sort.by(Order.desc("id"));
            
   //    List<Point> points = pointRepository.findAll(sort);
   //    model.addAttribute("points", points);
   //    return "map/search";
   // }

   @PostMapping("/map/search")
   public String searchPost() {
      return "redirect:/map/search";
   }

   @GetMapping("/map/delete/{id}")
   public String boardDelete(@PathVariable("id") long id) {
      pointRepository.deleteById(id);
      return "redirect:/map";

   }

   @GetMapping("/map/where")
   public String where() {
      return "/map/where";
   }

}