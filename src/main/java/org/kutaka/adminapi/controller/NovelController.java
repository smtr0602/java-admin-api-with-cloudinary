package org.kutaka.adminapi.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.kutaka.adminapi.model.Novel;
import org.kutaka.adminapi.service.NovelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin
@RestController
public class NovelController {

  @Autowired
  private NovelService novelService;

  @GetMapping(path = "/novels", produces = "application/json")
  public LinkedHashMap<String, Object> getNovels(@RequestParam Map<String, String> params) {
    return novelService.getNovels(params);
  }

  @GetMapping(path = "/novels/{novelId}", produces = "application/json")
  public LinkedHashMap<String, Object> getNovel(@PathVariable String novelId) {
    return novelService.getNovel(novelId);
  }

  @PostMapping(path = "/novels", consumes = "multipart/form-data", produces = "application/json")
  public LinkedHashMap<String, Object> addNovel(@ModelAttribute Novel novel, @RequestParam MultipartFile cover,
      @RequestParam MultipartFile[] pages) {
    return novelService.addNovel(novel, cover, pages);
  }

  @RequestMapping(value = "/novels/{novelId}", method = {
      RequestMethod.PUT }, consumes = "multipart/form-data", produces = "application/json")
  public LinkedHashMap<String, Object> updateNovel(@PathVariable String novelId, @ModelAttribute Novel novel,
      @RequestParam(required = false) MultipartFile cover,
      @RequestParam(required = false) MultipartFile[] pages) {
    return novelService.updateNovel(novelId, novel, cover, pages);
  }

  @RequestMapping(value = "/novels/{novelId}", method = { RequestMethod.DELETE }, produces = "application/json")
  public LinkedHashMap<String, Object> deleteNovel(@PathVariable String novelId, @ModelAttribute Novel novel) {
    return novelService.deleteNovel(novelId);
  }
}
