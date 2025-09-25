package in.co.rays.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import in.co.rays.bean.PatientBean;
import in.co.rays.exception.ApplicationException;
import in.co.rays.exception.DatabaseException;
import in.co.rays.util.JDBCDataSource;

public class PatientModel {
	
	public Integer nextPk() throws DatabaseException{
		
		 Connection conn = null;
	        int pk = 0;

	        try {
	            conn = JDBCDataSource.getConnection();
	            PreparedStatement pstmt = conn.prepareStatement("select max(id) from st_patient");
	            ResultSet rs = pstmt.executeQuery();
	            while (rs.next()) {
	                pk = rs.getInt(1);
	            }
	           
	        } catch (Exception e) {
	            throw new DatabaseException("Exception : Exception in getting PK");
	        } finally {
	     
	            JDBCDataSource.closeConnection(conn);
	        }

	        return pk + 1;
	    }
	
	public long add(PatientBean bean) throws ApplicationException {
	
		Connection conn = null;
        int pk = 0;

        try {
            pk = nextPk(); 
            conn = JDBCDataSource.getConnection();
            conn.setAutoCommit(false);
            PreparedStatement pstmt = conn
                    .prepareStatement("insert into st_patient values(?, ?, ?, ?, ?, ?, ?, ?, ?)");
            pstmt.setInt(1, pk);
            pstmt.setString(2, bean.getName());
            pstmt.setString(3, bean.getDisease());
            pstmt.setString(4, bean.getMobileNo());
            pstmt.setDate(5,new java.sql.Date(bean.getDateOfVisit().getTime()));
            pstmt.setString(6, bean.getCreatedBy());
            pstmt.setString(7, bean.getModifiedBy());
            pstmt.setTimestamp(8, bean.getCreatedDatetime());
            pstmt.setTimestamp(9, bean.getModifiedDatetime());
            
            pstmt.executeUpdate();
            conn.commit();
            pstmt.close();
            System.out.println("data add....." + pstmt);
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (Exception ex) {
                throw new ApplicationException("Exception : add rollback exception " + ex.getMessage());
            }
            throw new ApplicationException("Exception : Exception in add User");
        } finally {
            JDBCDataSource.closeConnection(conn);
        }
        return pk;
    }
	
	public void update (PatientBean bean) throws ApplicationException {
		
		 Connection conn = null;

	        try {
	            conn = JDBCDataSource.getConnection();
	            conn.setAutoCommit(false);
	            PreparedStatement pstmt = conn.prepareStatement(
	                    "update st_patient set name = ?, disease = ?, mobileNo = ?, dob = ?, created_by = ?, modified_by = ?, created_datetime = ?, modified_datetime = ? where id = ?");
	            pstmt.setString(1, bean.getName());
	            pstmt.setString(2, bean.getDisease());
	            pstmt.setString(3, bean.getMobileNo());
	            pstmt.setDate(4, new java.sql.Date(bean.getDateOfVisit().getTime()));
	            pstmt.setString(5, bean.getCreatedBy());
	            pstmt.setString(6, bean.getModifiedBy());
	            pstmt.setTimestamp(7, bean.getCreatedDatetime());
	            pstmt.setTimestamp(8, bean.getModifiedDatetime());
	            pstmt.setLong(9, bean.getId());
	            
	            pstmt.executeUpdate();
	            conn.commit();
	            pstmt.close();
	            System.out.println("data update......" + pstmt);
	        } catch (Exception e) {
	            try {
	                conn.rollback();
	            } catch (Exception ex) {
	                throw new ApplicationException("Exception : Delete rollback exception " + ex.getMessage());
	            }
	            throw new ApplicationException("Exception in updating User ");
	        } finally {
	            JDBCDataSource.closeConnection(conn);
	        }
	    }


