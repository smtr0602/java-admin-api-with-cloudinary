package org.kutaka.adminapi.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document("novels")
public class Novel {

  @Id
  private String id;
  @Field
  private boolean isHidden;
  @Field
  private String nameEn;
  @Field
  private String nameJa;
  @Field
  private int order;
  @Field
  private Object categories;
  @Field
  private Object tags;
  @Field
  private Date createdAt;
  @Field
  private Date updatedAt;

  public Novel(String id, boolean isHidden, String nameEn, String nameJa, int order, Object categories, Object tags,
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

  public boolean getIsHidden() {
    return isHidden;
  }

  public void setIsHidden(boolean isHidden) {
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

  public int getOrder() {
    return order;
  }

  public void setOrder(int order) {
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
