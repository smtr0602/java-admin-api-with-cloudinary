package org.kutaka.adminapi.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.kutaka.adminapi.constants.DbFields;
import org.kutaka.adminapi.exception.AlreadyExistsException;
import org.kutaka.adminapi.helper.BookHelper;
import org.kutaka.adminapi.constants.Cloudinary;
import org.kutaka.adminapi.model.Essay;
import org.kutaka.adminapi.validator.EssayValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class EssayService {

  @Autowired
  private MongoTemplate mongoTemplate;
  private BookHelper bookHelper;

  public EssayService() {
    this.bookHelper = new BookHelper();
  }

  public TreeMap<String, Object> getEssays(Map<String, String> params) {
    Query query = new Query();
    if (!params.isEmpty()) {
      query = createQuery(params, query);
    }
    query.with(Sort.by(Sort.Direction.DESC, DbFields.ESSAY.ORDER));

    TreeMap<String, Object> response = new TreeMap<>();
    List<Essay> results = mongoTemplate.find(query, Essay.class);
    List<String> uniqueNames = new ArrayList<String>();
    results.forEach(result -> {
      String nameEn = result.getNameEn();
      if (uniqueNames.contains(nameEn))
        return;
      uniqueNames.add(nameEn);
    });
    Object images = bookHelper.getImagesFromCloudinary(uniqueNames, Cloudinary.BOOK_TYPES.ESSAYS);
    response.put("book_data", results);
    response.put("book_img_data", images);

    return response;
  }

  public Essay addEssay(Essay essay, MultipartFile cover, MultipartFile[] files) {
    EssayValidator.validate(essay);

    // check if already exists
    Map<String, String> param = new HashMap<>();
    param.put(DbFields.ESSAY.NAME_EN, essay.getNameEn());
    Query query = createQuery(param, new Query());
    boolean doesExist = mongoTemplate.find(query, Essay.class).size() > 0;
    if (doesExist) {
      throw new AlreadyExistsException("Item with specified name already exists");
    }

    // cover image
    try {
      bookHelper.uploadCoverImageToCloudinary(cover, Cloudinary.BOOK_TYPES.ESSAYS, essay.getNameEn());
    } catch (IOException e1) {
      throw new RuntimeException("Failed in uploading cover image");
    }

    // page images
    for (int i = 0; i < files.length; i++) {
      try {
        bookHelper.uploadPageImagesToCloudinary(files, Cloudinary.BOOK_TYPES.ESSAYS, essay.getNameEn());
      } catch (IOException e) {
        throw new RuntimeException("Failed in uploading page images");
      }
    }

    essay = addAutoInsertData(essay);

    return mongoTemplate.insert(essay);
  }

  public Essay updateEssay(String essayId, Essay essay, MultipartFile cover, MultipartFile[] page) {
    EssayValidator.validate(essay);

    // retrieve existing document & update
    Map<String, String> param = new HashMap<>();
    param.put(DbFields.ESSAY.ID, essayId);
    Query query = createQuery(param, new Query());
    Essay existingEssay = mongoTemplate.findOne(query, Essay.class);

    existingEssay.setIsHidden(essay.getIsHidden());
    existingEssay.setNameEn(essay.getNameEn());
    existingEssay.setNameJa(essay.getNameJa());
    existingEssay.setCategories(essay.getCategories());
    existingEssay.setTags(essay.getTags());
    existingEssay.setUpdatedAt(new Date());

    // cover image
    if (cover != null) {
      try {
        bookHelper.uploadCoverImageToCloudinary(cover, Cloudinary.BOOK_TYPES.ESSAYS, essay.getNameEn());
      } catch (IOException e1) {
        throw new RuntimeException("Failed in uploading cover image");
      }
    }

    // page images
    if (page != null) {
      try {
        bookHelper.uploadPageImagesToCloudinary(page, Cloudinary.BOOK_TYPES.ESSAYS, essay.getNameEn());
      } catch (IOException e) {
        throw new RuntimeException("Failed in uploading page images");
      }

    }

    return mongoTemplate.save(existingEssay);
  }

  private Query createQuery(Map<String, String> params, Query query) {
    params.forEach((key, value) -> {
      if (key.equals(DbFields.NOVEL.ID)) {
        query.addCriteria(Criteria.where(DbFields.NOVEL.ID).is(value));
      }
      if (key.equals(DbFields.ESSAY.IS_HIDDEN)) {
        Boolean isTrue = value.equals("true");
        query.addCriteria(Criteria.where(DbFields.ESSAY.IS_HIDDEN).is(isTrue));
      }
      if (key.equals(DbFields.ESSAY.NAME_EN)) {
        query.addCriteria(Criteria.where(DbFields.ESSAY.NAME_EN).regex(value));
      }
      if (key.equals(DbFields.ESSAY.NAME_JA)) {
        query.addCriteria(Criteria.where(DbFields.ESSAY.NAME_JA).regex(value));
      }
      if (key.equals(DbFields.ESSAY.CATEGORIES)) {
        Object[] nums = value.split(",");
        query.addCriteria(Criteria.where(DbFields.ESSAY.CATEGORIES).all(nums));
      }
      if (key.equals(DbFields.ESSAY.TAGS)) {
        Object[] nums = value.split(",");
        query.addCriteria(Criteria.where(DbFields.ESSAY.TAGS).all(nums));
      }
    });

    return query;
  }

  private Essay addAutoInsertData(Essay essay) {
    Query query = new Query();
    query.with(Sort.by(Sort.Direction.DESC, DbFields.ESSAY.ORDER)).limit(1);
    int maxOrder = mongoTemplate.find(query, Essay.class).get(0).getOrder();

    essay.setOrder(maxOrder + 1);
    essay.setUpdatedAt(new Date());
    essay.setCreatedAt(new Date());

    return essay;
  }
}
