package in.co.rays.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import in.co.rays.bean.CourseBean;
import in.co.rays.bean.SubjectBean;
import in.co.rays.bean.TimetableBean;
import in.co.rays.exception.ApplicationException;
import in.co.rays.exception.DatabaseException;
import in.co.rays.exception.DuplicateRecordException;
import in.co.rays.util.JDBCDataSource;

/**
 * TimetableModel class handles all database operations related to Timetable. It
 * includes methods for add, update, delete, search, and validations such as
 * checking duplicates by course, subject, semester, and exam time.
 * 
 * @author Shad Khan
 * @version 1.0
 */
public class TimetableModel {

	private static Logger log = Logger.getLogger(TimetableModel.class);

	/**
	 * Get the next primary key value from the database.
	 *
	 * @return next PK as Integer
	 * @throws DatabaseException if any database error occurs
	 */
	public Integer nextPk() throws DatabaseException {
		log.debug("TimetableModel nextPk started");
		Connection conn = null;
		int pk = 0;
		try {
			conn = JDBCDataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement("select max(id) from st_timetable");
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
	 * Add a new Timetable entry.
	 *
	 * @param bean TimetableBean object
	 * @return primary key of newly added record
	 * @throws ApplicationException     if application error occurs
	 * @throws DuplicateRecordException if duplicate timetable exists
	 */
	public long add(TimetableBean bean) throws ApplicationException, DuplicateRecordException {
		log.debug("TimetableModel add started for courseId: " + bean.getCourseId() + ", subjectId: "
				+ bean.getSubjectId());
		Connection conn = null;
		int pk = 0;

		CourseModel courseModel = new CourseModel();
		CourseBean courseBean = courseModel.findByPk(bean.getCourseId());
		bean.setCourseName(courseBean.getName());

		SubjectModel subjectModel = new SubjectModel();
		SubjectBean subjectBean = subjectModel.findByPk(bean.getSubjectId());
		bean.setSubjectName(subjectBean.getName());

		try {
			conn = JDBCDataSource.getConnection();
			pk = nextPk();
			conn.setAutoCommit(false); // Begin transaction

			PreparedStatement pstmt = conn
					.prepareStatement("insert into st_timetable values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			pstmt.setInt(1, pk);
			pstmt.setString(2, bean.getSemester());
			pstmt.setString(3, bean.getDescription());
			pstmt.setDate(4, new java.sql.Date(bean.getExamDate().getTime()));
			pstmt.setString(5, bean.getExamTime());
			pstmt.setLong(6, bean.getCourseId());
			pstmt.setString(7, bean.getCourseName());
			pstmt.setLong(8, bean.getSubjectId());
			pstmt.setString(9, bean.getSubjectName());
			pstmt.setString(10, bean.getCreatedBy());
			pstmt.setString(11, bean.getModifiedBy());
			pstmt.setTimestamp(12, bean.getCreatedDatetime());
			pstmt.setTimestamp(13, bean.getModifiedDatetime());
			pstmt.executeUpdate();
			conn.commit(); // End transaction
			pstmt.close();
			log.debug("Timetable added successfully with id: " + pk);
		} catch (Exception e) {
			log.error("Exception in adding Timetable", e);
			try {
				conn.rollback();
			} catch (Exception ex) {
				log.error("Rollback exception in add Timetable", ex);
				throw new ApplicationException("Exception : add rollback exception " + ex.getMessage());
			}
			throw new ApplicationException("Exception : Exception in add Timetable");
		} finally {
			JDBCDataSource.closeConnection(conn);
		}
		return pk;
	}

	/**
	 * Update existing Timetable entry.
	 *
	 * @param bean TimetableBean object
	 * @throws ApplicationException     if application error occurs
	 * @throws DuplicateRecordException if duplicate timetable exists
	 */
	public void update(TimetableBean bean) throws ApplicationException, DuplicateRecordException {
		log.debug("TimetableModel update started for id: " + bean.getId());
		Connection conn = null;

		CourseModel courseModel = new CourseModel();
		CourseBean courseBean = courseModel.findByPk(bean.getCourseId());
		bean.setCourseName(courseBean.getName());

		SubjectModel subjectModel = new SubjectModel();
		SubjectBean subjectBean = subjectModel.findByPk(bean.getSubjectId());
		bean.setSubjectName(subjectBean.getName());

		try {
			conn = JDBCDataSource.getConnection();
			conn.setAutoCommit(false); // Begin transaction

			PreparedStatement pstmt = conn.prepareStatement(
					"update st_timetable set semester = ?, description = ?, exam_date = ?, exam_time = ?, course_id = ?, course_name = ?, subject_id = ?, subject_name = ?, created_by = ?, modified_by = ?, created_datetime = ?, modified_datetime = ? where id = ?");
			pstmt.setString(1, bean.getSemester());
			pstmt.setString(2, bean.getDescription());
			pstmt.setDate(3, new java.sql.Date(bean.getExamDate().getTime()));
			pstmt.setString(4, bean.getExamTime());
			pstmt.setLong(5, bean.getCourseId());
			pstmt.setString(6, bean.getCourseName());
			pstmt.setLong(7, bean.getSubjectId());
			pstmt.setString(8, bean.getSubjectName());
			pstmt.setString(9, bean.getCreatedBy());
			pstmt.setString(10, bean.getModifiedBy());
			pstmt.setTimestamp(11, bean.getCreatedDatetime());
			pstmt.setTimestamp(12, bean.getModifiedDatetime());
			pstmt.setLong(13, bean.getId());
			pstmt.executeUpdate();
			conn.commit(); // End transaction
			pstmt.close();
			log.debug("Timetable updated successfully for id: " + bean.getId());
		} catch (Exception e) {
			log.error("Exception in updating Timetable", e);
			try {
				conn.rollback();
			} catch (Exception ex) {
				log.error("Rollback exception in update Timetable", ex);
				throw new ApplicationException("Exception : Delete rollback exception " + ex.getMessage());
			}
			throw new ApplicationException("Exception in updating Timetable ");
		} finally {
			JDBCDataSource.closeConnection(conn);
		}
	}

	/**
	 * Delete Timetable entry.
	 *
	 * @param bean TimetableBean object
	 * @throws ApplicationException if application error occurs
	 */
	public void delete(TimetableBean bean) throws ApplicationException {
		log.debug("TimetableModel delete started for id: " + bean.getId());
		Connection conn = null;
		try {
			conn = JDBCDataSource.getConnection();
			conn.setAutoCommit(false); // Begin transaction
			PreparedStatement pstmt = conn.prepareStatement("DELETE FROM ST_TIMETABLE WHERE ID=?");
			pstmt.setLong(1, bean.getId());
			pstmt.executeUpdate();
			conn.commit(); // End transaction
			pstmt.close();
			log.debug("Timetable deleted successfully for id: " + bean.getId());
		} catch (Exception e) {
			log.error("Exception in deleting Timetable", e);
			try {
				conn.rollback();
			} catch (Exception ex) {
				log.error("Rollback exception in delete Timetable", ex);
				throw new ApplicationException("Exception : Delete rollback exception " + ex.getMessage());
			}
			throw new ApplicationException("Exception : Exception in delete Timetable");
		} finally {
			JDBCDataSource.closeConnection(conn);
		}
	}

	/**
	 * Find Timetable by Primary Key.
	 *
	 * @param pk Primary key
	 * @return TimetableBean object
	 * @throws ApplicationException if application error occurs
	 */
	public TimetableBean findByPk(long pk) throws ApplicationException {
		log.debug("TimetableModel findByPk started for id: " + pk);
		StringBuffer sql = new StringBuffer("select * from st_timetable where id = ?");
		TimetableBean bean = null;
		Connection conn = null;
		try {
			conn = JDBCDataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(1, pk);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				bean = new TimetableBean();
				bean.setId(rs.getLong(1));
				bean.setSemester(rs.getString(2));
				bean.setDescription(rs.getString(3));
				bean.setExamDate(rs.getDate(4));
				bean.setExamTime(rs.getString(5));
				bean.setCourseId(rs.getLong(6));
				bean.setCourseName(rs.getString(7));
				bean.setSubjectId(rs.getLong(8));
				bean.setSubjectName(rs.getString(9));
				bean.setCreatedBy(rs.getString(10));
				bean.setModifiedBy(rs.getString(11));
				bean.setCreatedDatetime(rs.getTimestamp(12));
				bean.setModifiedDatetime(rs.getTimestamp(13));
			}
			rs.close();
			pstmt.close();
			log.debug("TimetableModel findByPk completed for id: " + pk);
		} catch (Exception e) {
			log.error("Exception in findByPk", e);
			throw new ApplicationException("Exception : Exception in getting Timetable by pk");
		} finally {
			JDBCDataSource.closeConnection(conn);
		}
		return bean;
	}

	/**
	 * Check Timetable by Course name and Exam Date.
	 *
	 * @param courseId Course ID
	 * @param examDate Exam Date
	 * @return TimetableBean object
	 * @throws ApplicationException if application error occurs
	 */
	public TimetableBean checkByCourseName(Long courseId, Date examDate) throws ApplicationException {
		log.debug("checkByCourseName started for courseId: " + courseId + ", examDate: " + examDate);
		StringBuffer sql = new StringBuffer("select * from st_timetable where course_id = ? and exam_date = ?");
		TimetableBean bean = null;
		Connection conn = null;
		try {
			conn = JDBCDataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(1, courseId);
			pstmt.setDate(2, new java.sql.Date(examDate.getTime()));
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				bean = new TimetableBean();
				bean.setId(rs.getLong(1));
				bean.setSemester(rs.getString(2));
				bean.setDescription(rs.getString(3));
				bean.setExamDate(rs.getDate(4));
				bean.setExamTime(rs.getString(5));
				bean.setCourseId(rs.getLong(6));
				bean.setCourseName(rs.getString(7));
				bean.setSubjectId(rs.getLong(8));
				bean.setSubjectName(rs.getString(9));
				bean.setCreatedBy(rs.getString(10));
				bean.setModifiedBy(rs.getString(11));
				bean.setCreatedDatetime(rs.getTimestamp(12));
				bean.setModifiedDatetime(rs.getTimestamp(13));
			}
			rs.close();
			pstmt.close();
			log.debug("checkByCourseName completed for courseId: " + courseId);
		} catch (Exception e) {
			log.error("Exception in checkByCourseName", e);
			throw new ApplicationException("Exception : Exception in get Timetable");
		} finally {
			JDBCDataSource.closeConnection(conn);
		}
		return bean;
	}

	/**
	 * Check Timetable by Subject name.
	 *
	 * @param courseId  Course ID
	 * @param subjectId Subject ID
	 * @param examDate  Exam Date
	 * @return TimetableBean object
	 * @throws ApplicationException if application error occurs
	 */
	public TimetableBean checkBySubjectName(Long courseId, Long subjectId, Date examDate) throws ApplicationException {
		log.debug("checkBySubjectName started for courseId: " + courseId + ", subjectId: " + subjectId);
		StringBuffer sql = new StringBuffer(
				"select * from st_timetable where course_id = ? and subject_id = ? and exam_date = ?");
		TimetableBean bean = null;
		Connection conn = null;
		try {
			conn = JDBCDataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(1, courseId);
			pstmt.setLong(2, subjectId);
			pstmt.setDate(3, new java.sql.Date(examDate.getTime()));
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				bean = new TimetableBean();
				bean.setId(rs.getLong(1));
				bean.setSemester(rs.getString(2));
				bean.setDescription(rs.getString(3));
				bean.setExamDate(rs.getDate(4));
				bean.setExamTime(rs.getString(5));
				bean.setCourseId(rs.getLong(6));
				bean.setCourseName(rs.getString(7));
				bean.setSubjectId(rs.getLong(8));
				bean.setSubjectName(rs.getString(9));
				bean.setCreatedBy(rs.getString(10));
				bean.setModifiedBy(rs.getString(11));
				bean.setCreatedDatetime(rs.getTimestamp(12));
				bean.setModifiedDatetime(rs.getTimestamp(13));
			}
			rs.close();
			pstmt.close();
			log.debug("checkBySubjectName completed for courseId: " + courseId + ", subjectId: " + subjectId);
		} catch (Exception e) {
			log.error("Exception in checkBySubjectName", e);
			throw new ApplicationException("Exception : Exception in get Timetable");
		} finally {
			JDBCDataSource.closeConnection(conn);
		}
		return bean;
	}

