package com.sismics.docs.core.dao.criteria;
import java.util.Date;
import java.util.List;
import com.sismics.docs.core.constant.AuditLogType;


/**
 * Audit log criteria.
 *
 * @author bgamard 
 */
public class AuditLogCriteria {
    /**
     * Document ID.
     */
    private String documentId;

    /**
     * User ID.
     */
    private String userId;

    /**
     * The search is done for an admin user.
     */
    private boolean isAdmin = false;
    
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public AuditLogCriteria setAdmin(boolean admin) {
        isAdmin = admin;
        return this;
    }

    private Date startDate;
    private Date endDate;
    private List<AuditLogType> types;
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public List<AuditLogType> getTypes() {
        return types;
    }

    public void setTypes(List<AuditLogType> types) {
        this.types = types;
    }

}
