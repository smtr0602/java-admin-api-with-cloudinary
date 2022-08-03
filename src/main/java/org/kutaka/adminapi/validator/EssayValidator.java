package org.kutaka.adminapi.validator;

import org.kutaka.adminapi.model.Essay;

public class EssayValidator {

  static public void validate(Essay essay) {
    if (essay.getIsHidden() == null) {
      throw new IllegalArgumentException("isHidden cannot be null");
    }
    if (essay.getNameEn() == null) {
      throw new IllegalArgumentException("nameEn cannot be null");
    }
    if (essay.getNameJa() == null) {
      throw new IllegalArgumentException("nameJa cannot be null");
    }
  }
}
