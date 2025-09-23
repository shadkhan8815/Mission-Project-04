package in.co.rays.test;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import in.co.rays.bean.PatientBean;
import in.co.rays.exception.DatabaseException;
import in.co.rays.model.PatientModel;

public class TestPatientModel {
	
	public static void main(String[] args) throws Exception {
		// testAdd();
		//testNextPk();
		testSearch();
		//testUpdate();
		//testFindByPk();
	}
	
	public static void testNextPk() throws DatabaseException {
		PatientModel model = new PatientModel();
		
		int i = model.nextPk();
		
		System.out.println("Next pk is => " + i);
	}
	
	public static void testAdd() throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

		PatientBean bean = new PatientBean();
		
		bean.setName("Prabhakar");
		bean.setDesease("Cancer");
		bean.setDateOfVisit(sdf.parse("23-09-2025"));
		bean.setMobileNo("9998890811");
		bean.setCreatedBy("admin@gmail.com");
		bean.setModifiedBy("admin@gmail.com");
		bean.setCreatedDatetime(new Timestamp(new Date().getTime()));
		bean.setModifiedDatetime(new Timestamp(new Date().getTime()));

		PatientModel model = new PatientModel();
		model.add(bean);
	}

	public static void testUpdate() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

		PatientModel model = new PatientModel();
		PatientBean bean = new PatientBean();
		
		bean.setId(1);
		bean.setName("Aditya");
		bean.setDesease("blurr vision");
		bean.setDateOfVisit(sdf.parse("23-09-2025"));
		bean.setMobileNo("9998890811");
		bean.setCreatedBy("admin@gmail.com");
		bean.setModifiedBy("admin@gmail.com");
		bean.setCreatedDatetime(new Timestamp(new Date().getTime()));
		bean.setModifiedDatetime(new Timestamp(new Date().getTime()));

		model.update(bean);
	}

	public static void testDelete() throws Exception {
		
		PatientModel model = new PatientModel();
		model.delete(null);
	}

	public static void testFindByPk() throws Exception {

		PatientModel model = new PatientModel();

		PatientBean bean = model.findByPk(1);

		if (bean != null) {
			System.out.print(bean.getId());
			System.out.print("\t" + bean.getName());
			System.out.print("\t" + bean.getDesease());
			System.out.print("\t" + bean.getMobileNo());
			System.out.print("\t" + bean.getDateOfVisit());
			System.out.print("\t" + bean.getCreatedBy());
			System.out.print("\t" + bean.getModifiedBy());
			System.out.print("\t" + bean.getCreatedDatetime());
			System.out.println("\t" + bean.getModifiedDatetime());
		} else {
			System.out.println("id not found");
		}
	}

	
	public static void testSearch() throws Exception {
	    PatientBean bean = new PatientBean();
	  
	    PatientModel model = new PatientModel();
	    List list = model.search(bean, 1, 10);

	    Iterator it = list.iterator();
	    
	    while (it.hasNext()) {
	        bean = (PatientBean) it.next();
	        System.out.print(bean.getId());
	        System.out.print("\t" + bean.getName());
	        System.out.print("\t" + bean.getDesease());
	        System.out.print("\t" + bean.getMobileNo());
	        System.out.print("\t" + bean.getDateOfVisit());
	        System.out.print("\t" + bean.getCreatedBy());
	        System.out.print("\t" + bean.getModifiedBy());
	        System.out.print("\t" + bean.getCreatedDatetime());
	        System.out.println("\t" + bean.getModifiedDatetime());
	    }
	}

	}

