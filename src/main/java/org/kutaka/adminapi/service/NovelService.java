package org.kutaka.adminapi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.kutaka.adminapi.config.CloudinaryConfig;
import org.kutaka.adminapi.constants.DbFields;
import org.kutaka.adminapi.constants.Cloudinary;
import org.kutaka.adminapi.model.Novel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import com.cloudinary.api.ApiResponse;

@Service
public class NovelService {

  @Autowired
  private MongoTemplate mongoTemplate;
  private CloudinaryConfig cloudinaryConfig;

  public NovelService() {
    this.cloudinaryConfig = CloudinaryConfig.getInstance();
  }

  public TreeMap<String, Object> getNovels(Map<String, String> params) {
    Query query = new Query();
    if (!params.isEmpty()) {
      query = createQuery(params, query);
    }
    query.with(Sort.by(Sort.Direction.DESC, DbFields.NOVEL.ORDER));

    TreeMap<String, Object> response = new TreeMap<>();
    List<Novel> results = mongoTemplate.find(query, Novel.class);
    List<String> uniqueNames = new ArrayList<String>();
    results.forEach(result -> {
      String nameEn = result.getNameEn();
      if (uniqueNames.contains(nameEn))
        return;
      uniqueNames.add(nameEn);
    });
    Object images = getNovelImages(uniqueNames);
    response.put("book_data", results);
    response.put("book_img_data", images);

    return response;
  }

  /**
   * Fetch image data from Cloudinary based on MongoDB results
   */
  public Object getNovelImages(List<String> folders) {
    if (folders.isEmpty())
      return new ArrayList<>();

    ApiResponse cloudinaryRes = null;
    String query = "";
    for (int i = 0; i < folders.size(); i++) {
      if (i == 0) {
        query += "folder:" + Cloudinary.PATHS.ROOT + "/" + Cloudinary.BOOK_TYPES.NOVELS + "/" + folders.get(i) + "/*/";
        continue;
      }
      query += "OR folder:" + Cloudinary.PATHS.ROOT + "/" + Cloudinary.BOOK_TYPES.NOVELS + "/" + folders.get(i) + "/*/";
    }
    try {
      cloudinaryRes = cloudinaryConfig.getCloudinary().search()
          .expression(query)
          .maxResults(Cloudinary.SEARCH_PARAMS.MAX_RESULT)
          .execute();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return cloudinaryRes.values().iterator().next();
  }

  private Query createQuery(Map<String, String> params, Query query) {
    params.forEach((key, value) -> {
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
}
