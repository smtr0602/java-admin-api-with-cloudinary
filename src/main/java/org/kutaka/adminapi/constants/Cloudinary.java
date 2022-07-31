package org.kutaka.adminapi.constants;

public interface Cloudinary {

  interface SEARCH_PARAMS {
    int MAX_RESULT = 500;
  }

  interface PATHS {
    String ROOT = "kutaka";
  }

  interface BOOK_TYPES {
    String NOVELS = "novels";
    String ESSAYS = "essays";
  }

  interface FOLDER_NAMES {
    String PAGES = "pages";
  }

  interface FILE_NAMES {
    String COVER = "cover";
  }
}
