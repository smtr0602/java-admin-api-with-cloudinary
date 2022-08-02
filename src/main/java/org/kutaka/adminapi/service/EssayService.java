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

  public LinkedHashMap<String, Object> getEssays(Map<String, String> params) {
    Query query = new Query();
    if (!params.isEmpty()) {
      query = createQuery(params, query);
    }
    query.with(Sort.by(Sort.Direction.DESC, DbFields.ESSAY.ORDER));

    List<Essay> results = mongoTemplate.find(query, Essay.class);
    List<String> uniqueNames = new ArrayList<String>();
    results.forEach(result -> {
      String nameEn = result.getNameEn();
      if (uniqueNames.contains(nameEn))
        return;
      uniqueNames.add(nameEn);
    });
    Object images = bookHelper.getImagesFromCloudinary(uniqueNames, Cloudinary.BOOK_TYPES.ESSAYS);

    LinkedHashMap<String, Object> response = new LinkedHashMap<>();
    response.put("book_data", results);
    response.put("book_img_data", images);

    return response;
  }

  public LinkedHashMap<String, Object> getEssay(String essayId) {
    Map<String, String> params = new HashMap<>();
    params.put(DbFields.ESSAY.ID, essayId);
    Query query = createQuery(params, new Query());
    Essay bookData = mongoTemplate.findOne(query, Essay.class);

    LinkedHashMap<String, Object> response = new LinkedHashMap<>();
    if (bookData == null) {
      response.put("book_data", null);
      response.put("book_img_data", null);

      return response;
    }

    List<String> titles = new ArrayList<String>();
    titles.add(bookData.getNameEn());
    Object images = bookHelper.getImagesFromCloudinary(titles, Cloudinary.BOOK_TYPES.ESSAYS);

    response.put("book_data", bookData);
    response.put("book_img_data", images);

    return response;
  }

  public LinkedHashMap<String, Object> addEssay(Essay essay, MultipartFile cover, MultipartFile[] files) {
    EssayValidator.validate(essay);

    // check if already exists
    Map<String, String> param = new HashMap<>();
    param.put(DbFields.ESSAY.NAME_EN, essay.getNameEn());
    Query query = createQuery(param, new Query());
    boolean doesExist = mongoTemplate.find(query, Essay.class).size() > 0;
    if (doesExist) {
      throw new AlreadyExistsException("Item with specified name already exists");
    }

    Essay mongoDbRes = null;
    Map cloudinaryCoverRes = null;
    ArrayList<Map> cloudinaryPagesRes = new ArrayList<>();

    // cover image
    try {
      cloudinaryCoverRes = bookHelper.uploadCoverImageToCloudinary(cover, Cloudinary.BOOK_TYPES.ESSAYS,
          essay.getNameEn());
    } catch (IOException e1) {
      throw new RuntimeException("Failed in uploading cover image");
    }

    // page images
    try {
      cloudinaryPagesRes = bookHelper.uploadPageImagesToCloudinary(files, Cloudinary.BOOK_TYPES.ESSAYS,
          essay.getNameEn());
    } catch (IOException e) {
      throw new RuntimeException("Failed in uploading page images");
    }

    essay = addAutoInsertData(essay);
    mongoDbRes = mongoTemplate.save(essay);

    LinkedHashMap<String, Object> response = new LinkedHashMap<>();
    response.put("book_data", mongoDbRes);
    response.put("book_cover_img_data", cloudinaryCoverRes);
    response.put("book_page_imgs_data", cloudinaryPagesRes);

    return response;
  }

  public LinkedHashMap<String, Object> updateEssay(String essayId, Essay essay, MultipartFile cover,
      MultipartFile[] pages) {
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

    Essay mongoDbRes = null;
    Map cloudinaryCoverRes = null;
    ArrayList<Map> cloudinaryPagesRes = new ArrayList<Map>();

    // cover image
    if (cover != null) {
      try {
        cloudinaryCoverRes = bookHelper.uploadCoverImageToCloudinary(cover, Cloudinary.BOOK_TYPES.ESSAYS,
            essay.getNameEn());
      } catch (IOException e1) {
        throw new RuntimeException("Failed in uploading cover image");
      }
    }

    // page images
    if (pages != null) {
      for (int i = 0; i < pages.length; i++) {
        try {
          cloudinaryPagesRes = bookHelper.uploadPageImagesToCloudinary(pages, Cloudinary.BOOK_TYPES.ESSAYS,
              essay.getNameEn());
        } catch (IOException e) {
          throw new RuntimeException("Failed in uploading page images");
        }
      }
    }

    mongoDbRes = mongoTemplate.save(existingEssay);

    LinkedHashMap<String, Object> response = new LinkedHashMap<>();
    response.put("book_data", mongoDbRes);
    response.put("book_cover_img_data", cloudinaryCoverRes);
    response.put("book_page_imgs_data", cloudinaryPagesRes);

    return response;
  }

  private Query createQuery(Map<String, String> params, Query query) {
    params.forEach((key, value) -> {
      if (key.equals(DbFields.ESSAY.ID)) {
        query.addCriteria(Criteria.where(DbFields.ESSAY.ID).is(value));
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
