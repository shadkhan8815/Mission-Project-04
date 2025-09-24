package in.co.rays.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import in.co.rays.bean.BaseBean;
import in.co.rays.bean.PatientBean;
import in.co.rays.exception.ApplicationException;
import in.co.rays.model.PatientModel;
import in.co.rays.util.DataUtility;
import in.co.rays.util.DataValidator;
import in.co.rays.util.PropertyReader;
import in.co.rays.util.ServletUtility;

@WebServlet (name = "PatientCtl", urlPatterns = {"/ctl/PatientCtl"})
public class PatientCtl extends BaseCtl{
	
	@Override
	protected void preload(HttpServletRequest request) {
		
		HashMap<String, String> map = new HashMap<String, String>();

        map.put("Diabetes", "Diabetes");
        map.put("Hypertension", "Hypertension");
        map.put("Asthma", "Asthma");
        map.put("Tuberculosis", "Tuberculosis");
        map.put("Malaria", "Malaria");
        
        request.setAttribute("map", map);
	}
	
	@Override
	protected boolean validate(HttpServletRequest request) {
		
		boolean isValid = true ;
		
		if (DataValidator.isNull(request.getParameter("name"))) {
        request.setAttribute("name", PropertyReader.getValue("error.require", "name"));
        isValid = false ;
		}else if (!DataValidator.isName(request.getParameter("name"))) {
            request.setAttribute("name", "Invalid Name");
            isValid = false;
        }

        if (DataValidator.isNull(request.getParameter("desease"))) {
            request.setAttribute("desease", PropertyReader.getValue("error.require", "desease"));
            isValid = false;
        } 

        if (DataValidator.isNull(request.getParameter("mobileNo"))) {
            request.setAttribute("mobileNo", PropertyReader.getValue("error.require", "MobileNo"));
            isValid = false;
        } else if (!DataValidator.isPhoneLength(request.getParameter("mobileNo"))) {
            request.setAttribute("mobileNo", "Mobile No must have 10 digits");
            isValid = false;
        } else if (!DataValidator.isPhoneNo(request.getParameter("mobileNo"))) {
            request.setAttribute("mobileNo", "Invalid Mobile No");
            isValid = false;
        }
        
        if (DataValidator.isNull(request.getParameter("dateOfVisit"))) {
            request.setAttribute("dateOfVisit", PropertyReader.getValue("error.require", "dateOfVisit"));
            isValid = false;
        } else if (!DataValidator.isDate(request.getParameter("dateOfVisit"))) {
            request.setAttribute("dateOfVisit", PropertyReader.getValue("error.date", "dateOfVisit"));
            isValid = false;
        }
        return isValid ;
	}
	
	@Override
	protected BaseBean populateBean(HttpServletRequest request) {
		
		PatientBean bean = new PatientBean() ;
		
		 bean.setId(DataUtility.getLong(request.getParameter("id")));
	        bean.setName(DataUtility.getString(request.getParameter("name")));
	        bean.setDesease(DataUtility.getString(request.getParameter("desease")));
	        bean.setMobileNo(DataUtility.getString(request.getParameter("mobileNo")));
	        bean.setDateOfVisit(DataUtility.getDate(request.getParameter("dateOfVisit")));

	        populateDTO(bean, request);

	        return bean;
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PatientModel model = new PatientModel() ;
	        long id = DataUtility.getLong(request.getParameter("id"));

	        if (id > 0) {
	            try {
	                PatientBean bean = model.findByPk(id);
	                ServletUtility.setBean(bean, request);
	            } catch (ApplicationException e) {
	                e.printStackTrace();
	                return;
	            }
	        }

	        ServletUtility.forward(getView(), request, response);
	    }
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String op = DataUtility.getString(request.getParameter("operation"));
        PatientModel model = new PatientModel();

        if (OP_SAVE.equalsIgnoreCase(op)) {
        	PatientBean bean = (PatientBean) populateBean(request);

            try {
                model.add(bean);
                ServletUtility.setBean(bean, request);
                ServletUtility.setSuccessMessage("Data Saved Successfully", request);
            } catch (ApplicationException e) {
                e.printStackTrace();
                return;
            } catch (Exception e) {
                ServletUtility.setBean(bean, request);
                ServletUtility.setErrorMessage("Login Id already exists", request);
            }

        }else if (OP_RESET.equalsIgnoreCase(op)) {
            ServletUtility.redirect(ORSView.PATIENT_CTL, request, response);
            return;
        }

        else if (OP_UPDATE.equalsIgnoreCase(op)) {
        	PatientBean bean = (PatientBean) populateBean(request);

            try {
                model.update(bean);
                ServletUtility.setBean(bean, request);
                ServletUtility.setSuccessMessage("Data updated Successfully", request);
            } catch (ApplicationException e) {
                e.printStackTrace();
                return;
            } catch (Exception e) {
                ServletUtility.setBean(bean, request);
                ServletUtility.setErrorMessage("Login Id already exists", request);
            }

        } else if (OP_CANCEL.equalsIgnoreCase(op)) {
            ServletUtility.redirect(ORSView.PATIENT_LIST_CTL, request, response);
            return;

        } 
        ServletUtility.forward(getView(), request, response);
    }


	@Override
	protected String getView() {
		return ORSView.PATIENT_VIEW ;
	}

}
