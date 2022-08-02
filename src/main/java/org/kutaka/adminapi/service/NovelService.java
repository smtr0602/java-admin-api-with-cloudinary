package org.kutaka.adminapi.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.kutaka.adminapi.constants.DbFields;
import org.kutaka.adminapi.exception.AlreadyExistsException;
import org.kutaka.adminapi.helper.BookHelper;
import org.kutaka.adminapi.constants.Cloudinary;
import org.kutaka.adminapi.model.Novel;
import org.kutaka.adminapi.validator.NovelValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class NovelService {

  @Autowired
  private MongoTemplate mongoTemplate;
  private BookHelper bookHelper;

  public NovelService() {
    this.bookHelper = new BookHelper();
  }

  public LinkedHashMap<String, Object> getNovels(Map<String, String> params) {
    Query query = new Query();
    if (!params.isEmpty()) {
      query = createQuery(params, query);
    }
    query.with(Sort.by(Sort.Direction.DESC, DbFields.NOVEL.ORDER));

    List<Novel> results = mongoTemplate.find(query, Novel.class);
    List<String> uniqueNames = new ArrayList<String>();
    results.forEach(result -> {
      String nameEn = result.getNameEn();
      if (uniqueNames.contains(nameEn))
        return;
      uniqueNames.add(nameEn);
    });
    Object images = bookHelper.getImagesFromCloudinary(uniqueNames, Cloudinary.BOOK_TYPES.NOVELS);

    LinkedHashMap<String, Object> response = new LinkedHashMap<>();
    response.put("book_data", results);
    response.put("book_img_data", images);

    return response;
  }

  public LinkedHashMap<String, Object> addNovel(Novel novel, MultipartFile cover, MultipartFile[] files) {
    NovelValidator.validate(novel);

    // check if already exists
    Map<String, String> param = new HashMap<>();
    param.put(DbFields.NOVEL.NAME_EN, novel.getNameEn());
    Query query = createQuery(param, new Query());
    boolean doesExist = mongoTemplate.find(query, Novel.class).size() > 0;
    if (doesExist) {
      throw new AlreadyExistsException("Item with specified name already exists");
    }

    Novel mongoDbRes = null;
    Map cloudinaryCoverRes = null;
    ArrayList<Map> cloudinaryPagesRes = null;

    // cover image
    try {
      cloudinaryCoverRes = bookHelper.uploadCoverImageToCloudinary(cover, Cloudinary.BOOK_TYPES.NOVELS,
          novel.getNameEn());
    } catch (IOException e1) {
      throw new RuntimeException("Failed in uploading cover image");
    }

    // page images
    try {
      cloudinaryPagesRes = bookHelper.uploadPageImagesToCloudinary(files, Cloudinary.BOOK_TYPES.NOVELS,
          novel.getNameEn());
    } catch (IOException e) {
      throw new RuntimeException("Failed in uploading page images");
    }

    novel = addAutoInsertData(novel);
    mongoDbRes = mongoTemplate.save(novel);

    LinkedHashMap<String, Object> response = new LinkedHashMap<>();
    response.put("book_data", mongoDbRes);
    response.put("book_cover_img_data", cloudinaryCoverRes);
    response.put("book_page_imgs_data", cloudinaryPagesRes);

    return response;
  }

  public LinkedHashMap<String, Object> updateNovel(String novelId, Novel novel, MultipartFile cover,
      MultipartFile[] pages) {
    NovelValidator.validate(novel);

    // retrieve existing document & update
    Map<String, String> param = new HashMap<>();
    param.put(DbFields.NOVEL.ID, novelId);
    Query query = createQuery(param, new Query());
    Novel existingNovel = mongoTemplate.findOne(query, Novel.class);

    existingNovel.setIsHidden(novel.getIsHidden());
    existingNovel.setNameEn(novel.getNameEn());
    existingNovel.setNameJa(novel.getNameJa());
    existingNovel.setCategories(novel.getCategories());
    existingNovel.setTags(novel.getTags());
    existingNovel.setUpdatedAt(new Date());

    Novel mongoDbRes = null;
    Map cloudinaryCoverRes = null;
    ArrayList<Map> cloudinaryPagesRes = null;

    // cover image
    if (cover != null) {
      try {
        cloudinaryCoverRes = bookHelper.uploadCoverImageToCloudinary(cover, Cloudinary.BOOK_TYPES.NOVELS,
            novel.getNameEn());
      } catch (IOException e1) {
        throw new RuntimeException("Failed in uploading cover image");
      }
    }

    // page images
    if (pages != null) {
      for (int i = 0; i < pages.length; i++) {
        try {
          cloudinaryPagesRes = bookHelper.uploadPageImagesToCloudinary(pages, Cloudinary.BOOK_TYPES.NOVELS,
              novel.getNameEn());
        } catch (IOException e) {
          throw new RuntimeException("Failed in uploading page images");
        }
      }
    }

    mongoDbRes = mongoTemplate.save(existingNovel);

    LinkedHashMap<String, Object> response = new LinkedHashMap<>();
    response.put("book_data", mongoDbRes);
    response.put("book_cover_img_data", cloudinaryCoverRes);
    response.put("book_page_imgs_data", cloudinaryPagesRes);

    return response;
  }

  private Query createQuery(Map<String, String> params, Query query) {
    params.forEach((key, value) -> {
      if (key.equals(DbFields.NOVEL.ID)) {
        query.addCriteria(Criteria.where(DbFields.NOVEL.ID).is(value));
      }
      if (key.equals(DbFields.NOVEL.IS_HIDDEN)) {
        Boolean isTrue = value.equals("true");
        query.addCriteria(Criteria.where(DbFields.NOVEL.IS_HIDDEN).is(isTrue));
      }
      if (key.equals(DbFields.NOVEL.NAME_EN)) {
        query.addCriteria(Criteria.where(DbFields.NOVEL.NAME_EN).regex(value));
      }
      if (key.equals(DbFields.NOVEL.NAME_JA)) {
        query.addCriteria(Criteria.where(DbFields.NOVEL.NAME_JA).regex(value));
      }
      if (key.equals(DbFields.NOVEL.CATEGORIES)) {
        Object[] nums = value.split(",");
        query.addCriteria(Criteria.where(DbFields.NOVEL.CATEGORIES).all(nums));
      }
      if (key.equals(DbFields.NOVEL.TAGS)) {
        Object[] nums = value.split(",");
        query.addCriteria(Criteria.where(DbFields.NOVEL.TAGS).all(nums));
      }
    });

    return query;
  }

  private Novel addAutoInsertData(Novel novel) {
    Query query = new Query();
    query.with(Sort.by(Sort.Direction.DESC, DbFields.NOVEL.ORDER)).limit(1);
    int maxOrder = mongoTemplate.find(query, Novel.class).get(0).getOrder();

    novel.setOrder(maxOrder + 1);
    novel.setUpdatedAt(new Date());
    novel.setCreatedAt(new Date());

    return novel;
  }
}
