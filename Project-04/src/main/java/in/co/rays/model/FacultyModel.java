package in.co.rays.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import in.co.rays.bean.CollegeBean;
import in.co.rays.bean.CourseBean;
import in.co.rays.bean.FacultyBean;
import in.co.rays.bean.SubjectBean;
import in.co.rays.exception.ApplicationException;
import in.co.rays.exception.DatabaseException;
import in.co.rays.exception.DuplicateRecordException;
import in.co.rays.util.JDBCDataSource;

/**
 * FacultyModel handles CRUD operations, searches, and retrieval of Faculty entities.
 * 
 * @author Shad Khan
 * @version 1.0
 */
public class FacultyModel {

    private static Logger log = Logger.getLogger(FacultyModel.class);

    /**
     * Returns the next primary key (ID) for a new faculty record.
     * 
     * @return next primary key as Integer
     * @throws DatabaseException if database access fails
     */
    public Integer nextPk() throws DatabaseException {
        log.debug("FacultyModel nextPk started");
        Connection conn = null;
        int pk = 0;
        try {
            conn = JDBCDataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("select max(id) from st_faculty");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                pk = rs.getInt(1);
            }
            rs.close();
            pstmt.close();
            log.debug("FacultyModel nextPk returning: " + (pk + 1));
        } catch (Exception e) {
            log.error("Database Exception in nextPk", e);
            throw new DatabaseException("Exception : Exception in getting PK");
        } finally {
            JDBCDataSource.closeConnection(conn);
        }
        return pk + 1;
    }

    /**
     * Adds a new faculty record to the database.
     * Also sets college, course, and subject names based on IDs.
     * 
     * @param bean FacultyBean containing faculty details
     * @return primary key of the newly inserted faculty record
     * @throws ApplicationException for general errors
     * @throws DuplicateRecordException if email already exists
     */
    public long add(FacultyBean bean) throws ApplicationException, DuplicateRecordException {
        log.debug("FacultyModel add started");
        Connection conn = null;
        int pk = 0;

        CollegeModel collegeModel = new CollegeModel();
        CollegeBean collegeBean = collegeModel.findByPk(bean.getCollegeId());
        bean.setCollegeName(collegeBean.getName());

        CourseModel courseModel = new CourseModel();
        CourseBean courseBean = courseModel.findByPk(bean.getCourseId());
        bean.setCourseName(courseBean.getName());

        SubjectModel subjectModel = new SubjectModel();
        SubjectBean subjectBean = subjectModel.findByPk(bean.getSubjectId());
        bean.setSubjectName(subjectBean.getName());

        FacultyBean existbean = findByEmail(bean.getEmail());

        if (existbean != null) {
            log.error("Duplicate email found: " + bean.getEmail());
            throw new DuplicateRecordException("Email Id already exists");
        }

        try {
            conn = JDBCDataSource.getConnection();
            pk = nextPk();
            conn.setAutoCommit(false); // Begin transaction
            PreparedStatement pstmt = conn.prepareStatement(
                    "insert into st_faculty values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            pstmt.setInt(1, pk);
            pstmt.setString(2, bean.getFirstName());
            pstmt.setString(3, bean.getLastName());
            pstmt.setDate(4, new java.sql.Date(bean.getDob().getTime()));
            pstmt.setString(5, bean.getGender());
            pstmt.setString(6, bean.getMobileNo());
            pstmt.setString(7, bean.getEmail());
            pstmt.setLong(8, bean.getCollegeId());
            pstmt.setString(9, bean.getCollegeName());
            pstmt.setLong(10, bean.getCourseId());
            pstmt.setString(11, bean.getCourseName());
            pstmt.setLong(12, bean.getSubjectId());
            pstmt.setString(13, bean.getSubjectName());
            pstmt.setString(14, bean.getCreatedBy());
            pstmt.setString(15, bean.getModifiedBy());
            pstmt.setTimestamp(16, bean.getCreatedDatetime());
            pstmt.setTimestamp(17, bean.getModifiedDatetime());
            pstmt.executeUpdate();
            conn.commit(); // End transaction
            pstmt.close();
            log.debug("Faculty added successfully with id: " + pk);
        } catch (Exception e) {
            log.error("Exception in adding Faculty", e);
            try {
                conn.rollback();
            } catch (Exception ex) {
                log.error("Rollback exception in add", ex);
                throw new ApplicationException("Exception : add rollback exception " + ex.getMessage());
            }
            throw new ApplicationException("Exception : Exception in add Faculty");
        } finally {
            JDBCDataSource.closeConnection(conn);
        }
        return pk;
    }

    /**
     * Updates an existing faculty record.
     * Updates college, course, and subject names based on their IDs.
     * 
     * @param bean FacultyBean containing updated faculty data
     * @throws ApplicationException for general errors
     * @throws DuplicateRecordException if email already exists for a different record
     */
    public void update(FacultyBean bean) throws ApplicationException, DuplicateRecordException {
        log.debug("FacultyModel update started for id: " + bean.getId());
        Connection conn = null;

        // get College Name
        CollegeModel collegeModel = new CollegeModel();
        CollegeBean collegeBean = collegeModel.findByPk(bean.getCollegeId());
        bean.setCollegeName(collegeBean.getName());

        // get Course Name
        CourseModel courseModel = new CourseModel();
        CourseBean courseBean = courseModel.findByPk(bean.getCourseId());
        bean.setCourseName(courseBean.getName());

        // get Subject Name
        SubjectModel subjectModel = new SubjectModel();
        SubjectBean subjectBean = subjectModel.findByPk(bean.getSubjectId());
        bean.setSubjectName(subjectBean.getName());

        FacultyBean beanExist = findByEmail(bean.getEmail());
        if (beanExist != null && !(beanExist.getId() == bean.getId())) {
            log.error("Duplicate email found during update: " + bean.getEmail());
            throw new DuplicateRecordException("EmailId is already exist");
        }
        try {
            conn = JDBCDataSource.getConnection();

            conn.setAutoCommit(false);
            PreparedStatement pstmt = conn.prepareStatement(
                    "update st_faculty set first_name = ?, last_name = ?, dob = ?, gender = ?, mobile_no = ?, email = ?, college_id = ?, college_name = ?, course_id = ?, course_name = ?, subject_id = ?, subject_name = ?, created_by = ?, modified_by = ?, created_datetime = ?, modified_datetime = ? where id = ?");

            pstmt.setString(1, bean.getFirstName());
            pstmt.setString(2, bean.getLastName());
            pstmt.setDate(3, new java.sql.Date(bean.getDob().getTime()));
            pstmt.setString(4, bean.getGender());
            pstmt.setString(5, bean.getMobileNo());
            pstmt.setString(6, bean.getEmail());
            pstmt.setLong(7, bean.getCollegeId());
            pstmt.setString(8, bean.getCollegeName());
            pstmt.setLong(9, bean.getCourseId());
            pstmt.setString(10, bean.getCourseName());
            pstmt.setLong(11, bean.getSubjectId());
            pstmt.setString(12, bean.getSubjectName());
            pstmt.setString(13, bean.getCreatedBy());
            pstmt.setString(14, bean.getModifiedBy());
            pstmt.setTimestamp(15, bean.getCreatedDatetime());
            pstmt.setTimestamp(16, bean.getModifiedDatetime());
            pstmt.setLong(17, bean.getId());
            pstmt.executeUpdate();
            conn.commit();
            pstmt.close();
            log.debug("Faculty updated successfully for id: " + bean.getId());
        } catch (Exception e) {
            log.error("Exception in updating Faculty", e);
            try {
                conn.rollback();
            } catch (Exception ex) {
                log.error("Rollback exception in update", ex);
                throw new ApplicationException("Exception : Delete rollback exception " + ex.getMessage());
            }
            throw new ApplicationException("Exception in updating Faculty ");
        } finally {
            JDBCDataSource.closeConnection(conn);
        }
    }

    /**
     * Deletes a faculty record from the database.
     * 
     * @param bean FacultyBean containing the faculty record to delete
     * @throws ApplicationException if deletion fails
     */
    public void delete(FacultyBean bean) throws ApplicationException {
        log.debug("FacultyModel delete started for id: " + bean.getId());
        Connection conn = null;
        try {
            conn = JDBCDataSource.getConnection();
            conn.setAutoCommit(false);
            PreparedStatement pstmt = conn.prepareStatement("delete from st_faculty where id = ?");
            pstmt.setLong(1, bean.getId());
            pstmt.executeUpdate();
            conn.commit();
            pstmt.close();
            log.debug("Faculty deleted successfully for id: " + bean.getId());
        } catch (Exception e) {
            log.error("Exception in deleting Faculty", e);
            try {
                conn.rollback();
            } catch (Exception ex) {
                log.error("Rollback exception in delete", ex);
                throw new ApplicationException("Exception : Delete rollback exception " + ex.getMessage());
            }
            throw new ApplicationException("Exception : Exception in delete Faculty");
        } finally {
            JDBCDataSource.closeConnection(conn);
        }
    }

    /**
     * Finds a faculty record by its primary key (ID).
     * 
     * @param pk the primary key ID
     * @return FacultyBean if found, else null
     * @throws ApplicationException if database error occurs
     */
    public FacultyBean findByPk(long pk) throws ApplicationException {
        log.debug("FacultyModel findByPk started for id: " + pk);
        StringBuffer sql = new StringBuffer("select * from st_faculty where id = ?");
        FacultyBean bean = null;
        Connection conn = null;
        try {
            conn = JDBCDataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql.toString());
            pstmt.setLong(1, pk);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                bean = new FacultyBean();
                bean.setId(rs.getLong(1));
                bean.setFirstName(rs.getString(2));
                bean.setLastName(rs.getString(3));
                bean.setDob(rs.getDate(4));
                bean.setGender(rs.getString(5));
                bean.setMobileNo(rs.getString(6));
                bean.setEmail(rs.getString(7));
                bean.setCollegeId(rs.getLong(8));
                bean.setCollegeName(rs.getString(9));
                bean.setCourseId(rs.getLong(10));
                bean.setCourseName(rs.getString(11));
                bean.setSubjectId(rs.getLong(12));
                bean.setSubjectName(rs.getString(13));
                bean.setCreatedBy(rs.getString(14));
                bean.setModifiedBy(rs.getString(15));
                bean.setCreatedDatetime(rs.getTimestamp(16));
                bean.setModifiedDatetime(rs.getTimestamp(17));
            }
            rs.close();
            pstmt.close();
            log.debug("FacultyModel findByPk completed");
        } catch (Exception e) {
            log.error("Exception in findByPk", e);
            throw new ApplicationException("Exception : Exception in getting Faculty by pk");
        } finally {
            JDBCDataSource.closeConnection(conn);
        }
        return bean;
    }

    /**
     * Finds a faculty record by email.
     * 
     * @param email faculty email to search for
     * @return FacultyBean if found, else null
     * @throws ApplicationException if database error occurs
     */
    public FacultyBean findByEmail(String email) throws ApplicationException {
        log.debug("FacultyModel findByEmail started for email: " + email);
        StringBuffer sql = new StringBuffer("select * from st_faculty where email = ?");
        FacultyBean bean = null;
        Connection conn = null;
        System.out.println("sql" + sql);

        try {
            conn = JDBCDataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql.toString());
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                bean = new FacultyBean();
                bean.setId(rs.getLong(1));
                bean.setFirstName(rs.getString(2));
                bean.setLastName(rs.getString(3));
                bean.setDob(rs.getDate(4));
                bean.setGender(rs.getString(5));
                bean.setMobileNo(rs.getString(6));
                bean.setEmail(rs.getString(7));
                bean.setCollegeId(rs.getLong(8));
                bean.setCollegeName(rs.getString(9));
                bean.setCourseId(rs.getLong(10));
                bean.setCourseName(rs.getString(11));
                bean.setSubjectId(rs.getLong(12));
                bean.setSubjectName(rs.getString(13));
                bean.setCreatedBy(rs.getString(14));
                bean.setModifiedBy(rs.getString(15));
                bean.setCreatedDatetime(rs.getTimestamp(16));
                bean.setModifiedDatetime(rs.getTimestamp(17));
            }
            rs.close();
            pstmt.close();
            log.debug("FacultyModel findByEmail completed");
        } catch (Exception e) {
            log.error("Exception in findByEmail", e);
            throw new ApplicationException("Exception : Exception in getting Faculty by Email");
        } finally {
            JDBCDataSource.closeConnection(conn);
        }
        return bean;
    }

    /**
     * Returns all faculty records as a list.
     * 
     * @return List of FacultyBean
     * @throws ApplicationException if database error occurs
     */
    public List<FacultyBean> list() throws ApplicationException {
        log.debug("FacultyModel list started");
        List<FacultyBean> list = search(null, 0, 0);
        log.debug("FacultyModel list completed with size: " + list.size());
        return list;
    }

    /**
     * Searches for faculty records matching the criteria in the bean.
     * Supports pagination.
     * 
     * @param bean FacultyBean with search criteria
     * @param pageNo current page number (starts at 1)
     * @param pageSize number of records per page
     * @return List of FacultyBean matching the criteria
     * @throws ApplicationException if database error occurs
     */
    public List<FacultyBean> search(FacultyBean bean, int pageNo, int pageSize) throws ApplicationException {
        log.debug("FacultyModel search started");
        StringBuffer sql = new StringBuffer("select * from st_faculty where 1=1");

        if (bean != null) {
            if (bean.getId() > 0) {
                sql.append(" and id = " + bean.getId());
            }
            if (bean.getCollegeId() > 0) {
                sql.append(" and college_id = " + bean.getCollegeId());
            }
            if (bean.getSubjectId() > 0) {
                sql.append(" and subject_id = " + bean.getSubjectId());
            }
            if (bean.getCourseId() > 0) {
                sql.append(" and course_id = " + bean.getCourseId());
            }
            if (bean.getFirstName() != null && bean.getFirstName().length() > 0) {
                sql.append(" and first_name like '" + bean.getFirstName() + "%'");
            }
            if (bean.getLastName() != null && bean.getLastName().length() > 0) {
                sql.append(" and last_name like '" + bean.getLastName() + "%'");
            }
            if (bean.getGender() != null && bean.getGender().length() > 0) {
                sql.append(" and gender like '" + bean.getGender() + "%'");
            }
            if (bean.getDob() != null && bean.getDob().getDate() > 0) {
                sql.append(" and dob = " + bean.getDob());
            }
            if (bean.getEmail() != null && bean.getEmail().length() > 0) {
                sql.append(" and email like '" + bean.getEmail() + "%'");
            }
            if (bean.getMobileNo() != null && bean.getMobileNo().length() > 0) {
                sql.append(" and mobile_no = " + bean.getMobileNo());
            }
            if (bean.getCourseName() != null && bean.getCourseName().length() > 0) {
                sql.append(" and course_name like '" + bean.getCourseName() + "%'");
            }
            if (bean.getCollegeName() != null && bean.getCollegeName().length() > 0) {
                sql.append(" and college_name like '" + bean.getCollegeName() + "%'");
            }
            if (bean.getSubjectName() != null && bean.getSubjectName().length() > 0) {
                sql.append(" and subject_name like '" + bean.getSubjectName() + "%'");
            }
        }
        if (pageSize > 0) {
            pageNo = (pageNo - 1) * pageSize;
            sql.append(" limit " + pageNo + ", " + pageSize);
        }

        ArrayList<FacultyBean> list = new ArrayList<FacultyBean>();
        Connection conn = null;
        try {
            conn = JDBCDataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql.toString());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                bean = new FacultyBean();
                bean.setId(rs.getLong(1));
                bean.setFirstName(rs.getString(2));
                bean.setLastName(rs.getString(3));
                bean.setDob(rs.getDate(4));
                bean.setGender(rs.getString(5));
                bean.setMobileNo(rs.getString(6));
                bean.setEmail(rs.getString(7));
                bean.setCollegeId(rs.getLong(8));
                bean.setCollegeName(rs.getString(9));
                bean.setCourseId(rs.getLong(10));
                bean.setCourseName(rs.getString(11));
                bean.setSubjectId(rs.getLong(12));
                bean.setSubjectName(rs.getString(13));
                bean.setCreatedBy(rs.getString(14));
                bean.setModifiedBy(rs.getString(15));
                bean.setCreatedDatetime(rs.getTimestamp(16));
                bean.setModifiedDatetime(rs.getTimestamp(17));
                list.add(bean);
            }
            rs.close();
            pstmt.close();
            log.debug("FacultyModel search completed with size: " + list.size());
        } catch (Exception e) {
            log.error("Exception in search Faculty", e);
            throw new ApplicationException("Exception : Exception in search Faculty");
        } finally {
            JDBCDataSource.closeConnection(conn);
        }
        return list;
    }
}