	/**
	 * Check Timetable by Semester.
	 *
	 * @param courseId  Course ID
	 * @param subjectId Subject ID
	 * @param semester  Semester
	 * @param examDate  Exam Date
	 * @return TimetableBean object
	 * @throws ApplicationException if application error occurs
	 */
	public TimetableBean checkBySemester(Long courseId, Long subjectId, String semester, Date examDate)
			throws ApplicationException {
		log.debug("checkBySemester started for courseId: " + courseId + ", subjectId: " + subjectId + ", semester: "
				+ semester);
		StringBuffer sql = new StringBuffer(
				"select * from st_timetable where course_id = ? and subject_id = ? and semester = ? and exam_date = ?");
		TimetableBean bean = null;
		Connection conn = null;
		try {
			conn = JDBCDataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(1, courseId);
			pstmt.setLong(2, subjectId);
			pstmt.setString(3, semester);
			pstmt.setDate(4, new java.sql.Date(examDate.getTime()));
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				bean = new TimetableBean();
				bean.setId(rs.getLong(1));
				bean.setSemester(rs.getString(2));
				bean.setDescription(rs.getString(3));
				bean.setExamDate(rs.getDate(4));
				bean.setExamTime(rs.getString(5));
				bean.setCourseId(rs.getLong(6));
				bean.setCourseName(rs.getString(7));
				bean.setSubjectId(rs.getLong(8));
				bean.setSubjectName(rs.getString(9));
				bean.setCreatedBy(rs.getString(10));
				bean.setModifiedBy(rs.getString(11));
				bean.setCreatedDatetime(rs.getTimestamp(12));
				bean.setModifiedDatetime(rs.getTimestamp(13));
			}
			rs.close();
			pstmt.close();
			log.debug("checkBySemester completed for courseId: " + courseId + ", semester: " + semester);
		} catch (Exception e) {
			log.error("Exception in checkBySemester", e);
			throw new ApplicationException("Exception : Exception in get Timetable");
		} finally {
			JDBCDataSource.closeConnection(conn);
		}
		return bean;
	}

	/**
	 * Check Timetable by Exam Time.
	 *
	 * @param courseId    Course ID
	 * @param subjectId   Subject ID
	 * @param semester    Semester
	 * @param examDate    Exam Date
	 * @param examTime    Exam Time
	 * @param description Description
	 * @return TimetableBean object
	 * @throws ApplicationException if application error occurs
	 */
	public TimetableBean checkByExamTime(Long courseId, Long subjectId, String semester, Date examDate, String examTime,
			String description) throws ApplicationException {
		log.debug("checkByExamTime started for courseId: " + courseId + ", subjectId: " + subjectId + ", examTime: "
				+ examTime);
		StringBuffer sql = new StringBuffer(
				"select * from st_timetable where course_id = ? and subject_id = ? and semester = ? and exam_date = ? and exam_time = ? and description = ?");
		TimetableBean bean = null;
		Connection conn = null;
		try {
			conn = JDBCDataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(1, courseId);
			pstmt.setLong(2, subjectId);
			pstmt.setString(3, semester);
			pstmt.setDate(4, new java.sql.Date(examDate.getTime()));
			pstmt.setString(5, examTime);
			pstmt.setString(6, description);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				bean = new TimetableBean();
				bean.setId(rs.getLong(1));
				bean.setSemester(rs.getString(2));
				bean.setDescription(rs.getString(3));
				bean.setExamDate(rs.getDate(4));
				bean.setExamTime(rs.getString(5));
				bean.setCourseId(rs.getLong(6));
				bean.setCourseName(rs.getString(7));
				bean.setSubjectId(rs.getLong(8));
				bean.setSubjectName(rs.getString(9));
				bean.setCreatedBy(rs.getString(10));
				bean.setModifiedBy(rs.getString(11));
				bean.setCreatedDatetime(rs.getTimestamp(12));
				bean.setModifiedDatetime(rs.getTimestamp(13));
			}
			rs.close();
			pstmt.close();
			log.debug("checkByExamTime completed for courseId: " + courseId + ", examTime: " + examTime);
		} catch (Exception e) {
			log.error("Exception in checkByExamTime", e);
			throw new ApplicationException("Exception : Exception in get Timetable");
		} finally {
			JDBCDataSource.closeConnection(conn);
		}
		return bean;
	}
	
	/**
	 * Retrieves a complete list of all Timetable records.
	 * 
	 * This method internally calls the {@link #search(TimetableBean, int, int)} method
	 * with null criteria and no pagination to fetch all records from the timetable table.
	 *
	 * @return List of TimetableBean objects containing all timetable entries
	 * @throws ApplicationException if an error occurs during database access
	 */
	public List<TimetableBean> list() throws ApplicationException {
	    log.debug("SubjectModel list started");
	    List<TimetableBean> list = search(null, 0, 0);
	    log.debug("SubjectModel list completed with size: " + list.size());
	    return list;
	}


	/**
	 * Search Timetable records.
	 *
	 * @param bean     TimetableBean object (search criteria)
	 * @param pageNo   Page number
	 * @param pageSize Number of records per page
	 * @return list of TimetableBean objects
	 * @throws ApplicationException if application error occurs
	 */
	public List<TimetableBean> search(TimetableBean bean, int pageNo, int pageSize) throws ApplicationException {
		log.debug("search started for TimetableBean: " + bean + ", pageNo: " + pageNo + ", pageSize: " + pageSize);
		StringBuffer sql = new StringBuffer("select * from st_timetable where 1=1");
		if (bean != null) {
			if (bean.getId() > 0)
				sql.append(" and id = " + bean.getId());
			if (bean.getCourseId() > 0)
				sql.append(" and course_id = " + bean.getCourseId());
			if (bean.getCourseName() != null && bean.getCourseName().length() > 0)
				sql.append(" and course_name like '" + bean.getCourseName() + "%'");
			if (bean.getSubjectId() > 0)
				sql.append(" and subject_id = " + bean.getSubjectId());
			if (bean.getSubjectName() != null && bean.getSubjectName().length() > 0)
				sql.append(" and subject_name like '" + bean.getSubjectName() + "%'");
			if (bean.getSemester() != null && bean.getSemester().length() > 0)
				sql.append(" and semester like '" + bean.getSemester() + "%'");
			if (bean.getDescription() != null && bean.getDescription().length() > 0)
				sql.append(" and description like '" + bean.getDescription() + "%'");
			if (bean.getExamDate() != null && bean.getExamDate().getDate() > 0)
				sql.append(" and exam_date like '" + new java.sql.Date(bean.getExamDate().getTime()) + "%'");
			if (bean.getExamTime() != null && bean.getExamTime().length() > 0)
				sql.append(" and exam_time like '" + bean.getExamTime() + "%'");
		}
//		if (bean != null) {
//			if (bean.getId() > 0) {
//				sql.append(" and id = " + bean.getId());
//			}
//			if (bean.getCourseId() > 0) {
//				sql.append(" and course_id = " + bean.getCourseId());
//			}
//			if (bean.getCourseName() != null && bean.getCourseName().length() > 0) {
//				sql.append(" and course_name like '" + bean.getCourseName() + "%'");
//			}
//			if (bean.getSubjectId() > 0) {
//				sql.append(" and subject_id = " + bean.getSubjectId());
//			}
//			if (bean.getSubjectName() != null && bean.getSubjectName().length() > 0) {
//				sql.append(" and subject_name like '" + bean.getSubjectName() + "%'");
//			}
//			if (bean.getSemester() != null && bean.getSemester().length() > 0) {
//				sql.append(" and semester like '" + bean.getSemester() + "%'");
//			}
//			if (bean.getDescription() != null && bean.getDescription().length() > 0) {
//				sql.append(" and description like '" + bean.getDescription() + "%'");
//			}
//			if (bean.getExamDate() != null && bean.getExamDate().getDate() > 0) {
//				sql.append(" and exam_date like '" + new java.sql.Date(bean.getExamDate().getTime()) + "%'");
//			}
//			if (bean.getExamTime() != null && bean.getExamTime().length() > 0) {
//				sql.append(" and exam_time like '" + bean.getExamTime() + "%'");
//			}
//		}

		if (pageSize > 0) {
			pageNo = (pageNo - 1) * pageSize;
			sql.append(" limit " + pageNo + ", " + pageSize);
		}

		ArrayList<TimetableBean> list = new ArrayList<TimetableBean>();
		Connection conn = null;
		try {
			conn = JDBCDataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				bean = new TimetableBean();
				bean.setId(rs.getLong(1));
				bean.setSemester(rs.getString(2));
				bean.setDescription(rs.getString(3));
				bean.setExamDate(rs.getDate(4));
				bean.setExamTime(rs.getString(5));
				bean.setCourseId(rs.getLong(6));
				bean.setCourseName(rs.getString(7));
				bean.setSubjectId(rs.getLong(8));
				bean.setSubjectName(rs.getString(9));
				bean.setCreatedBy(rs.getString(10));
				bean.setModifiedBy(rs.getString(11));
				bean.setCreatedDatetime(rs.getTimestamp(12));
				bean.setModifiedDatetime(rs.getTimestamp(13));
				list.add(bean);
			}
			rs.close();
			pstmt.close();
			log.debug("search completed with " + list.size() + " records");
		} catch (Exception e) {
			log.error("Exception in search", e);
			throw new ApplicationException("Exception : Exception in search Timetable");
		} finally {
			JDBCDataSource.closeConnection(conn);
		}
		return list;
	}
}