	public void delete(PatientBean bean) throws ApplicationException {
		
		Connection conn = null;

        try {
            conn = JDBCDataSource.getConnection();
            conn.setAutoCommit(false);
            PreparedStatement pstmt = conn.prepareStatement("delete from st_patient where id = ?");
            pstmt.setLong(1, bean.getId());
            pstmt.executeUpdate();
            conn.commit();
            pstmt.close();
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (Exception ex) {
                throw new ApplicationException("Exception : Delete rollback exception " + ex.getMessage());
            }
            throw new ApplicationException("Exception : Exception in delete User");
        } finally {
            JDBCDataSource.closeConnection(conn);
        }
    }
	
	public PatientBean findByPk(long pk) throws ApplicationException {
		
		PatientBean bean = null;
        Connection conn = null;

        StringBuffer sql = new StringBuffer("select * from st_patient where id = ?");

        try {
            conn = JDBCDataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql.toString());
            pstmt.setLong(1, pk);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                bean = new PatientBean();
                
                bean.setId(rs.getLong(1));
                bean.setName(rs.getString(2));
                bean.setDisease(rs.getString(3));
                bean.setMobileNo(rs.getString(4));
                bean.setDateOfVisit(rs.getDate(5));
                bean.setCreatedBy(rs.getString(6));
                bean.setModifiedBy(rs.getString(7));
                bean.setCreatedDatetime(rs.getTimestamp(8));
                bean.setModifiedDatetime(rs.getTimestamp(9));
            }
            rs.close();
            pstmt.close();
        } catch (Exception e) {
            throw new ApplicationException("Exception : Exception in getting User by pk");
        } finally {
            JDBCDataSource.closeConnection(conn);
        }
        return bean;
    }
	
	public List list () throws ApplicationException {
		return search(null,0,0);
	}

	public List search(PatientBean bean, int pageNo, int pageSize) throws ApplicationException {

	    Connection conn = null;
	    ArrayList list = new ArrayList();

	    StringBuffer sql = new StringBuffer("select * from st_patient where 1=1 ");

	    if (bean != null) {
	        if (bean.getId() > 0) {
	            sql.append(" AND id = " + bean.getId());
	        }
	        if (bean.getName() != null && bean.getName().length() > 0) {
	            sql.append(" AND name LIKE '" + bean.getName() + "%'");
	        }
	        if (bean.getDisease() != null && bean.getDisease().length() > 0) {
	            sql.append(" AND disease LIKE '" + bean.getDisease() + "%'");
	            System.out.println("i,m in search of patient");
	        }
	        if (bean.getMobileNo() != null && bean.getMobileNo().length() > 0) {
	            sql.append(" AND mobileNo LIKE '" + bean.getMobileNo() + "%'");
	        }
	        if (bean.getDateOfVisit() != null) {
	            sql.append(" AND dateOfVisit = '" + new java.sql.Date(bean.getDateOfVisit().getTime()) + "'");
	        }
	    }

	    if (pageSize > 0) {
            pageNo = (pageNo - 1) * pageSize;
            sql.append(" limit " + pageNo + ", " + pageSize);
        }

	    try {
	        conn = JDBCDataSource.getConnection();
	        PreparedStatement pstmt = conn.prepareStatement(sql.toString());
	        ResultSet rs = pstmt.executeQuery();

	        while (rs.next()) {
	            bean = new PatientBean();
	            bean.setId(rs.getLong(1));
	            bean.setName(rs.getString(2));
	            bean.setDisease(rs.getString(3));
	            bean.setMobileNo(rs.getString(4));
	            bean.setDateOfVisit(rs.getDate(5));
	            bean.setCreatedBy(rs.getString(6));
	            bean.setModifiedBy(rs.getString(7));
	            bean.setCreatedDatetime(rs.getTimestamp(8));
	            bean.setModifiedDatetime(rs.getTimestamp(9));

	            list.add( bean);  
	        }

	        rs.close();
	        pstmt.close();
	    } catch (Exception e) {
	        throw new ApplicationException("Exception : Exception in search user " + e.getMessage());
	    } finally {
	        JDBCDataSource.closeConnection(conn);
	    }
	    return list;
	}

}
