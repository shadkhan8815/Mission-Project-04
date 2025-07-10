package in.co.rays.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import in.co.rays.bean.RoleBean;
import in.co.rays.exception.ApplicationException;
import in.co.rays.exception.DatabaseException;
import in.co.rays.exception.DuplicateRecordException;
import in.co.rays.exception.RecordNotFoundException;
import in.co.rays.util.JDBCDataSource;

public class RoleModel {

	public Integer nextPk() throws Exception, ApplicationException {

		int pk = 0;

		Connection conn = null;

		try {

			conn = JDBCDataSource.getConnection();

			PreparedStatement pstmt = conn.prepareStatement("select max(id) from st_role");

			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				pk = rs.getInt(1);
			}
		} catch (Exception e) {
			throw new ApplicationException("Exception : " + e.getMessage());

		} finally {

			JDBCDataSource.closeConnection(conn);
		}

		return pk + 1;
	}
//------------------------------------------------------------------	

	public long add(RoleBean bean) throws Exception, DatabaseException, ApplicationException {

		RoleBean existBean = findByName(bean.getName());

		if (existBean != null) {
			throw new DuplicateRecordException("Role Name Already Exist..!!");
		}

		Connection conn = null;

		int pk = nextPk();
		
		try {
			conn = JDBCDataSource.getConnection();

			conn.setAutoCommit(false);

			PreparedStatement pstmt = conn.prepareStatement("insert into st_role values(?, ?, ?, ?, ?, ?, ?)");

			pstmt.setInt(1, pk);
			pstmt.setString(2, bean.getName());
			pstmt.setString(3, bean.getDescription());
			pstmt.setString(4, bean.getCreatedBy());
			pstmt.setString(5, bean.getModifiedBy());
			pstmt.setTimestamp(6, bean.getCreatedDatetime());
			pstmt.setTimestamp(7, bean.getModifiedDatetime());

			int i = pstmt.executeUpdate();

			conn.commit();

			pstmt.close();

			System.out.println("data inserted => " + i);
		} catch (Exception e) {

			try {
				conn.rollback();
				conn.close();
			} catch (Exception e2) {

				throw new DatabaseException("Exception : " + e2.getMessage());
			}
			throw new ApplicationException("Excepton : " + e.getMessage());
		} finally {

			JDBCDataSource.closeConnection(conn);

		}
		return pk;

	}
//---------------------------------------------------------------------------	

	public void update(RoleBean bean) throws Exception, DatabaseException, ApplicationException {

		RoleBean existBean = findByName(bean.getName());

		if (existBean != null && bean.getId() != existBean.getId()) {
			throw new DuplicateRecordException("Role Name Already Exist..!!");
		}

		Connection conn = null;

		try {

			conn = JDBCDataSource.getConnection();

			conn.setAutoCommit(false);

			PreparedStatement pstmt = conn.prepareStatement(
					"update st_role set name = ?, description = ?, created_by = ?, modified_by = ?, created_datetime = ?, modified_datetime = ? where id = ?");

			pstmt.setString(1, bean.getName());
			pstmt.setString(2, bean.getDescription());
			pstmt.setString(3, bean.getCreatedBy());
			pstmt.setString(4, bean.getModifiedBy());
			pstmt.setTimestamp(5, bean.getCreatedDatetime());
			pstmt.setTimestamp(6, bean.getModifiedDatetime());
			pstmt.setLong(7, bean.getId());

			int i = pstmt.executeUpdate();

			conn.commit();

			pstmt.close();

			System.out.println("data updated => " + i);
		} catch (Exception e) {

			try {
				conn.rollback();
				conn.close();

			} catch (Exception e2) {
				throw new DatabaseException("Exception : " + e2.getMessage());
			}
			throw new ApplicationException("Exception : " + e.getMessage());
		} finally {

			JDBCDataSource.closeConnection(conn);
		}

	}
