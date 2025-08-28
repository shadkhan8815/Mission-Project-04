package in.co.rays.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import in.co.rays.bean.FacultyBean;
import in.co.rays.bean.MarksheetBean;
import in.co.rays.bean.StudentBean;
import in.co.rays.exception.ApplicationException;
import in.co.rays.exception.DatabaseException;
import in.co.rays.exception.DuplicateRecordException;
import in.co.rays.util.JDBCDataSource;

/**
 * MarksheetModel is a Data Access Object (DAO) class that provides methods to
 * perform CRUD operations on the Marksheet table in the database.
 * It handles adding, updating, deleting, searching, and retrieving merit lists.
 * 
 * @author Shad Khan
 * @version 1.0
 */
public class MarksheetModel {

    private static Logger log = Logger.getLogger(MarksheetModel.class);

    /**
     * Gets the next primary key for the marksheet table.
     *
     * @return the next primary key value
     * @throws DatabaseException if any database error occurs
     */
    public Integer nextPk() throws DatabaseException {
        log.debug("MarksheetModel nextPk started");
        Connection conn = null;
        int pk = 0;
        try {
            conn = JDBCDataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("select max(id) from st_marksheet");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                pk = rs.getInt(1);
            }
            rs.close();
            pstmt.close();
            log.debug("Next PK retrieved: " + (pk + 1));
        } catch (Exception e) {
            log.error("Database Exception in nextPk", e);
            throw new DatabaseException("Exception in Marksheet getting PK");
        } finally {
            JDBCDataSource.closeConnection(conn);
        }
        return pk + 1;
    }

    /**
     * Adds a new marksheet record into the database.
     *
     * @param bean the marksheet bean containing data
     * @return the primary key of the newly added marksheet
     * @throws ApplicationException if any application error occurs
     * @throws DuplicateRecordException if the roll number already exists
     */
    public long add(MarksheetBean bean) throws ApplicationException, DuplicateRecordException {
        log.debug("MarksheetModel add started for RollNo: " + bean.getRollNo());
        Connection conn = null;
        int pk = 0;

        StudentModel studentModel = new StudentModel();
        StudentBean studentbean = studentModel.findByPk(bean.getStudentId());
        bean.setName(studentbean.getFirstName() + " " + studentbean.getLastName());

        MarksheetBean duplicateMarksheet = findByRollNo(bean.getRollNo());

        if (duplicateMarksheet != null) {
            log.error("Duplicate RollNo found: " + bean.getRollNo());
            throw new DuplicateRecordException("Roll Number already exists");
        }

        try {
            conn = JDBCDataSource.getConnection();
            pk = nextPk();
            conn.setAutoCommit(false); // Begin transaction
            PreparedStatement pstmt = conn
                    .prepareStatement("insert into st_marksheet values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            pstmt.setInt(1, pk);
            pstmt.setString(2, bean.getRollNo());
            pstmt.setLong(3, bean.getStudentId());
            pstmt.setString(4, bean.getName());
            pstmt.setInt(5, bean.getPhysics());
            pstmt.setInt(6, bean.getChemistry());
            pstmt.setInt(7, bean.getMaths());
            pstmt.setString(8, bean.getCreatedBy());
            pstmt.setString(9, bean.getModifiedBy());
            pstmt.setTimestamp(10, bean.getCreatedDatetime());
            pstmt.setTimestamp(11, bean.getModifiedDatetime());
            pstmt.executeUpdate();
            conn.commit(); // End transaction
            pstmt.close();
            log.debug("Marksheet added successfully with id: " + pk);
        } catch (Exception e) {
            log.error("Exception in adding Marksheet", e);
            try {
                conn.rollback();
            } catch (Exception ex) {
                log.error("Rollback Exception in add", ex);
                throw new ApplicationException("add rollback exception " + ex.getMessage());
            }
            throw new ApplicationException("Exception in add marksheet");
        } finally {
            JDBCDataSource.closeConnection(conn);
        }
        return pk;
    }

    /**
     * Updates an existing marksheet record.
     *
     * @param bean the marksheet bean containing updated data
     * @throws ApplicationException if any application error occurs
     * @throws DuplicateRecordException if the roll number already exists
     */
    public void update(MarksheetBean bean) throws ApplicationException, DuplicateRecordException {
        log.debug("MarksheetModel update started for id: " + bean.getId());
        Connection conn = null;

        MarksheetBean beanExist = findByRollNo(bean.getRollNo());

        if (beanExist != null && beanExist.getId() != bean.getId()) {
            log.error("Duplicate RollNo found during update: " + bean.getRollNo());
            throw new DuplicateRecordException("Roll No is already exist");
        }

        StudentModel studentModel = new StudentModel();
        StudentBean studentbean = studentModel.findByPk(bean.getStudentId());
        bean.setName(studentbean.getFirstName() + " " + studentbean.getLastName());

        try {
            conn = JDBCDataSource.getConnection();
            conn.setAutoCommit(false); // Begin transaction
            PreparedStatement pstmt = conn.prepareStatement(
                    "update st_marksheet set roll_no = ?, student_id = ?, name = ?, physics = ?, chemistry = ?, maths = ?, created_by = ?, modified_by = ?, created_datetime = ?, modified_datetime = ? where id = ?");
            pstmt.setString(1, bean.getRollNo());
            pstmt.setLong(2, bean.getStudentId());
            pstmt.setString(3, bean.getName());
            pstmt.setInt(4, bean.getPhysics());
            pstmt.setInt(5, bean.getChemistry());
            pstmt.setInt(6, bean.getMaths());
            pstmt.setString(7, bean.getCreatedBy());
            pstmt.setString(8, bean.getModifiedBy());
            pstmt.setTimestamp(9, bean.getCreatedDatetime());
            pstmt.setTimestamp(10, bean.getModifiedDatetime());
            pstmt.setLong(11, bean.getId());
            pstmt.executeUpdate();
            conn.commit(); // End transaction
            pstmt.close();
            log.debug("Marksheet updated successfully for id: " + bean.getId());
        } catch (Exception e) {
            log.error("Exception in updating Marksheet", e);
            try {
                conn.rollback();
            } catch (Exception ex) {
                log.error("Rollback exception in update", ex);
                throw new ApplicationException("Update rollback exception " + ex.getMessage());
            }
            throw new ApplicationException("Exception in updating Marksheet ");
        } finally {
            JDBCDataSource.closeConnection(conn);
        }
    }

    /**
     * Deletes a marksheet record from the database.
     *
     * @param bean the marksheet bean containing the id to delete
     * @throws ApplicationException if any application error occurs
     */
    public void delete(MarksheetBean bean) throws ApplicationException {
        log.debug("MarksheetModel delete started for id: " + bean.getId());
        Connection conn = null;
        try {
            conn = JDBCDataSource.getConnection();
            conn.setAutoCommit(false); // Begin transaction
            PreparedStatement pstmt = conn.prepareStatement("delete from st_marksheet where id = ?");
            pstmt.setLong(1, bean.getId());
            System.out.println("Deleted Marksheet");
            pstmt.executeUpdate();
            conn.commit(); // End transaction
            pstmt.close();
            log.debug("Marksheet deleted successfully for id: " + bean.getId());
        } catch (Exception e) {
            log.error("Exception in deleting Marksheet", e);
            try {
                conn.rollback();
            } catch (Exception ex) {
                log.error("Rollback exception in delete", ex);
                throw new ApplicationException("Delete rollback exception " + ex.getMessage());
            }
            throw new ApplicationException("Exception in delete marksheet");
        } finally {
            JDBCDataSource.closeConnection(conn);
        }
    }

    /**
     * Finds a marksheet record by primary key.
     *
     * @param pk the primary key
     * @return the marksheet bean, or null if not found
     * @throws ApplicationException if any application error occurs
     */
    public MarksheetBean findByPk(long pk) throws ApplicationException {
        log.debug("MarksheetModel findByPk started for id: " + pk);
        StringBuffer sql = new StringBuffer("select * from st_marksheet where id = ?");
        MarksheetBean bean = null;
        Connection conn = null;
        try {
            conn = JDBCDataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql.toString());
            pstmt.setLong(1, pk);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                bean = new MarksheetBean();
                bean.setId(rs.getLong(1));
                bean.setRollNo(rs.getString(2));
                bean.setStudentId(rs.getLong(3));
                bean.setName(rs.getString(4));
                bean.setPhysics(rs.getInt(5));
                bean.setChemistry(rs.getInt(6));
                bean.setMaths(rs.getInt(7));
                bean.setCreatedBy(rs.getString(8));
                bean.setModifiedBy(rs.getString(9));
                bean.setCreatedDatetime(rs.getTimestamp(10));
                bean.setModifiedDatetime(rs.getTimestamp(11));
            }
            rs.close();
            pstmt.close();
            log.debug("MarksheetModel findByPk completed for id: " + pk);
        } catch (Exception e) {
            log.error("Exception in findByPk", e);
            throw new ApplicationException("Exception in getting marksheet by pk");
        } finally {
            JDBCDataSource.closeConnection(conn);
        }
        return bean;
    }

    /**
     * Finds a marksheet record by roll number.
     *
     * @param rollNo the roll number
     * @return the marksheet bean, or null if not found
     * @throws ApplicationException if any application error occurs
     */
    public MarksheetBean findByRollNo(String rollNo) throws ApplicationException {
        log.debug("MarksheetModel findByRollNo started for RollNo: " + rollNo);
        StringBuffer sql = new StringBuffer("select * from st_marksheet where roll_no = ?");
        MarksheetBean bean = null;
        Connection conn = null;
        try {
            conn = JDBCDataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql.toString());
            pstmt.setString(1, rollNo);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                bean = new MarksheetBean();
                bean.setId(rs.getLong(1));
                bean.setRollNo(rs.getString(2));
                bean.setStudentId(rs.getLong(3));
                bean.setName(rs.getString(4));
                bean.setPhysics(rs.getInt(5));
                bean.setChemistry(rs.getInt(6));
                bean.setMaths(rs.getInt(7));
                bean.setCreatedBy(rs.getString(8));
                bean.setModifiedBy(rs.getString(9));
                bean.setCreatedDatetime(rs.getTimestamp(10));
                bean.setModifiedDatetime(rs.getTimestamp(11));
            }
            rs.close();
            pstmt.close();
            log.debug("MarksheetModel findByRollNo completed for RollNo: " + rollNo);
        } catch (Exception e) {
            log.error("Exception in findByRollNo", e);
            throw new ApplicationException("Exception in getting marksheet by roll no");
        } finally {
            JDBCDataSource.closeConnection(conn);
        }
        return bean;
    }

    /**
     * Returns a list of all marksheets.
     *
     * @return list of marksheet beans
     * @throws ApplicationException if any application error occurs
     */
    public List<MarksheetBean> list() throws ApplicationException {
        log.debug("MarksheetModel list started");
        List<MarksheetBean> list = search(null, 0, 0);
        log.debug("MarksheetModel list completed with size: " + list.size());
        return list;
    }

    /**
     * Searches marksheet records with pagination.
     *
     * @param bean the search criteria bean
     * @param pageNo the page number
     * @param pageSize the number of records per page
     * @return a list of marksheet beans
     * @throws ApplicationException if any application error occurs
     */
    public List<MarksheetBean> search(MarksheetBean bean, int pageNo, int pageSize) throws ApplicationException {
        log.debug("MarksheetModel search started");
        StringBuffer sql = new StringBuffer("select * from st_marksheet where 1=1");

        if (bean != null) {
            if (bean.getId() > 0) {
                sql.append(" and id = " + bean.getId());
            }
            if (bean.getRollNo() != null && bean.getRollNo().length() > 0) {
                sql.append(" and roll_no like '" + bean.getRollNo() + "%'");
            }
            if (bean.getName() != null && bean.getName().length() > 0) {
                sql.append(" and name like '" + bean.getName() + "%'");
            }
            if (bean.getPhysics() != null && bean.getPhysics() > 0) {
                sql.append(" and physics = " + bean.getPhysics());
            }
            if (bean.getChemistry() != null && bean.getChemistry() > 0) {
                sql.append(" and chemistry = " + bean.getChemistry());
            }
            if (bean.getMaths() != null && bean.getMaths() > 0) {
                sql.append(" and maths = '" + bean.getMaths());
            }
        }

        if (pageSize > 0) {
            pageNo = (pageNo - 1) * pageSize;
            sql.append(" limit " + pageNo + ", " + pageSize);
        }

        ArrayList<MarksheetBean> list = new ArrayList<MarksheetBean>();
        Connection conn = null;
        try {
            conn = JDBCDataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql.toString());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                bean = new MarksheetBean();
                bean.setId(rs.getLong(1));
                bean.setRollNo(rs.getString(2));
                bean.setStudentId(rs.getLong(3));
                bean.setName(rs.getString(4));
                bean.setPhysics(rs.getInt(5));
                bean.setChemistry(rs.getInt(6));
                bean.setMaths(rs.getInt(7));
                bean.setCreatedBy(rs.getString(8));
                bean.setModifiedBy(rs.getString(9));
                bean.setCreatedDatetime(rs.getTimestamp(10));
                bean.setModifiedDatetime(rs.getTimestamp(11));
                list.add(bean);
            }
            rs.close();
            log.debug("MarksheetModel search completed with size: " + list.size());
        } catch (Exception e) {
            log.error("Exception in search Marksheet", e);
            throw new ApplicationException("Update rollback exception " + e.getMessage());
        } finally {
            JDBCDataSource.closeConnection(conn);
        }
        return list;
    }

    /**
     * Retrieves the merit list of marksheets with pagination.
     * Only students passing in all subjects (marks > 33) are included.
     *
     * @param pageNo the page number
     * @param pageSize the number of records per page
     * @return a list of marksheet beans in merit order
     * @throws ApplicationException if any application error occurs
     */
    public List<MarksheetBean> getMeritList(int pageNo, int pageSize) throws ApplicationException {
        log.debug("MarksheetModel getMeritList started");
        ArrayList<MarksheetBean> list = new ArrayList<MarksheetBean>();
        StringBuffer sql = new StringBuffer(
                "select id, roll_no, name, physics, chemistry, maths, (physics + chemistry + maths) as total from st_marksheet where physics > 33 and chemistry > 33 and maths > 33 order by total desc");

        if (pageSize > 0) {
            pageNo = (pageNo - 1) * pageSize;
            sql.append(" limit " + pageNo + ", " + pageSize);
        }

        Connection conn = null;

        try {
            conn = JDBCDataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql.toString());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                MarksheetBean bean = new MarksheetBean();
                bean.setId(rs.getLong(1));
                bean.setRollNo(rs.getString(2));
                bean.setName(rs.getString(3));
                bean.setPhysics(rs.getInt(4));
                bean.setChemistry(rs.getInt(5));
                bean.setMaths(rs.getInt(6));
                list.add(bean);
            }
            rs.close();
            pstmt.close();
            log.debug("MarksheetModel getMeritList completed with size: " + list.size());
        } catch (Exception e) {
            log.error("Exception in getMeritList", e);
            throw new ApplicationException("Exception in getting merit list of Marksheet");
        } finally {
            JDBCDataSource.closeConnection(conn);
        }
        return list;
    }
}
