package in.co.rays.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import in.co.rays.bean.BaseBean;
import in.co.rays.bean.TimetableBean;
import in.co.rays.exception.ApplicationException;
import in.co.rays.exception.DuplicateRecordException;
import in.co.rays.model.CourseModel;
import in.co.rays.model.SubjectModel;
import in.co.rays.model.TimetableModel;
import in.co.rays.util.DataUtility;
import in.co.rays.util.DataValidator;
import in.co.rays.util.PropertyReader;
import in.co.rays.util.ServletUtility;

import org.apache.log4j.Logger;

/**
 * TimetableCtl handles operations related to Timetable management.
 * 
 * Supports adding, updating, and validating timetable entries.
 * Also preloads Subject and Course lists for the form.
 * 
 * Author: Shad Khan
 * Version: 1.0
 */
@WebServlet(name = "TimetableCtl", urlPatterns = { "/TimetableCtl" })
public class TimetableCtl extends BaseCtl {

    private static Logger log = Logger.getLogger(TimetableCtl.class);

    /**
     * Preloads Subject and Course lists to populate dropdowns in the view.
     * 
     * @param request HttpServletRequest
     */
    @Override
    protected void preload(HttpServletRequest request) {
        log.debug("TimetableCtl preload run");

        SubjectModel subjectModel = new SubjectModel();
        CourseModel courseModel = new CourseModel();

        try {
            List subjectList = subjectModel.list();
            request.setAttribute("subjectList", subjectList);
            log.debug("Preloaded subject list size: " + subjectList.size());

            List courseList = courseModel.list();
            request.setAttribute("courseList", courseList);
            log.debug("Preloaded course list size: " + courseList.size());
        } catch (ApplicationException e) {
            log.error("Error in preloading TimetableCtl", e);
            e.printStackTrace();
        }
    }

    /**
     * Validates the Timetable form inputs.
     * 
     * @param request HttpServletRequest
     * @return boolean true if valid, false otherwise
     */
    @Override
    protected boolean validate(HttpServletRequest request) {
        log.debug("TimetableCtl validate run");

        boolean pass = true;

        if (DataValidator.isNull(request.getParameter("semester"))) {
            request.setAttribute("semester", PropertyReader.getValue("error.require", "Semester"));
            pass = false;
        }

        if (DataValidator.isNull(request.getParameter("examDate"))) {
            request.setAttribute("examDate", PropertyReader.getValue("error.require", "Date of Exam"));
            pass = false;
        } else if (!DataValidator.isDate(request.getParameter("examDate"))) {
            request.setAttribute("examDate", PropertyReader.getValue("error.date", "Date of Exam"));
            pass = false;
        } else if (DataValidator.isSunday(request.getParameter("examDate"))) {
            request.setAttribute("examDate", "Exam should not be on Sunday");
            pass = false;
        }

        if (DataValidator.isNull(request.getParameter("examTime"))) {
            request.setAttribute("examTime", PropertyReader.getValue("error.require", "Exam Time"));
            pass = false;
        }

        if (DataValidator.isNull(request.getParameter("description"))) {
            request.setAttribute("description", PropertyReader.getValue("error.require", "Description"));
            pass = false;
        }

        if (DataValidator.isNull(request.getParameter("courseId"))) {
            request.setAttribute("courseId", PropertyReader.getValue("error.require", "Course Name"));
            pass = false;
        }

        if (DataValidator.isNull(request.getParameter("subjectId"))) {
            request.setAttribute("subjectId", PropertyReader.getValue("error.require", "Subject Name"));
            pass = false;
        }

        return pass;
    }

    /**
     * Populates TimetableBean from request parameters.
     * 
     * @param request HttpServletRequest
     * @return BaseBean populated TimetableBean
     */
    @Override
    protected BaseBean populateBean(HttpServletRequest request) {
        log.debug("TimetableCtl populateBean run");

        TimetableBean bean = new TimetableBean();

        bean.setId(DataUtility.getLong(request.getParameter("id")));
        bean.setSemester(DataUtility.getString(request.getParameter("semester")));
        bean.setDescription(DataUtility.getString(request.getParameter("description")));
        bean.setExamTime(DataUtility.getString(request.getParameter("examTime")));
        bean.setExamDate(DataUtility.getDate(request.getParameter("examDate")));
        bean.setCourseId(DataUtility.getLong(request.getParameter("courseId")));
        bean.setSubjectId(DataUtility.getLong(request.getParameter("subjectId")));

        populateDTO(bean, request);

        return bean;
    }

