package org.kutaka.adminapi.validator;

import org.kutaka.adminapi.model.Novel;

public class NovelValidator {

  static public void validate(Novel novel) {
    if (novel.getIsHidden() == null) {
      throw new IllegalArgumentException("isHidden cannot be null");
    }
    if (novel.getNameEn() == null) {
      throw new IllegalArgumentException("nameEn cannot be null!!");
    }
    if (novel.getNameJa() == null) {
      throw new IllegalArgumentException("nameJa cannot be null");
    }
  }
}
