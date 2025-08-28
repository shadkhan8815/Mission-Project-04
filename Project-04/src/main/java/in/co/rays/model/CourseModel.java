package in.co.rays.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import in.co.rays.bean.CourseBean;
import in.co.rays.exception.ApplicationException;
import in.co.rays.exception.DatabaseException;
import in.co.rays.exception.DuplicateRecordException;
import in.co.rays.util.JDBCDataSource;

/**
 * CourseModel class handles CRUD operations for Course entities.
 * It interacts with the database to perform create, read, update, delete,
 * and search operations for the "st_course" table.
 * 
 * Responsibilities:
 * <ul>
 *   <li>Generate primary keys for courses.</li>
 *   <li>Add, update, and delete course details.</li>
 *   <li>Search courses by criteria and list all courses.</li>
 *   <li>Find course by primary key or by name.</li>
 * </ul>
 * 
 * @author Shad Khan
 * @version 1.0
 * @since 2025
 */
public class CourseModel {

    private static Logger log = Logger.getLogger(CourseModel.class);

    /**
     * Returns the next primary key value from the database.
     *
     * @return the next primary key as Integer
     * @throws DatabaseException if there is any database access error
     */
    public Integer nextPk() throws DatabaseException {
        log.debug("CourseModel nextPk() method started");
        Connection conn = null;
        int pk = 0;
        try {
            conn = JDBCDataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("select max(id) from st_course");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                pk = rs.getInt(1);
            }
            rs.close();
            pstmt.close();
        } catch (Exception e) {
            log.error("Database Exception in nextPk()", e);
            throw new DatabaseException("Exception : Exception in getting PK");
        } finally {
            JDBCDataSource.closeConnection(conn);
        }
        log.debug("CourseModel nextPk() method End, PK=" + pk);
        return pk + 1;
    }

    /**
     * Adds a new Course record into the database.
     *
     * @param bean CourseBean object containing course details
     * @return primary key of the newly inserted course record
     * @throws ApplicationException if an application error occurs
     * @throws DuplicateRecordException if a course with the same name already exists
     */
    public long add(CourseBean bean) throws ApplicationException, DuplicateRecordException {
        log.debug("CourseModel add() method started");
        Connection conn = null;
        int pk = 0;

        CourseBean duplicateCourse = findByName(bean.getName());
        if (duplicateCourse != null) {
            log.error("Duplicate Course Name : " + bean.getName());
            throw new DuplicateRecordException("Course Name already exists");
        }

        try {
            conn = JDBCDataSource.getConnection();
            pk = nextPk();
            conn.setAutoCommit(false);
            PreparedStatement pstmt = conn.prepareStatement("insert into st_course values(?, ?, ?, ?, ?, ?, ?, ?)");
            pstmt.setInt(1, pk);
            pstmt.setString(2, bean.getName());
            pstmt.setString(3, bean.getDuration());
            pstmt.setString(4, bean.getDescription());
            pstmt.setString(5, bean.getCreatedBy());
            pstmt.setString(6, bean.getModifiedBy());
            pstmt.setTimestamp(7, bean.getCreatedDatetime());
            pstmt.setTimestamp(8, bean.getModifiedDatetime());
            pstmt.executeUpdate();
            conn.commit();
            pstmt.close();
        } catch (Exception e) {
            log.error("Application Exception in add()", e);
            try {
                conn.rollback();
            } catch (Exception ex) {
                log.error("Rollback Exception in add()", ex);
                throw new ApplicationException("Exception : add rollback exception " + ex.getMessage());
            }
            throw new ApplicationException("Exception : Exception in add Course");
        } finally {
            JDBCDataSource.closeConnection(conn);
        }
        log.debug("CourseModel add() method End, PK=" + pk);
        return pk;
    }

    /**
     * Updates an existing Course record in the database.
     *
     * @param bean CourseBean object containing updated course details
     * @throws ApplicationException if an application error occurs
     * @throws DuplicateRecordException if a course with the same name already exists
     */
    public void update(CourseBean bean) throws ApplicationException, DuplicateRecordException {
        log.debug("CourseModel update() method started");
        Connection conn = null;

        CourseBean duplicateCourse = findByName(bean.getName());
        if (duplicateCourse != null && duplicateCourse.getId() != bean.getId()) {
            log.error("Duplicate Course Name Found in Update : " + bean.getName());
            throw new DuplicateRecordException("Course already exists");
        }

        try {
            conn = JDBCDataSource.getConnection();
            conn.setAutoCommit(false);
            PreparedStatement pstmt = conn.prepareStatement(
                    "update st_course set name = ?, duration = ?, description = ?, created_by = ?, modified_by = ?, created_datetime = ?, modified_datetime = ? where id = ?");
            pstmt.setString(1, bean.getName());
            pstmt.setString(2, bean.getDuration());
            pstmt.setString(3, bean.getDescription());
            pstmt.setString(4, bean.getCreatedBy());
            pstmt.setString(5, bean.getModifiedBy());
            pstmt.setTimestamp(6, bean.getCreatedDatetime());
            pstmt.setTimestamp(7, bean.getModifiedDatetime());
            pstmt.setLong(8, bean.getId());
            pstmt.executeUpdate();
            conn.commit();
            pstmt.close();
        } catch (Exception e) {
            log.error("Application Exception in update()", e);
            try {
                conn.rollback();
            } catch (Exception ex) {
                log.error("Rollback Exception in update()", ex);
                throw new ApplicationException("Exception : Delete rollback exception " + ex.getMessage());
            }
            throw new ApplicationException("Exception in updating Course ");
        } finally {
            JDBCDataSource.closeConnection(conn);
        }
        log.debug("CourseModel update() method End");
    }

    /**
     * Deletes a Course record from the database.
     *
     * @param bean CourseBean object identifying the course to delete
     * @throws ApplicationException if an error occurs during deletion
     */
    public void delete(CourseBean bean) throws ApplicationException {
        log.debug("CourseModel delete() method started, ID=" + bean.getId());
        Connection conn = null;
        try {
            conn = JDBCDataSource.getConnection();
            conn.setAutoCommit(false);
            PreparedStatement pstmt = conn.prepareStatement("delete from st_course where id = ?");
            pstmt.setLong(1, bean.getId());
            pstmt.executeUpdate();
            conn.commit();
            pstmt.close();
        } catch (Exception e) {
            log.error("Application Exception in delete()", e);
            try {
                conn.rollback();
            } catch (Exception ex) {
                log.error("Rollback Exception in delete()", ex);
                throw new ApplicationException("Exception : Delete rollback exception " + ex.getMessage());
            }
            throw new ApplicationException("Exception : Exception in delete Course");
        } finally {
            JDBCDataSource.closeConnection(conn);
        }
        log.debug("CourseModel delete() method End");
    }

    /**
     * Finds a Course by its primary key.
     *
     * @param pk the primary key of the course
     * @return CourseBean object if found, otherwise null
     * @throws ApplicationException if an error occurs during retrieval
     */
    public CourseBean findByPk(long pk) throws ApplicationException {
        log.debug("CourseModel findByPk() method started, PK=" + pk);
        StringBuffer sql = new StringBuffer("select * from st_course where id = ?");
        CourseBean bean = null;
        Connection conn = null;
        try {
            conn = JDBCDataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql.toString());
            pstmt.setLong(1, pk);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                bean = new CourseBean();
                bean.setId(rs.getLong(1));
                bean.setName(rs.getString(2));
                bean.setDuration(rs.getString(3));
                bean.setDescription(rs.getString(4));
                bean.setCreatedBy(rs.getString(5));
                bean.setModifiedBy(rs.getString(6));
                bean.setCreatedDatetime(rs.getTimestamp(7));
                bean.setModifiedDatetime(rs.getTimestamp(8));
            }
            rs.close();
            pstmt.close();
        } catch (Exception e) {
            log.error("Application Exception in findByPk()", e);
            throw new ApplicationException("Exception : Exception in getting Course by pk");
        } finally {
            JDBCDataSource.closeConnection(conn);
        }
        log.debug("CourseModel findByPk() method End");
        return bean;
    }

    /**
     * Finds a Course by its name.
     *
     * @param name the name of the course
     * @return CourseBean object if found, otherwise null
     * @throws ApplicationException if an error occurs during retrieval
     */
    public CourseBean findByName(String name) throws ApplicationException {
        log.debug("CourseModel findByName() method started, Name=" + name);
        StringBuffer sql = new StringBuffer("select * from st_course where name = ?");
        CourseBean bean = null;
        Connection conn = null;
        try {
            conn = JDBCDataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql.toString());
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                bean = new CourseBean();
                bean.setId(rs.getLong(1));
                bean.setName(rs.getString(2));
                bean.setDuration(rs.getString(3));
                bean.setDescription(rs.getString(4));
                bean.setCreatedBy(rs.getString(5));
                bean.setModifiedBy(rs.getString(6));
                bean.setCreatedDatetime(rs.getTimestamp(7));
                bean.setModifiedDatetime(rs.getTimestamp(8));
            }
            rs.close();
            pstmt.close();
        } catch (Exception e) {
            log.error("Application Exception in findByName()", e);
            throw new ApplicationException("Exception : Exception in getting Course by Course Name");
        } finally {
            JDBCDataSource.closeConnection(conn);
        }
        log.debug("CourseModel findByName() method End");
        return bean;
    }

    /**
     * Returns a list of all courses.
     *
     * @return List of CourseBean objects
     * @throws ApplicationException if an error occurs during retrieval
     */
    public List<CourseBean> list() throws ApplicationException {
        log.debug("CourseModel list() method started");
        List<CourseBean> list = search(null, 0, 0);
        log.debug("CourseModel list() method End, Total Records=" + list.size());
        return list;
    }

    /**
     * Searches for courses matching the given criteria.
     *
     * @param bean the search criteria as a CourseBean object
     * @param pageNo the page number (for pagination, starts at 1)
     * @param pageSize the number of records per page
     * @return list of CourseBean objects matching the criteria
     * @throws ApplicationException if an error occurs during search
     */
    public List<CourseBean> search(CourseBean bean, int pageNo, int pageSize) throws ApplicationException {
        log.debug("CourseModel search() method started");
        StringBuffer sql = new StringBuffer("select * from st_course where 1=1");

        if (bean != null) {
            if (bean.getId() > 0) {
                sql.append(" and id = " + bean.getId());
            }
            if (bean.getName() != null && bean.getName().length() > 0) {
                sql.append(" and name like '" + bean.getName() + "%'");
            }
            if (bean.getDuration() != null && bean.getDuration().length() > 0) {
                sql.append(" and duration like '" + bean.getDuration() + "%'");
            }
            if (bean.getDescription() != null && bean.getDescription().length() > 0) {
                sql.append(" and description like '" + bean.getDescription() + "%'");
            }
        }

        if (pageSize > 0) {
            pageNo = (pageNo - 1) * pageSize;
            sql.append(" limit " + pageNo + ", " + pageSize);
        }

        ArrayList<CourseBean> list = new ArrayList<CourseBean>();
        Connection conn = null;
        try {
            conn = JDBCDataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql.toString());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                bean = new CourseBean();
                bean.setId(rs.getLong(1));
                bean.setName(rs.getString(2));
                bean.setDuration(rs.getString(3));
                bean.setDescription(rs.getString(4));
                bean.setCreatedBy(rs.getString(5));
                bean.setModifiedBy(rs.getString(6));
                bean.setCreatedDatetime(rs.getTimestamp(7));
                bean.setModifiedDatetime(rs.getTimestamp(8));
                list.add(bean);
            }
            rs.close();
            pstmt.close();
        } catch (Exception e) {
            log.error("Application Exception in search()", e);
            throw new ApplicationException("Exception : Exception in search Course");
        } finally {
            JDBCDataSource.closeConnection(conn);
        }
        log.debug("CourseModel search() method End, Records Found=" + list.size());
        return list;
    }
}
