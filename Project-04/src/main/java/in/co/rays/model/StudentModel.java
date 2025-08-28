package in.co.rays.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import in.co.rays.bean.CollegeBean;
import in.co.rays.bean.StudentBean;
import in.co.rays.exception.ApplicationException;
import in.co.rays.exception.DatabaseException;
import in.co.rays.exception.DuplicateRecordException;
import in.co.rays.util.JDBCDataSource;

/**
 * StudentModel is a model class that provides methods to perform CRUD
 * operations on the st_student database table.
 * 
 * It includes methods for adding, updating, deleting, searching, and finding
 * students by primary key and email ID.
 * 
 * This class also handles duplicate checks and ensures proper transaction
 * management with rollback support.
 * 
 * @author Shad Khan
 * @version 1.0
 */
public class StudentModel {

	private static Logger log = Logger.getLogger(StudentModel.class);

	/**
	 * Gets the next primary key value for the st_student table.
	 *
	 * @return the next primary key as Integer
	 * @throws DatabaseException if any database error occurs
	 */
	public Integer nextPk() throws DatabaseException {
		log.debug("StudentModel nextPk started");
		Connection conn = null;
		int pk = 0;
		try {
			conn = JDBCDataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement("select max(id) from st_student");
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				pk = rs.getInt(1);
			}
			rs.close();
			pstmt.close();
			log.debug("Next PK retrieved: " + (pk + 1));
		} catch (Exception e) {
			log.error("Database exception in nextPk", e);
			throw new DatabaseException("Exception : Exception in getting PK");
		} finally {
			JDBCDataSource.closeConnection(conn);
		}
		return pk + 1;
	}

	/**
	 * Adds a new Student record into the database.
	 *
	 * @param bean StudentBean object containing student details
	 * @return the primary key of the newly added student
	 * @throws ApplicationException     if any application level exception occurs
	 * @throws DuplicateRecordException if the email ID already exists
	 */
	public long add(StudentBean bean) throws ApplicationException, DuplicateRecordException {
		log.debug("StudentModel add started for email: " + bean.getEmail());
		Connection conn = null;

		CollegeModel collegeModel = new CollegeModel();
		CollegeBean collegeBean = collegeModel.findByPk(bean.getCollegeId());
		bean.setCollegeName(collegeBean.getName());

		StudentBean existBean = findByEmailId(bean.getEmail());
		int pk = 0;

		if (existBean != null) {
			log.error("Duplicate email found: " + bean.getEmail());
			throw new DuplicateRecordException("Email already exists");
		}

		try {
			pk = nextPk();
			conn = JDBCDataSource.getConnection();
			conn.setAutoCommit(false); // Begin transaction
			PreparedStatement pstmt = conn
					.prepareStatement("insert into st_student values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			pstmt.setInt(1, pk);
			pstmt.setString(2, bean.getFirstName());
			pstmt.setString(3, bean.getLastName());
			pstmt.setDate(4, new java.sql.Date(bean.getDob().getTime()));
			pstmt.setString(5, bean.getGender());
			pstmt.setString(6, bean.getMobileNo());
			pstmt.setString(7, bean.getEmail());
			pstmt.setLong(8, bean.getCollegeId());
			pstmt.setString(9, bean.getCollegeName());
			pstmt.setString(10, bean.getCreatedBy());
			pstmt.setString(11, bean.getModifiedBy());
			pstmt.setTimestamp(12, bean.getCreatedDatetime());
			pstmt.setTimestamp(13, bean.getModifiedDatetime());
			pstmt.executeUpdate();
			conn.commit(); // End transaction
			pstmt.close();
			log.debug("Student added successfully with id: " + pk);
		} catch (Exception e) {
			log.error("Exception in adding Student", e);
			try {
				conn.rollback();
			} catch (Exception ex) {
				log.error("Rollback exception in add Student", ex);
				throw new ApplicationException("Exception : add rollback exception " + ex.getMessage());
			}
			throw new ApplicationException("Exception : Exception in add Student");
		} finally {
			JDBCDataSource.closeConnection(conn);
		}
		return pk;
	}

	/**
	 * Updates an existing Student record in the database.
	 *
	 * @param bean StudentBean object containing updated student details
	 * @throws ApplicationException     if any application level exception occurs
	 * @throws DuplicateRecordException if the email ID already exists for another
	 *                                  student
	 */
	public void update(StudentBean bean) throws ApplicationException, DuplicateRecordException {
		log.debug("StudentModel update started for id: " + bean.getId());
		Connection conn = null;

		StudentBean existBean = findByEmailId(bean.getEmail());
		if (existBean != null && existBean.getId() != bean.getId()) {
			log.error("Duplicate email found during update: " + bean.getEmail());
			throw new DuplicateRecordException("Email Id is already exist");
		}

		CollegeModel collegeModel = new CollegeModel();
		CollegeBean collegeBean = collegeModel.findByPk(bean.getCollegeId());
		bean.setCollegeName(collegeBean.getName());

		try {
			conn = JDBCDataSource.getConnection();
			conn.setAutoCommit(false); // Begin transaction
			PreparedStatement pstmt = conn.prepareStatement(
					"update st_student set first_name = ?, last_name = ?, dob = ?, gender = ?, mobile_no = ?, email = ?, college_id = ?, college_name = ?, created_by = ?, modified_by = ?, created_datetime = ?, modified_datetime = ? where id = ?");
			pstmt.setString(1, bean.getFirstName());
			pstmt.setString(2, bean.getLastName());
			pstmt.setDate(3, new java.sql.Date(bean.getDob().getTime()));
			pstmt.setString(4, bean.getGender());
			pstmt.setString(5, bean.getMobileNo());
			pstmt.setString(6, bean.getEmail());
			pstmt.setLong(7, bean.getCollegeId());
			pstmt.setString(8, bean.getCollegeName());
			pstmt.setString(9, bean.getCreatedBy());
			pstmt.setString(10, bean.getModifiedBy());
			pstmt.setTimestamp(11, bean.getCreatedDatetime());
			pstmt.setTimestamp(12, bean.getModifiedDatetime());
			pstmt.setLong(13, bean.getId());
			pstmt.executeUpdate();
			conn.commit(); // End transaction
			pstmt.close();
			log.debug("Student updated successfully for id: " + bean.getId());
		} catch (Exception e) {
			log.error("Exception in updating Student", e);
			try {
				conn.rollback();
			} catch (Exception ex) {
				log.error("Rollback exception in update Student", ex);
				throw new ApplicationException("Exception : Delete rollback exception " + ex.getMessage());
			}
			throw new ApplicationException("Exception in updating Student");
		} finally {
			JDBCDataSource.closeConnection(conn);
		}
	}

	/**
	 * Deletes a Student record from the database.
	 *
	 * @param bean StudentBean object containing student details
	 * @throws ApplicationException if any exception occurs during deletion
	 */
	public void delete(StudentBean bean) throws ApplicationException {
		log.debug("StudentModel delete started for id: " + bean.getId());
		Connection conn = null;

		try {
			conn = JDBCDataSource.getConnection();
			conn.setAutoCommit(false); // Begin transaction
			PreparedStatement pstmt = conn.prepareStatement("delete from st_student where id = ?");
			pstmt.setLong(1, bean.getId());
			pstmt.executeUpdate();
			conn.commit(); // End transaction
			pstmt.close();
			log.debug("Student deleted successfully for id: " + bean.getId());
		} catch (Exception e) {
			log.error("Exception in deleting Student", e);
			try {
				conn.rollback();
			} catch (Exception ex) {
				log.error("Rollback exception in delete Student", ex);
				throw new ApplicationException("Exception : Delete rollback exception " + ex.getMessage());
			}
			throw new ApplicationException("Exception : Exception in delete Student");
		} finally {
			JDBCDataSource.closeConnection(conn);
		}
	}

	/**
	 * Finds a Student by its primary key.
	 *
	 * @param pk primary key of the Student
	 * @return StudentBean object containing student details, or null if not found
	 * @throws ApplicationException if any database error occurs
	 */
	public StudentBean findByPk(long pk) throws ApplicationException {
		log.debug("StudentModel findByPk started for id: " + pk);
		StringBuffer sql = new StringBuffer("select * from st_student where id = ?");
		StudentBean bean = null;
		Connection conn = null;

		try {
			conn = JDBCDataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(1, pk);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				bean = new StudentBean();
				bean.setId(rs.getLong(1));
				bean.setFirstName(rs.getString(2));
				bean.setLastName(rs.getString(3));
				bean.setDob(rs.getDate(4));
				bean.setGender(rs.getString(5));
				bean.setMobileNo(rs.getString(6));
				bean.setEmail(rs.getString(7));
				bean.setCollegeId(rs.getLong(8));
				bean.setCollegeName(rs.getString(9));
				bean.setCreatedBy(rs.getString(10));
				bean.setModifiedBy(rs.getString(11));
				bean.setCreatedDatetime(rs.getTimestamp(12));
				bean.setModifiedDatetime(rs.getTimestamp(13));
			}
			rs.close();
			pstmt.close();
			log.debug("StudentModel findByPk completed for id: " + pk);
		} catch (Exception e) {
			log.error("Exception in findByPk", e);
			throw new ApplicationException("Exception : Exception in getting User by pk");
		} finally {
			JDBCDataSource.closeConnection(conn);
		}
		return bean;
	}

	/**
	 * Finds a Student by email ID.
	 *
	 * @param Email email ID of the Student
	 * @return StudentBean object if found, otherwise null
	 * @throws ApplicationException if any database error occurs
	 */
	public StudentBean findByEmailId(String Email) throws ApplicationException {
		log.debug("StudentModel findByEmailId started for email: " + Email);
		StringBuffer sql = new StringBuffer("select * from st_student where email = ?");
		StudentBean bean = null;
		Connection conn = null;

		try {
			conn = JDBCDataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, Email);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				bean = new StudentBean();
				bean.setId(rs.getLong(1));
				bean.setFirstName(rs.getString(2));
				bean.setLastName(rs.getString(3));
				bean.setDob(rs.getDate(4));
				bean.setGender(rs.getString(5));
				bean.setMobileNo(rs.getString(6));
				bean.setEmail(rs.getString(7));
				bean.setCollegeId(rs.getLong(8));
				bean.setCollegeName(rs.getString(9));
				bean.setCreatedBy(rs.getString(10));
				bean.setModifiedBy(rs.getString(11));
				bean.setCreatedDatetime(rs.getTimestamp(12));
				bean.setModifiedDatetime(rs.getTimestamp(13));
			}
			rs.close();
			pstmt.close();
			log.debug("StudentModel findByEmailId completed for email: " + Email);
		} catch (Exception e) {
			log.error("Exception in findByEmailId", e);
			throw new ApplicationException("Exception : Exception in getting User by Email");
		} finally {
			JDBCDataSource.closeConnection(conn);
		}
		return bean;
	}

	/**
	 * Returns a list of all Students.
	 *
	 * @return list of StudentBean
	 * @throws ApplicationException if any database error occurs
	 */
	public List<StudentBean> list() throws ApplicationException {
		log.debug("StudentModel list started");
		List<StudentBean> list = search(null, 0, 0);
		log.debug("StudentModel list completed with size: " + list.size());
		return list;
	}

	/**
	 * Searches for Students based on given criteria.
	 *
	 * @param bean     StudentBean object containing search criteria
	 * @param pageNo   page number for pagination
	 * @param pageSize number of records per page
	 * @return list of StudentBean matching the search criteria
	 * @throws ApplicationException if any database error occurs
	 */
	public List<StudentBean> search(StudentBean bean, int pageNo, int pageSize) throws ApplicationException {
		log.debug("StudentModel search started");
		StringBuffer sql = new StringBuffer("select * from st_student where 1 = 1");

		if (bean != null) {
			if (bean.getId() > 0) {
				sql.append(" and id = " + bean.getId());
			}
			if (bean.getFirstName() != null && bean.getFirstName().length() > 0) {
				sql.append(" and first_name like '" + bean.getFirstName() + "%'");
			}
			if (bean.getLastName() != null && bean.getLastName().length() > 0) {
				sql.append(" and last_name like '" + bean.getLastName() + "%'");
			}
			if (bean.getDob() != null && bean.getDob().getDate() > 0) {
				sql.append(" and dob = " + bean.getDob());
			}
			if (bean.getGender() != null && bean.getGender().length() > 0) {
				sql.append(" and gender like '" + bean.getGender() + "%'");
			}
			if (bean.getMobileNo() != null && bean.getMobileNo().length() > 0) {
				sql.append(" and mobile_no like '" + bean.getMobileNo() + "%'");
			}
			if (bean.getEmail() != null && bean.getEmail().length() > 0) {
				sql.append(" and email like '" + bean.getEmail() + "%'");
			}
			if (bean.getCollegeName() != null && bean.getCollegeName().length() > 0) {
				sql.append(" and college_name = " + bean.getCollegeName());
			}
		}
		if (pageSize > 0) {
			pageNo = (pageNo - 1) * pageSize;
			sql.append(" limit " + pageNo + ", " + pageSize);
		}

		ArrayList<StudentBean> list = new ArrayList<StudentBean>();
		Connection conn = null;
		try {
			conn = JDBCDataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				bean = new StudentBean();
				bean.setId(rs.getLong(1));
				bean.setFirstName(rs.getString(2));
				bean.setLastName(rs.getString(3));
				bean.setDob(rs.getDate(4));
				bean.setGender(rs.getString(5));
				bean.setMobileNo(rs.getString(6));
				bean.setEmail(rs.getString(7));
				bean.setCollegeId(rs.getLong(8));
				bean.setCollegeName(rs.getString(9));
				bean.setCreatedBy(rs.getString(10));
				bean.setModifiedBy(rs.getString(11));
				bean.setCreatedDatetime(rs.getTimestamp(12));
				bean.setModifiedDatetime(rs.getTimestamp(13));
				list.add(bean);
			}
			rs.close();
			pstmt.close();
			log.debug("StudentModel search completed with size: " + list.size());
		} catch (Exception e) {
			log.error("Exception in search Student", e);
			throw new ApplicationException("Exception : Exception in search Student");
		} finally {
			JDBCDataSource.closeConnection(conn);
		}
		return list;
	}
}
