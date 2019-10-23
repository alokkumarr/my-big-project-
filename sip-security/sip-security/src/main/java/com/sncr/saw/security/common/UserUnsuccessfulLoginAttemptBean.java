package com.sncr.saw.security.common;

import java.io.Serializable;
import java.util.Date;

public class UserUnsuccessfulLoginAttemptBean implements Serializable {
    private static final long serialVersionUID = 6710590219794990734L;
    private Long userSysId;
    private String userId;
    private Long invalidPassWordCount;
    private Date lastUnsuccessLoginTime;

    public Long getUserSysId() {
        return userSysId;
    }

    public void setUserSysId(Long userSysId) {
        this.userSysId = userSysId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getInvalidPassWordCount() {
        return invalidPassWordCount;
    }

    public void setInvalidPassWordCount(Long invalidPassWordCount) {
        this.invalidPassWordCount = invalidPassWordCount;
    }

    public Date getLastUnsuccessLoginTime() {
        return lastUnsuccessLoginTime;
    }

    public void setLastUnsuccessLoginTime(Date lastUnsuccessLoginTime) {
        this.lastUnsuccessLoginTime = lastUnsuccessLoginTime;
    }
}
