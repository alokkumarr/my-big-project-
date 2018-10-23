package com.synchronoss.saw.entities;

import io.swagger.annotations.ApiModel;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;



@ApiModel("Route Entity")
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "BIS_ROUTE", catalog = "batch_ingestion", schema = "")
public class BisRouteEntity extends BaseEntity implements Serializable {

  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "BIS_ROUTE_SYS_ID", nullable = false)
  private Long bisRouteSysId;
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
  @Basic(optional = false)
  @Lob
  @Column(name = "ROUTE_METADATA", nullable = false, length = 2147483647)
  private String routeMetadata;
  @JoinColumn(name = "BIS_CHANNEL_SYS_ID", referencedColumnName = "BIS_CHANNEL_SYS_ID",
      nullable = true)
  @ManyToOne(optional = true, fetch = FetchType.LAZY)
  private BisChannelEntity bisChannelSysId;

  public BisRouteEntity() {}

  public BisRouteEntity(Long bisRouteSysId) {
    this.bisRouteSysId = bisRouteSysId;
  }

  /**
   * This is parameterized constructor.
   * @param bisRouteSysId Long
   * @param createdBy String
   * @param productCode String
   * @param projectCode String
   * @param customerCode String
   * @param routeMetadata String
   */
  public BisRouteEntity(Long bisRouteSysId, String createdBy, String productCode,
      String projectCode, String customerCode, String routeMetadata) {
    this.bisRouteSysId = bisRouteSysId;
    this.createdBy = createdBy;
    this.productCode = productCode;
    this.projectCode = projectCode;
    this.customerCode = customerCode;
    this.routeMetadata = routeMetadata;
  }

  public Long getBisRouteSysId() {
    return bisRouteSysId;
  }

  public void setBisRouteSysId(Long bisRouteSysId) {
    this.bisRouteSysId = bisRouteSysId;
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

  public String getRouteMetadata() {
    return routeMetadata;
  }

  public void setRouteMetadata(String routeMetadata) {
    this.routeMetadata = routeMetadata;
  }

  public BisChannelEntity getBisChannelSysId() {
    return bisChannelSysId;
  }

  public void setBisChannelSysId(BisChannelEntity bisChannelSysId) {
    this.bisChannelSysId = bisChannelSysId;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (bisRouteSysId != null ? bisRouteSysId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof BisRouteEntity)) {
      return false;
    }
    BisRouteEntity other = (BisRouteEntity) object;
    if ((this.bisRouteSysId == null && other.bisRouteSysId != null)
        || (this.bisRouteSysId != null && !this.bisRouteSysId.equals(other.bisRouteSysId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "{ bisRouteSysId:" + bisRouteSysId + ", modifiedDate:" + getModifiedDate()
        + ", modifiedBy:" + modifiedBy + ", createdDate:" + getCreatedDate() + ", createdBy:"
        + createdBy + ", productCode:" + productCode + ", projectCode:" + projectCode
        + ", customerCode:" + customerCode + ", routeMetadata:" + routeMetadata
        + ", bisChannelSysId:" + bisChannelSysId + "}";
  }


}
