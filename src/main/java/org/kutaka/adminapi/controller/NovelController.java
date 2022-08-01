package org.kutaka.adminapi.controller;

import java.util.Map;
import java.util.TreeMap;
import org.kutaka.adminapi.model.Novel;
import org.kutaka.adminapi.service.NovelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class NovelController {

  @Autowired
  private NovelService novelService;

  @GetMapping(path = "/novels", produces = "application/json")
  public TreeMap<String, Object> getNovels(@RequestParam Map<String, String> params) {
    return novelService.getNovels(params);
  }

  @PostMapping(path = "/novels", consumes = "multipart/form-data", produces = "application/json")
  public Object addNovel(@ModelAttribute Novel novel, @RequestParam MultipartFile cover,
      @RequestParam MultipartFile[] page) {
    return novelService.addNovel(novel, cover, page);
  }
}
