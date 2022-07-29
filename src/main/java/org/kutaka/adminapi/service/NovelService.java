package org.kutaka.adminapi.service;

import java.util.List;
import java.util.Map;
import org.kutaka.adminapi.constants.DbFields;
import org.kutaka.adminapi.model.Novel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class NovelService {

  @Autowired
  private MongoTemplate mongoTemplate;

  public List<Novel> getNovels(Map<String, String> params) {
    Query query = new Query();
    if (!params.isEmpty()) {
      query = createQuery(params, query);
    }
    query.with(Sort.by(Sort.Direction.DESC, DbFields.NOVEL.ORDER));

    return mongoTemplate.find(query, Novel.class);
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
