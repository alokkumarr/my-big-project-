package com.synchronoss.saw.batch.entities;

import io.swagger.annotations.ApiModel;
import java.io.Serializable;
import java.util.Date;
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

@ApiModel("Route Entity")
@Entity(name = "routes")
@EntityListeners(AuditingEntityListener.class)
@Table(name = "BIS_ROUTE", catalog = "sip_batch_ingestion", schema = "")
public class BisRouteEntity implements Serializable {

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
  @Lob
  @Column(name = "ROUTE_METADATA", nullable = false, length = 2147483647)
  private String routeMetadata;

  // @ManyToOne(fetch = FetchType.LAZY, optional = false)
  // @JoinColumn(name = "BIS_CHANNEL_SYS_ID", nullable = false)
  // @OnDelete(action = OnDeleteAction.CASCADE)
  // @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
  // @JsonIdentityReference(alwaysAsId = true)
  // @JsonProperty("BIS_CHANNEL_SYS_ID")
  @Basic(optional = false)
  @Column(name = "BIS_CHANNEL_SYS_ID", nullable = false)
  private Long bisChannelSysId;
  // private BisChannelEntity bisChannelSysId;

  @Column(name = "MODIFIED_DATE")
  private Date modifiedDate;

  @Column(name = "CREATED_DATE")
  private Date createdDate;

  public BisRouteEntity() {}

  public BisRouteEntity(Long bisRouteSysId) {
    this.bisRouteSysId = bisRouteSysId;
  }

  /**
   * This is parameterized constructor.
   * 
   * @param bisRouteSysId Long
   * @param createdBy String
   * @param routeMetadata String
   */
  public BisRouteEntity(Long bisRouteSysId, String createdBy, String routeMetadata) {
    this.bisRouteSysId = bisRouteSysId;
    this.createdBy = createdBy;
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

  public String getRouteMetadata() {
    return routeMetadata;
  }

  public void setRouteMetadata(String routeMetadata) {
    this.routeMetadata = routeMetadata;
  }

  public Long getBisChannelSysId() {
    return bisChannelSysId;
  }

  public void setBisChannelSysId(Long bisChannelSysId) {
    this.bisChannelSysId = bisChannelSysId;
  }

  public Date getModifiedDate() {
    return modifiedDate;
  }


  public Date getCreatedDate() {
    return createdDate;
  }

  public void setModifiedDate(Date modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
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
        + createdBy + ", routeMetadata:" + routeMetadata + ", bisChannelSysId:" + bisChannelSysId
        + "}";
  }

}
