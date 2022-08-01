package org.kutaka.adminapi.validator;

import org.kutaka.adminapi.exception.MissingRequestParameterException;
import org.kutaka.adminapi.model.Novel;

public class NovelValidator {

  static public void validate(Novel novel) {
    if (novel.getIsHidden() == null) {
      throw new MissingRequestParameterException("isHidden cannot be null");
    }
    if (novel.getNameEn() == null) {
      throw new MissingRequestParameterException("nameEn cannot be null");
    }
    if (novel.getNameJa() == null) {
      throw new MissingRequestParameterException("nameJa cannot be null");
    }
  }
}
