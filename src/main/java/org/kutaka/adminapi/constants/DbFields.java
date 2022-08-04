package org.kutaka.adminapi.constants;

public interface DbFields {

  interface USER {
    String ID = "_id";
    String USER_NAME = "username";
    String PASSWORD = "password";
  }

  interface NOVEL {
    String ID = "_id";
    String IS_HIDDEN = "isHidden";
    String NAME_EN = "nameEn";
    String NAME_JA = "nameJa";
    String ORDER = "order";
    String CATEGORIES = "categories";
    String TAGS = "tags";
  }

  interface ESSAY {
    String ID = "_id";
    String IS_HIDDEN = "isHidden";
    String NAME_EN = "nameEn";
    String NAME_JA = "nameJa";
    String ORDER = "order";
    String CATEGORIES = "categories";
    String TAGS = "tags";
  }
}
