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
import org.kutaka.adminapi.exception.FailedDbDataManipulationException;
import org.kutaka.adminapi.exception.ObjectNotFoundException;
import org.kutaka.adminapi.exception.FailedCloudinaryDataManipulationException;
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

import com.cloudinary.api.ApiResponse;
import com.mongodb.client.result.DeleteResult;

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

    List<Essay> mongoDbResult = mongoTemplate.find(query, Essay.class);
    List<String> bookTitles = new ArrayList<String>();
    mongoDbResult.forEach(result -> {
      String nameEn = result.getNameEn();
      if (bookTitles.contains(nameEn))
        return;
      bookTitles.add(nameEn);
    });
    Object images = bookHelper.getImagesFromCloudinary(bookTitles, Cloudinary.BOOK_TYPES.ESSAYS);

    LinkedHashMap<String, Object> response = new LinkedHashMap<>();
    response.put("book_data", mongoDbResult);
    response.put("book_img_data", images);

    return response;
  }

  public LinkedHashMap<String, Object> getEssay(String essayId) {
    Map<String, String> params = new HashMap<>();
    params.put(DbFields.ESSAY.ID, essayId);
    Query query = createQuery(params, new Query());
    Essay mongoDbResult = mongoTemplate.findOne(query, Essay.class);

    LinkedHashMap<String, Object> response = new LinkedHashMap<>();
    if (mongoDbResult == null) {
      response.put("book_data", null);
      response.put("book_img_data", null);

      return response;
    }

    List<String> titles = new ArrayList<String>();
    titles.add(mongoDbResult.getNameEn());
    Object images = bookHelper.getImagesFromCloudinary(titles, Cloudinary.BOOK_TYPES.ESSAYS);

    response.put("book_data", mongoDbResult);
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

    Essay mongoDbResult = null;
    Map cloudinaryCoverResult = null;
    ArrayList<Map> cloudinaryPagesResult = new ArrayList<>();

    // cover image
    try {
      cloudinaryCoverResult = bookHelper.uploadCoverImageToCloudinary(cover, Cloudinary.BOOK_TYPES.ESSAYS,
          essay.getNameEn());
    } catch (IOException e1) {
      throw new FailedCloudinaryDataManipulationException("Failed in uploading cover image");
    }

    // page images
    try {
      cloudinaryPagesResult = bookHelper.uploadPageImagesToCloudinary(files, Cloudinary.BOOK_TYPES.ESSAYS,
          essay.getNameEn());
    } catch (IOException e) {
      throw new FailedCloudinaryDataManipulationException("Failed in uploading page images");
    }

    essay = addAutoInsertData(essay);
    mongoDbResult = mongoTemplate.save(essay);
    if (mongoDbResult == null) {
      throw new FailedDbDataManipulationException("Failed in adding new entry");
    }

    LinkedHashMap<String, Object> response = new LinkedHashMap<>();
    response.put("book_data", mongoDbResult);
    response.put("book_cover_img_data", cloudinaryCoverResult);
    response.put("book_page_imgs_data", cloudinaryPagesResult);

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

    Essay mongoDbResult = null;
    Map cloudinaryCoverResult = null;
    ArrayList<Map> cloudinaryPagesResult = new ArrayList<Map>();

    // cover image
    if (cover != null) {
      try {
        cloudinaryCoverResult = bookHelper.uploadCoverImageToCloudinary(cover, Cloudinary.BOOK_TYPES.ESSAYS,
            essay.getNameEn());
      } catch (IOException e1) {
        throw new FailedCloudinaryDataManipulationException("Failed in uploading cover image");
      }
    }

    // page images
    if (pages != null) {
      for (int i = 0; i < pages.length; i++) {
        try {
          cloudinaryPagesResult = bookHelper.uploadPageImagesToCloudinary(pages, Cloudinary.BOOK_TYPES.ESSAYS,
              essay.getNameEn());
        } catch (IOException e) {
          throw new FailedCloudinaryDataManipulationException("Failed in uploading page images");
        }
      }
    }

    mongoDbResult = mongoTemplate.save(existingEssay);
    if (mongoDbResult == null) {
      throw new FailedDbDataManipulationException("Failed in updating existing entry with specified id");
    }

    LinkedHashMap<String, Object> response = new LinkedHashMap<>();
    response.put("book_data", mongoDbResult);
    response.put("book_cover_img_data", cloudinaryCoverResult);
    response.put("book_page_imgs_data", cloudinaryPagesResult);

    return response;
  }

  public LinkedHashMap<String, Object> deleteEssay(String essayId) {
    Map<String, String> params = new HashMap<>();
    params.put(DbFields.ESSAY.ID, essayId);
    Query query = createQuery(params, new Query());
    Essay targetBook = mongoTemplate.findOne(query, Essay.class);
    if (targetBook == null) {
      throw new ObjectNotFoundException("No entry with specified id exists");
    }

    String bookTitle = targetBook.getNameEn();
    DeleteResult mongoDbResult = mongoTemplate.remove(query, Essay.class);
    ApiResponse cloudinaryResult;
    try {
      cloudinaryResult = bookHelper.deleteImagesFromCloudinary(Cloudinary.BOOK_TYPES.ESSAYS, bookTitle);
    } catch (Exception e) {
      throw new FailedCloudinaryDataManipulationException("Failed in deleting images on Cloudinary");
    }

    LinkedHashMap<String, Object> response = new LinkedHashMap<>();
    response.put("book_data", mongoDbResult);
    response.put("book_img_data", cloudinaryResult);

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