    /**
     * Handles GET request to display Timetable form for add/edit operations.
     * 
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        log.debug("TimetableCtl doGet run");

        long id = DataUtility.getLong(request.getParameter("id"));

        TimetableModel model = new TimetableModel();

        if (id > 0) {
            try {
                TimetableBean bean = model.findByPk(id);
                ServletUtility.setBean(bean, request);
                log.debug("Loaded TimetableBean with ID: " + id);
            } catch (ApplicationException e) {
                log.error("Error in doGet of TimetableCtl", e);
                e.printStackTrace();
                ServletUtility.handleException(e, request, response);
                return;
            }
        }
        ServletUtility.forward(getView(), request, response);
    }

    /**
     * Handles POST request to add/update Timetable entries.
     * 
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        log.debug("TimetableCtl doPost run");

        String op = DataUtility.getString(request.getParameter("operation"));

        TimetableModel model = new TimetableModel();

        long id = DataUtility.getLong(request.getParameter("id"));

        if (OP_SAVE.equalsIgnoreCase(op)) {

            TimetableBean bean = (TimetableBean) populateBean(request);

            try {
                TimetableBean bean1 = model.checkByCourseName(bean.getCourseId(), bean.getExamDate());
                TimetableBean bean2 = model.checkBySubjectName(bean.getCourseId(), bean.getSubjectId(), bean.getExamDate());
                TimetableBean bean3 = model.checkBySemester(bean.getCourseId(), bean.getSubjectId(), bean.getSemester(), bean.getExamDate());

                if (bean1 == null && bean2 == null && bean3 == null) {
                    long pk = model.add(bean);
                    ServletUtility.setBean(bean, request);
                    ServletUtility.setSuccessMessage("Timetable added successfully", request);
                    log.debug("Added Timetable with ID: " + pk);
                } else {
                    ServletUtility.setBean(bean, request);
                    ServletUtility.setErrorMessage("Timetable already exist!", request);
                    log.debug("Timetable already exists, add operation skipped");
                }
            } catch (DuplicateRecordException e) {
                ServletUtility.setBean(bean, request);
                ServletUtility.setErrorMessage("Timetable already exist!", request);
                log.error("Duplicate Timetable found", e);
            } catch (ApplicationException e) {
                log.error("Error in adding Timetable", e);
                e.printStackTrace();
                ServletUtility.handleException(e, request, response);
                return;
            }

        } else if (OP_UPDATE.equalsIgnoreCase(op)) {

            TimetableBean bean = (TimetableBean) populateBean(request);

            try {
                TimetableBean bean4 = model.checkByExamTime(bean.getCourseId(), bean.getSubjectId(),
                        bean.getSemester(), bean.getExamDate(), bean.getExamTime(), bean.getDescription());

                if (id > 0 && bean4 == null) {
                    model.update(bean);
                    ServletUtility.setBean(bean, request);
                    ServletUtility.setSuccessMessage("Timetable updated successfully", request);
                    log.debug("Updated Timetable with ID: " + id);
                } else {
                    ServletUtility.setBean(bean, request);
                    ServletUtility.setErrorMessage("Timetable already exist!", request);
                    log.debug("Timetable already exists, update operation skipped");
                }
            } catch (DuplicateRecordException e) {
                ServletUtility.setBean(bean, request);
                ServletUtility.setErrorMessage("Timetable already exist!", request);
                log.error("Duplicate Timetable found during update", e);
            } catch (ApplicationException e) {
                log.error("Error in updating Timetable", e);
                e.printStackTrace();
                ServletUtility.handleException(e, request, response);
                return;
            }
        } else if (OP_CANCEL.equalsIgnoreCase(op)) {
            ServletUtility.redirect(ORSView.TIMETABLE_LIST_CTL, request, response);
            return;
        } else if (OP_RESET.equalsIgnoreCase(op)) {
            ServletUtility.redirect(ORSView.TIMETABLE_CTL, request, response);
            return;
        }
        ServletUtility.forward(getView(), request, response);
    }

    /**
     * Returns Timetable view page.
     * 
     * @return String view page
     */
    @Override
    protected String getView() {
        return ORSView.TIMETABLE_VIEW;
    }
}
