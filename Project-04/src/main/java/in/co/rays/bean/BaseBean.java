package in.co.rays.bean;

import java.sql.Timestamp;

/**
 * Abstract base bean class that contains common audit fields.
 * All beans should extend this class.
 * Implements {@link DropdownListBean}.
 * 
 * @author  shad
 * @version 1.0
 */
public abstract class BaseBean implements DropdownListBean {

    /** Primary key ID of the bean */
    protected long id;

    /** User who created the record */
    protected String createdBy;

    /** User who last modified the record */
    protected String modifiedBy;

    /** Timestamp when the record was created */
    protected Timestamp createdDatetime;

    /** Timestamp when the record was last modified */
    protected Timestamp modifiedDatetime;

    /**
     * Gets the ID of the bean.
     * 
     * @return the ID
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the ID of the bean.
     * 
     * @param id the ID to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets the user who created the record.
     * 
     * @return the creator's username
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets the user who created the record.
     * 
     * @param createdBy the creator's username
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Gets the user who last modified the record.
     * 
     * @return the username of the last modifier
     */
    public String getModifiedBy() {
        return modifiedBy;
    }

    /**
     * Sets the user who last modified the record.
     * 
     * @param modifiedBy the username of the last modifier
     */
    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    /**
     * Gets the timestamp when the record was created.
     * 
     * @return the creation timestamp
     */
    public Timestamp getCreatedDatetime() {
        return createdDatetime;
    }

    /**
     * Sets the timestamp when the record was created.
     * 
     * @param createdDatetime the creation timestamp
     */
    public void setCreatedDatetime(Timestamp createdDatetime) {
        this.createdDatetime = createdDatetime;
    }

    /**
     * Gets the timestamp when the record was last modified.
     * 
     * @return the modification timestamp
     */
    public Timestamp getModifiedDatetime() {
        return modifiedDatetime;
    }

    /**
     * Sets the timestamp when the record was last modified.
     * 
     * @param modifiedDatetime the modification timestamp
     */
    public void setModifiedDatetime(Timestamp modifiedDatetime) {
        this.modifiedDatetime = modifiedDatetime;
    }
}
