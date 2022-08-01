package org.kutaka.adminapi.constants;

public interface Cloudinary {

  interface SEARCH_PARAMS {
    int MAX_RESULT = 500;
  }

  interface BOOK_TYPES {
    String NOVELS = "novels";
    String ESSAYS = "essays";
  }

  interface FOLDER_NAMES {
    String ROOT = "kutaka";
    String PAGES = "pages";
  }

  interface FILE_NAMES {
    String COVER = "cover";
  }
}
