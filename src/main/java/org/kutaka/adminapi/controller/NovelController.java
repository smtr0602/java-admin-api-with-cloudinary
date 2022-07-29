package org.kutaka.adminapi.controller;

import java.util.List;
import java.util.Map;
import org.kutaka.adminapi.model.Novel;
import org.kutaka.adminapi.service.NovelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NovelController {

  @Autowired
  private NovelService novelService;

  @GetMapping("/novels")
  public List<Novel> getNovels(@RequestParam Map<String, String> params) {
    return novelService.getNovels(params);
  }
}
