package org.kutaka.adminapi.model;

import java.util.Date;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("novels")
public class Novel {

  @Id
  private String id;

  private Boolean isHidden;

  private String nameEn;

  private String nameJa;

  private Integer order;

  private Object categories;

  private Object tags;

  private Date createdAt;

  private Date updatedAt;

  public Novel(String id, Boolean isHidden, String nameEn, String nameJa, Integer order, Object categories, Object tags,
      Date createdAt, Date updatedAt) {
    this.id = id;
    this.isHidden = isHidden;
    this.nameEn = nameEn;
    this.nameJa = nameJa;
    this.order = order;
    this.categories = categories;
    this.tags = tags;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public String getId() {
    return id;
  }

  public Boolean getIsHidden() {
    return isHidden;
  }

  public void setIsHidden(Boolean isHidden) {
    this.isHidden = isHidden;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getNameEn() {
    return nameEn;
  }

  public void setNameEn(String nameEn) {
    this.nameEn = nameEn;
  }

  public String getNameJa() {
    return nameJa;
  }

  public void setNameJa(String nameJa) {
    this.nameJa = nameJa;
  }

  public Integer getOrder() {
    return order;
  }

  public void setOrder(Integer order) {
    this.order = order;
  }

  public Object getCategories() {
    return categories;
  }

  public void setCategories(Object categories) {
    this.categories = categories;
  }

  public Object getTags() {
    return tags;
  }

  public void setTags(Object tags) {
    this.tags = tags;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public Date getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
  }

}
