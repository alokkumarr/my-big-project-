package com.synchronoss.saw.batch.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Date;
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
@JsonIgnoreProperties(
        value = {"createdDate", "modifiedDate"},
        allowGetters = true
)
public abstract class BaseEntity {

  @LastModifiedDate
  @Column(name = "MODIFIED_DATE", nullable=true, insertable=false)
  private Date modifiedDate;

  @CreatedDate
  @Basic(optional = false)
  @Column(name = "CREATED_DATE", nullable = true, updatable=false)
  private Date createdDate;

  public Date getModifiedDate() {
    return modifiedDate;
  }

  public void setModifiedDate(Date modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  public Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  /**
   * This is invoked before saving the data.
   */
  @PrePersist
  public void prePersist() {
    Date now = new Date();
    this.createdDate = now;
    this.modifiedDate = now;
  }

  @PreUpdate
  public void preUpdate() {
    this.modifiedDate = new Date();
  }

}
