package org.kutaka.adminapi.service;

import java.util.List;
import java.util.Map;
import org.kutaka.adminapi.constants.DbFields;
import org.kutaka.adminapi.model.Essay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class EssayService {

  @Autowired
  private MongoTemplate mongoTemplate;

  public List<Essay> getEssays(Map<String, String> params) {
    Query query = new Query();
    if (!params.isEmpty()) {
      query = createQuery(params, query);
    }
    query.with(Sort.by(Sort.Direction.DESC, DbFields.ESSAY.ORDER));

    return mongoTemplate.find(query, Essay.class);
  }

  private Query createQuery(Map<String, String> params, Query query) {
    params.forEach((key, value) -> {
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

}
