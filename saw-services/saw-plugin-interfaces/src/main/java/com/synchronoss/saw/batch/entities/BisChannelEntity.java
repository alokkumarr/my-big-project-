package com.synchronoss.saw.batch.entities;

import io.swagger.annotations.ApiModel;
import java.io.Serializable;
import javax.persistence.Basic;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import javax.persistence.Lob;

import javax.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@ApiModel("Channel Entity")
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "BIS_CHANNEL", catalog = "sip_batch_ingestion", schema = "")
public class BisChannelEntity  extends BaseEntity implements Serializable {

  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "BIS_CHANNEL_SYS_ID", nullable = false)
  private Long bisChannelSysId;

  @Column(name = "MODIFIED_BY", length = 255)
  private String modifiedBy;
  @Basic(optional = false)
  @Column(name = "CREATED_BY", nullable = false, length = 255)
  private String createdBy;
  @Basic(optional = false)
  @Column(name = "PRODUCT_CODE", nullable = false, length = 50)
  private String productCode;
  @Basic(optional = false)
  @Column(name = "PROJECT_CODE", nullable = false, length = 50)
  private String projectCode;
  @Basic(optional = false)
  @Column(name = "CUSTOMER_CODE", nullable = false, length = 50)
  private String customerCode;
  @Column(name = "CHANNEL_TYPE", nullable = false, length = 50)
  private String channelType;
  
  @Basic(optional = false)
  @Lob
  @Column(name = "CHANNEL_METADATA", nullable = false, length = 2147483647)
  private String channelMetadata;
  
  //@OneToMany(cascade = CascadeType.ALL, orphanRemoval = false)
  //@JoinColumn(name = "bisChannelSysId")
  //@BatchSize(size = 20)
  //private Set<BisRouteEntity> bisRouteCollection;

  public BisChannelEntity() {}

  public BisChannelEntity(Long bisChannelSysId) {
    this.bisChannelSysId = bisChannelSysId;
  }

  /**
   * This is parameterized constructor.
   * @param bisChannelSysId Long
   * @param createdBy String
   * @param productCode String
   * @param projectCode String
   * @param customerCode String
   * @param channelMetadata JSON/String
   */
  public BisChannelEntity(Long bisChannelSysId, String createdBy, String productCode,
      String channelType, String projectCode, String customerCode, String channelMetadata) {
    this.bisChannelSysId = bisChannelSysId;
    this.createdBy = createdBy;
    this.productCode = productCode;
    this.channelType = channelType;
    this.projectCode = projectCode;
    this.customerCode = customerCode;
    this.channelMetadata = channelMetadata;
  }

  public Long getBisChannelSysId() {
    return bisChannelSysId;
  }

  public void setBisChannelSysId(Long bisChannelSysId) {
    this.bisChannelSysId = bisChannelSysId;
  }

  public String getModifiedBy() {
    return modifiedBy;
  }

  public void setModifiedBy(String modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public String getProductCode() {
    return productCode;
  }

  public void setProductCode(String productCode) {
    this.productCode = productCode;
  }

  public String getProjectCode() {
    return projectCode;
  }

  public void setProjectCode(String projectCode) {
    this.projectCode = projectCode;
  }

  public String getCustomerCode() {
    return customerCode;
  }

  public void setCustomerCode(String customerCode) {
    this.customerCode = customerCode;
  }

  public String getChannelMetadata() {
    return channelMetadata;
  }

  public void setChannelMetadata(String channelMetadata) {
    this.channelMetadata = channelMetadata;
  }

  /*  
  public Set<BisRouteEntity> getBisRouteCollection() {
    return bisRouteCollection;
  }

  public void setBisRouteCollection(Set<BisRouteEntity> bisRouteCollection) {
    this.bisRouteCollection = bisRouteCollection;
  }
 */
  public String getChannelType() {
    return channelType;
  }

  public void setChannelType(String channelType) {
    this.channelType = channelType;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (bisChannelSysId != null ? bisChannelSysId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof BisChannelEntity)) {
      return false;
    }
    BisChannelEntity other = (BisChannelEntity) object;
    if ((this.bisChannelSysId == null && other.bisChannelSysId != null)
        || (this.bisChannelSysId != null && !this.bisChannelSysId.equals(other.bisChannelSysId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "{ bisChannelSysId:" + bisChannelSysId + ", modifiedDate:" + getModifiedDate()
        + ", modifiedBy:" + modifiedBy + ", createdDate:" + getCreatedDate() + ", createdBy:"
        + createdBy + ", productCode:" + productCode + ", projectCode:" + projectCode
        + ", customerCode:" + customerCode + ", channelMetadata:" + channelMetadata
        //   + ", bisRouteCollection:" + bisRouteCollection 
        + "}";
  }

  
}
