package org.kutaka.adminapi.controller;

import java.util.Map;
import java.util.TreeMap;
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
  public TreeMap<String, Object> getNovels(@RequestParam Map<String, String> params) {
    return novelService.getNovels(params);
  }
}
