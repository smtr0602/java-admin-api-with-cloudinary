package org.kutaka.adminapi.controller;

import java.util.Map;
import java.util.TreeMap;
import org.kutaka.adminapi.model.Essay;
import org.kutaka.adminapi.service.EssayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class EssayController {

  @Autowired
  private EssayService essayService;

  @GetMapping(path = "/essays", produces = "application/json")
  public TreeMap<String, Object> getEssays(@RequestParam Map<String, String> params) {
    return essayService.getEssays(params);
  }

  @PostMapping(path = "/essays", consumes = "multipart/form-data", produces = "application/json")
  public Object addEssay(@ModelAttribute Essay essay, @RequestParam MultipartFile cover,
      @RequestParam MultipartFile[] page) {
    return essayService.addEssay(essay, cover, page);
  }
}
