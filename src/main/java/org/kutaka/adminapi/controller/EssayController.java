package org.kutaka.adminapi.controller;

import java.util.LinkedHashMap;
import java.util.Map;
import org.kutaka.adminapi.model.Essay;
import org.kutaka.adminapi.service.EssayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class EssayController {

  @Autowired
  private EssayService essayService;

  @GetMapping(path = "/essays", produces = "application/json")
  public LinkedHashMap<String, Object> getEssays(@RequestParam Map<String, String> params) {
    return essayService.getEssays(params);
  }

  @GetMapping(path = "/essays/{essayId}", produces = "application/json")
  public LinkedHashMap<String, Object> getEssay(@PathVariable String essayId) {
    return essayService.getEssay(essayId);
  }

  @PostMapping(path = "/essays", consumes = "multipart/form-data", produces = "application/json")
  public LinkedHashMap<String, Object> addEssay(@ModelAttribute Essay essay, @RequestParam MultipartFile cover,
      @RequestParam MultipartFile[] pages) {
    return essayService.addEssay(essay, cover, pages);
  }

  @RequestMapping(value = "/essays/{essayId}", method = {
      RequestMethod.PUT }, consumes = "multipart/form-data", produces = "application/json")
  public LinkedHashMap<String, Object> updateEssay(@PathVariable String essayId, @ModelAttribute Essay essay,
      @RequestParam(required = false) MultipartFile cover,
      @RequestParam(required = false) MultipartFile[] pages) {
    return essayService.updateEssay(essayId, essay, cover, pages);
  }

  @RequestMapping(value = "/essays/{essayId}", method = { RequestMethod.DELETE }, produces = "application/json")
  public LinkedHashMap<String, Object> deleteEssay(@PathVariable String essayId, @ModelAttribute Essay essay) {
    return essayService.deleteEssay(essayId);
  }
}
