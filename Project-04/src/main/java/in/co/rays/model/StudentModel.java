package in.co.rays.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import in.co.rays.bean.CollegeBean;
import in.co.rays.bean.StudentBean;
import in.co.rays.exception.ApplicationException;
import in.co.rays.exception.DatabaseException;
import in.co.rays.exception.DuplicateRecordException;
import in.co.rays.exception.RecordNotFoundException;
import in.co.rays.util.JDBCDataSource;


public class StudentModel {

	
	public Integer nextPK() throws DatabaseException {
		
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

		} catch (Exception e) {
			throw new DatabaseException("Exception : Exception in getting PK");
		} finally {
			JDBCDataSource.closeConnection(conn);
		}
		return pk + 1;
	}
//--------------------------------------------------------------------------------------------
	
	public long add(StudentBean bean) throws RecordNotFoundException, Exception {
		
		CollegeModel collegemodel = new CollegeModel();
		CollegeBean collegebean = collegemodel.findByPk(bean.getCollegeId());
		bean.setCollegeName(collegebean.getName());

		StudentBean existEmail = findByEmail(bean.getEmail());
		if (existEmail != null) {

			throw new DuplicateRecordException("email already exist....!!");
		}

		Connection conn = null;
		
		 int pk = nextPK();

		try {
			conn = JDBCDataSource.getConnection();

			conn.setAutoCommit(false); 
			

			PreparedStatement pstmt = conn
					.prepareStatement("insert into st_student values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

			pstmt.setLong(1, pk);
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

			int i = pstmt.executeUpdate();
			
			System.out.println("Data insertrd =>" + i);
			
			conn.commit(); 
			pstmt.close();
			
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (Exception ex) {
				throw new ApplicationException("Exception : add rollback exception " + ex.getMessage());
			}
			throw new ApplicationException("Exception : Exception in add Student");
		} finally {
			JDBCDataSource.closeConnection(conn);
		}
		return pk;
	}
//-------------------------------------------------------------------------------------------------------------
	
	public void delete(long id) throws ApplicationException {

		Connection conn = null;

		try {

			conn = JDBCDataSource.getConnection();

			conn.setAutoCommit(false);
			PreparedStatement pstmt = conn.prepareStatement("delete from st_student where id = ?");

			pstmt.setLong(1, id);

			int i = pstmt.executeUpdate();

			conn.commit();

		} catch (Exception e) {

			try {

				conn.rollback();

			} catch (Exception e2) {

				throw new ApplicationException("Exception : Add RollBack Exception" + e2.getMessage());
			}
			throw new ApplicationException("Exception : Delete Student Exception" + e);

		} finally {

			JDBCDataSource.closeConnection(conn);
		}
	}

//-----------------------------------------------------------------------------------------------------------
	
	public StudentBean findByEmail(String email) throws ApplicationException {
		
		Connection conn = null;
		StudentBean bean = null;

		try {
			conn = JDBCDataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement("select * from st_student where email = ?");
			pstmt.setString(1, email);

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

		} catch (Exception e) {

			throw new ApplicationException("Exception : Exception getting student email");

		} finally {

			JDBCDataSource.closeConnection(conn);
		}
		return bean;

	}

//-------------------------------------------------------------------------------------------

	public StudentBean findByPk(long id) throws ApplicationException {

		Connection conn = null;
		StudentBean bean = null;

		try {
			conn = JDBCDataSource.getConnection();

			PreparedStatement pstmt = conn.prepareStatement("select * from st_student where id = ?");
			pstmt.setLong(1, id);

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
		} catch (Exception e) {

			throw new ApplicationException("Exception : Exception  getting student by pk");
		} finally {

			JDBCDataSource.closeConnection(conn);
		}
		return bean;
	}
//------------------------------------------------------------------------------------------------------
	
	public void update(StudentBean bean) throws RecordNotFoundException, Exception {

		CollegeModel collegeModel = new CollegeModel();
		CollegeBean collegeBean = collegeModel.findByPk(bean.getCollegeId());
		bean.setCollegeName(collegeBean.getName());

		StudentBean existbBean = findByEmail(bean.getEmail());

		if (existbBean != null && bean.getId() != existbBean.getId()) {

			throw new DuplicateRecordException("email already exist.....!!");
		}
		
		Connection conn = null;

		try {

			conn = JDBCDataSource.getConnection();
			conn.setAutoCommit(false);

			PreparedStatement pstmt = conn.prepareStatement(
					"update st_student set first_name = ?, last_name = ?, dob = ?, gender = ?, mobile_no = ?, email = ?, college_id = ?, college_name = ?, created_by = ?, modified_by = ?, created_datetime = ?, modified_datetime = ?  where id = ?");

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

			int i = pstmt.executeUpdate();
			System.out.println("Data updated => " + i);
			conn.commit();

		} catch (Exception e) {
			try {
				conn.rollback();

			} catch (Exception e2) {

				throw new ApplicationException("Exception : Add rollBack Exception" + e2.getMessage());
			}
			throw new ApplicationException("Exception : Add Student Exception" + e);

		} finally {

			JDBCDataSource.closeConnection(conn);

		}
	}
//---------------------------------------------------------------------------------------------------

	public List list() throws ApplicationException {
		return search(null, 0, 0);
	}
//-----------------------------------------------------------------
	
	public List search(StudentBean bean, int pageNo, int pageSize) throws ApplicationException {

		StringBuffer sql = new StringBuffer("select * from st_student where 1=1");

		if (bean != null) {
			if (bean.getId() > 0) {
				sql.append(" AND id = " + bean.getId());
			}
			if (bean.getFirstName() != null && bean.getFirstName().length() > 0) {
				sql.append(" AND FIRST_NAME like '" + bean.getFirstName() + "%'");
			}
			if (bean.getLastName() != null && bean.getLastName().length() > 0) {
				sql.append(" AND LAST_NAME like '" + bean.getLastName() + "%'");
			}
			if (bean.getDob() != null && bean.getDob().getDate() > 0) {
				sql.append(" AND DATE_OF_BIRTH = " + bean.getDob());
			}
			if (bean.getGender() != null && bean.getGender().length() > 0) {
				sql.append(" AND GENDER like '" + bean.getGender() + "%'");
			}
			if (bean.getMobileNo() != null && bean.getMobileNo().length() > 0) {
				sql.append(" AND MOBILE_NO like '" + bean.getMobileNo() + "%'");
			}
			if (bean.getEmail() != null && bean.getEmail().length() > 0) {
				sql.append(" AND EMAIL like '" + bean.getEmail() + "%'");
			}
			if (bean.getCollegeName() != null && bean.getCollegeName().length() > 0) {
				sql.append(" AND COLLEGE_NAME = " + bean.getCollegeName());
			}

		}

		
		if (pageSize > 0) {
			
			pageNo = (pageNo - 1) * pageSize;

			sql.append(" Limit " + pageNo + ", " + pageSize);
			
		}

		List list = new ArrayList();
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
		} catch (Exception e) {

			throw new ApplicationException("Exception :  Exception in search student " + e);

		} finally {

			JDBCDataSource.closeConnection(conn);
		}
		return list;

	}
}