//-----------------------------------------------------------------------------------	

	public void delete(long id) throws DatabaseException, ApplicationException {

		Connection conn = null;

		try {

			conn = JDBCDataSource.getConnection();

			conn.setAutoCommit(false);

			PreparedStatement pstmt = conn.prepareStatement("delete from st_role where id = ?");

			pstmt.setLong(1, id);

			conn.commit();
			pstmt.close();

			int i = pstmt.executeUpdate();

			System.out.println("data deleted => " + i);
		} catch (Exception e) {

			try {
				conn.rollback();
				conn.close();

			} catch (Exception e2) {
				throw new DatabaseException("Exception : " + e2.getMessage());

			}
			throw new ApplicationException("Exception : " + e.getMessage());
		} finally {
			JDBCDataSource.closeConnection(conn);
		}

	}
//--------------------------------------------------------------------------	

	public RoleBean findByPk(long id) throws ApplicationException {

		Connection conn = null;

		RoleBean bean = null;

		try {

			conn = JDBCDataSource.getConnection();

			conn.setAutoCommit(false);

			PreparedStatement pstmt = conn.prepareStatement("select * from st_role where id = ?");

			pstmt.setLong(1, id);

			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				bean = new RoleBean();
				bean.setId(rs.getLong(1));
				bean.setName(rs.getString(2));
				bean.setDescription(rs.getString(3));
				bean.setCreatedBy(rs.getString(4));
				bean.setModifiedBy(rs.getString(5));
				bean.setCreatedDatetime(rs.getTimestamp(6));
				bean.setModifiedDatetime(rs.getTimestamp(7));
			}
			conn.commit();
			rs.close();
			pstmt.close();
		} catch (Exception e) {

		} finally {

			JDBCDataSource.closeConnection(conn);
		}
		return bean;
	}
//-----------------------------------------------------------------	

	public RoleBean findByName(String name) throws ApplicationException, SQLException {

		Connection conn = null;

		RoleBean bean = null;

		try {

			conn = JDBCDataSource.getConnection();

			PreparedStatement pstmt = conn.prepareStatement("select * from st_role where name = ?");

			pstmt.setString(1, name);

			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				bean = new RoleBean();
				bean.setId(rs.getLong(1));
				bean.setName(rs.getString(2));
				bean.setDescription(rs.getString(3));
				bean.setCreatedBy(rs.getString(4));
				bean.setModifiedBy(rs.getString(5));
				bean.setCreatedDatetime(rs.getTimestamp(6));
				bean.setModifiedDatetime(rs.getTimestamp(7));
			}
			rs.close();

			pstmt.close();

		} catch (Exception e) {
			throw new ApplicationException("Exception : " + e.getMessage());

		} finally {
			conn.close();
			JDBCDataSource.closeConnection(conn);

		}
		return bean;
	}
//----------------------------------------------------------------------	

	public List list() throws Exception {
		return search(null, 0, 0);
	}
//-----------------------------------------------------------------------------------------	

	public List search(RoleBean bean, int pageNo, int pageSize) throws Exception {

		StringBuffer sql = new StringBuffer("select * from st_role where 1=1");

		if (bean != null) {
			if (bean.getName() != null && bean.getName().length() > 0) {
				sql.append(" and first_name like '" + bean.getName() + "%'");
			}
		}

		if (pageSize > 0) {
			pageNo = (pageNo - 1) * pageSize;
			sql.append(" limit " + pageNo + ", " + pageSize);
		}

		System.out.println("sql ==>> " + sql.toString());

		Connection conn = null;

		List list = new ArrayList();

		try {
			conn = JDBCDataSource.getConnection();

			PreparedStatement pstmt = conn.prepareStatement(sql.toString());

			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				bean = new RoleBean();
				bean.setId(rs.getLong(1));
				bean.setName(rs.getString(2));
				bean.setDescription(rs.getString(3));
				bean.setCreatedBy(rs.getString(4));
				bean.setModifiedBy(rs.getString(5));
				bean.setCreatedDatetime(rs.getTimestamp(6));
				bean.setModifiedDatetime(rs.getTimestamp(7));
				list.add(bean);
			}
			rs.close();
			pstmt.close();

		} catch (Exception e) {
			throw new RecordNotFoundException("Exception : " + e.getMessage());
		} finally {
			conn.close();
			JDBCDataSource.closeConnection(conn);
		}
		return list;

	}
}