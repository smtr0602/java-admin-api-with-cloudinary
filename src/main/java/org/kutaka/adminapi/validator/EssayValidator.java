package org.kutaka.adminapi.validator;

import org.kutaka.adminapi.exception.MissingRequestParameterException;
import org.kutaka.adminapi.model.Essay;

public class EssayValidator {

  static public void validate(Essay essay) {
    if (essay.getIsHidden() == null) {
      throw new MissingRequestParameterException("isHidden cannot be null");
    }
    if (essay.getNameEn() == null) {
      throw new MissingRequestParameterException("nameEn cannot be null");
    }
    if (essay.getNameJa() == null) {
      throw new MissingRequestParameterException("nameJa cannot be null");
    }
  }
}
