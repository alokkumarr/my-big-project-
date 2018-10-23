package com.synchronoss.saw.entities;

import java.time.LocalDateTime;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

  @LastModifiedDate
  @Column(name = "MODIFIED_DATE")
  private LocalDateTime modifiedDate;

  @CreatedDate
  @Basic(optional = false)
  @Column(name = "CREATED_DATE", nullable = false)
  private LocalDateTime createdDate;

  public LocalDateTime getModifiedDate() {
    return modifiedDate;
  }

  public void setModifiedDate(LocalDateTime modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  public LocalDateTime getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(LocalDateTime createdDate) {
    this.createdDate = createdDate;
  }

  /**
   * This is invoked before saving the data.
   */
  @PrePersist
  public void prePersist() {
    LocalDateTime now = LocalDateTime.now();
    this.createdDate = now;
    this.modifiedDate = now;
  }

  @PreUpdate
  public void preUpdate() {
    this.modifiedDate = LocalDateTime.now();
  }

}
