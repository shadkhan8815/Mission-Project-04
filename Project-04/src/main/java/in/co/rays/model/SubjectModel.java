package in.co.rays.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import in.co.rays.bean.CourseBean;
import in.co.rays.bean.SubjectBean;
import in.co.rays.exception.ApplicationException;
import in.co.rays.exception.DatabaseException;
import in.co.rays.exception.DuplicateRecordException;
import in.co.rays.util.JDBCDataSource;

/**
 * SubjectModel class handles all CRUD operations and search functionality for
 * the Subject entity in the database. It interacts with the st_subject table
 * and provides methods to add, update, delete, and retrieve subject data.
 * 
 * @author Shad Khan
 * @version 1.0
 */
public class SubjectModel {

	private static Logger log = Logger.getLogger(SubjectModel.class);

	/**
	 * Get the next primary key value for the st_subject table.
	 * 
	 * @return the next primary key
	 * @throws DatabaseException if there is any database access error
	 */
	public Integer nextPk() throws DatabaseException {
		log.debug("SubjectModel nextPk started");
		Connection conn = null;
		int pk = 0;
		try {
			conn = JDBCDataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement("select max(id) from st_subject");
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
	 * Add a new Subject into the database.
	 * 
	 * @param bean the SubjectBean containing subject details
	 * @return the primary key of the newly inserted subject
	 * @throws ApplicationException     if any application-level error occurs
	 * @throws DuplicateRecordException if the subject name already exists
	 */
	public long add(SubjectBean bean) throws ApplicationException, DuplicateRecordException {
		log.debug("SubjectModel add started for subject: " + bean.getName());
		Connection conn = null;
		int pk = 0;

		CourseModel courseModel = new CourseModel();
		CourseBean courseBean = courseModel.findByPk(bean.getCourseId());
		bean.setCourseName(courseBean.getName());

		SubjectBean duplicateSubject = findByName(bean.getName());
		if (duplicateSubject != null) {
			log.error("Duplicate Subject found: " + bean.getName());
			throw new DuplicateRecordException("Subject Name already exists");
		}

		try {
			conn = JDBCDataSource.getConnection();
			pk = nextPk();
			conn.setAutoCommit(false); // Begin transaction
			PreparedStatement pstmt = conn.prepareStatement("insert into st_subject values(?, ?, ?, ?, ?, ?, ?, ?, ?)");
			pstmt.setInt(1, pk);
			pstmt.setString(2, bean.getName());
			pstmt.setLong(3, bean.getCourseId());
			pstmt.setString(4, bean.getCourseName());
			pstmt.setString(5, bean.getDescription());
			pstmt.setString(6, bean.getCreatedBy());
			pstmt.setString(7, bean.getModifiedBy());
			pstmt.setTimestamp(8, bean.getCreatedDatetime());
			pstmt.setTimestamp(9, bean.getModifiedDatetime());
			pstmt.executeUpdate();
			conn.commit(); // End transaction
			pstmt.close();
			log.debug("Subject added successfully with id: " + pk);
		} catch (Exception e) {
			log.error("Exception in adding Subject", e);
			try {
				conn.rollback();
			} catch (Exception ex) {
				log.error("Rollback exception in add Subject", ex);
				throw new ApplicationException("Exception : add rollback exception " + ex.getMessage());
			}
			throw new ApplicationException("Exception : Exception in add Subject");
		} finally {
			JDBCDataSource.closeConnection(conn);
		}
		return pk;
	}

	/**
	 * Update an existing subject in the database.
	 * 
	 * @param bean the SubjectBean containing updated subject details
	 * @throws ApplicationException     if any application-level error occurs
	 * @throws DuplicateRecordException if subject duplication occurs
	 */
	public void update(SubjectBean bean) throws ApplicationException, DuplicateRecordException {
		log.debug("SubjectModel update started for id: " + bean.getId());
		Connection conn = null;
		CourseModel courseModel = new CourseModel();
		CourseBean courseBean = courseModel.findByPk(bean.getCourseId());
		bean.setCourseName(courseBean.getName());
		try {
			conn = JDBCDataSource.getConnection();
			conn.setAutoCommit(false); // Begin transaction
			PreparedStatement pstmt = conn.prepareStatement(
					"update st_subject set name = ?, course_id = ?, course_name = ?, description = ?, created_by = ?, modified_by = ?, created_datetime = ?, modified_datetime = ? where id = ?");
			pstmt.setString(1, bean.getName());
			pstmt.setLong(2, bean.getCourseId());
			pstmt.setString(3, bean.getCourseName());
			pstmt.setString(4, bean.getDescription());
			pstmt.setString(5, bean.getCreatedBy());
			pstmt.setString(6, bean.getModifiedBy());
			pstmt.setTimestamp(7, bean.getCreatedDatetime());
			pstmt.setTimestamp(8, bean.getModifiedDatetime());
			pstmt.setLong(9, bean.getId());
			pstmt.executeUpdate();
			conn.commit(); // End transaction
			pstmt.close();
			log.debug("Subject updated successfully for id: " + bean.getId());
		} catch (Exception e) {
			log.error("Exception in updating Subject", e);
			try {
				conn.rollback();
			} catch (Exception ex) {
				log.error("Rollback exception in update Subject", ex);
				throw new ApplicationException("Exception : Delete rollback exception " + ex.getMessage());
			}
			throw new ApplicationException("Exception in updating Subject ");
		} finally {
			JDBCDataSource.closeConnection(conn);
		}
	}

	/**
	 * Delete a subject from the database.
	 * 
	 * @param bean the SubjectBean containing subject ID to delete
	 * @throws ApplicationException if any error occurs while deleting
	 */
	public void delete(SubjectBean bean) throws ApplicationException {
		log.debug("SubjectModel delete started for id: " + bean.getId());
		Connection conn = null;
		try {
			conn = JDBCDataSource.getConnection();
			conn.setAutoCommit(false); // Begin transaction
			PreparedStatement pstmt = conn.prepareStatement("delete from st_subject where id = ?");
			pstmt.setLong(1, bean.getId());
			pstmt.executeUpdate();
			conn.commit(); // End transaction
			pstmt.close();
			log.debug("Subject deleted successfully for id: " + bean.getId());
		} catch (Exception e) {
			log.error("Exception in deleting Subject", e);
			try {
				conn.rollback();
			} catch (Exception ex) {
				log.error("Rollback exception in delete Subject", ex);
				throw new ApplicationException("Exception : Delete rollback exception " + ex.getMessage());
			}
			throw new ApplicationException("Exception : Exception in delete Subject");
		} finally {
			JDBCDataSource.closeConnection(conn);
		}
	}

	/**
	 * Find subject by primary key.
	 * 
	 * @param pk the primary key
	 * @return SubjectBean containing subject details, null if not found
	 * @throws ApplicationException if any error occurs while fetching data
	 */
	public SubjectBean findByPk(long pk) throws ApplicationException {
		log.debug("SubjectModel findByPk started for id: " + pk);
		StringBuffer sql = new StringBuffer("select * from st_subject where id = ?");
		SubjectBean bean = null;
		Connection conn = null;
		try {
			conn = JDBCDataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(1, pk);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				bean = new SubjectBean();
				bean.setId(rs.getLong(1));
				bean.setName(rs.getString(2));
				bean.setCourseId(rs.getLong(3));
				bean.setCourseName(rs.getString(4));
				bean.setDescription(rs.getString(5));
				bean.setCreatedBy(rs.getString(6));
				bean.setModifiedBy(rs.getString(7));
				bean.setCreatedDatetime(rs.getTimestamp(8));
				bean.setModifiedDatetime(rs.getTimestamp(9));
			}
			rs.close();
			pstmt.close();
			log.debug("SubjectModel findByPk completed for id: " + pk);
		} catch (Exception e) {
			log.error("Exception in findByPk", e);
			throw new ApplicationException("Exception : Exception in getting Subject by pk");
		} finally {
			JDBCDataSource.closeConnection(conn);
		}
		return bean;
	}

	/**
	 * Find subject by subject name.
	 * 
	 * @param name the subject name
	 * @return SubjectBean containing subject details, null if not found
	 * @throws ApplicationException if any error occurs while fetching data
	 */
	public SubjectBean findByName(String name) throws ApplicationException {
		log.debug("SubjectModel findByName started for subject: " + name);
		StringBuffer sql = new StringBuffer("select * from st_subject where name = ?");
		SubjectBean bean = null;
		Connection conn = null;
		try {
			conn = JDBCDataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, name);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				bean = new SubjectBean();
				bean.setId(rs.getLong(1));
				bean.setName(rs.getString(2));
				bean.setCourseId(rs.getLong(3));
				bean.setCourseName(rs.getString(4));
				bean.setDescription(rs.getString(5));
				bean.setCreatedBy(rs.getString(6));
				bean.setModifiedBy(rs.getString(7));
				bean.setCreatedDatetime(rs.getTimestamp(8));
				bean.setModifiedDatetime(rs.getTimestamp(9));
			}
			rs.close();
			pstmt.close();
			log.debug("SubjectModel findByName completed for subject: " + name);
		} catch (Exception e) {
			log.error("Exception in findByName", e);
			throw new ApplicationException("Exception : Exception in getting Subject by Subject Name");
		} finally {
			JDBCDataSource.closeConnection(conn);
		}
		return bean;
	}

	/**
	 * Get list of all subjects.
	 * 
	 * @return list of SubjectBean
	 * @throws ApplicationException if any error occurs while fetching data
	 */
	public List<SubjectBean> list() throws ApplicationException {
		log.debug("SubjectModel list started");
		List<SubjectBean> list = search(null, 0, 0);
		log.debug("SubjectModel list completed with size: " + list.size());
		return list;
	}

	/**
	 * Search subjects based on given criteria.
	 * 
	 * @param bean     SubjectBean containing search criteria
	 * @param pageNo   the current page number
	 * @param pageSize number of records per page
	 * @return list of matching SubjectBean
	 * @throws ApplicationException if any error occurs while searching
	 */
	public List<SubjectBean> search(SubjectBean bean, int pageNo, int pageSize) throws ApplicationException {
		log.debug("SubjectModel search started");
		StringBuffer sql = new StringBuffer("select * from st_subject where 1=1");

		if (bean != null) {
			if (bean.getId() > 0) {
				sql.append(" and id = " + bean.getId());
			}
			if (bean.getName() != null && bean.getName().length() > 0) {
				sql.append(" and name like '" + bean.getName() + "%'");
			}
			if (bean.getCourseId() > 0) {
				sql.append(" and course_id = " + bean.getCourseId());
			}
			if (bean.getCourseName() != null && bean.getCourseName().length() > 0) {
				sql.append(" and course_name like '" + bean.getCourseName() + "%'");
			}
			if (bean.getDescription() != null && bean.getDescription().length() > 0) {
				sql.append(" and description like '" + bean.getDescription() + "%'");
			}
		}

		if (pageSize > 0) {
			pageNo = (pageNo - 1) * pageSize;
			sql.append(" limit " + pageNo + ", " + pageSize);
		}

		ArrayList<SubjectBean> list = new ArrayList<SubjectBean>();
		Connection conn = null;
		try {
			conn = JDBCDataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				bean = new SubjectBean();
				bean.setId(rs.getLong(1));
				bean.setName(rs.getString(2));
				bean.setCourseId(rs.getLong(3));
				bean.setCourseName(rs.getString(4));
				bean.setDescription(rs.getString(5));
				bean.setCreatedBy(rs.getString(6));
				bean.setModifiedBy(rs.getString(7));
				bean.setCreatedDatetime(rs.getTimestamp(8));
				bean.setModifiedDatetime(rs.getTimestamp(9));
				list.add(bean);
			}
			rs.close();
			pstmt.close();
			log.debug("SubjectModel search completed with size: " + list.size());
		} catch (Exception e) {
			log.error("Exception in search Subject", e);
			throw new ApplicationException("Exception : Exception in search Subject");
		} finally {
			JDBCDataSource.closeConnection(conn);
		}
		return list;
	}
}
