package org.kutaka.adminapi.model;

import java.util.Date;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Document("novels")
@RequiredArgsConstructor
@Getter
@Setter
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
}
