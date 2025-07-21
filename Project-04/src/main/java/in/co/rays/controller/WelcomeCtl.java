package in.co.rays.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import in.co.rays.util.ServletUtility;

@WebServlet(name = "WelcomeCtl", urlPatterns = {"/WelcomeCtl"})
public class WelcomeCtl extends BaseCtl{
	
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		    System.out.println("WelcomeCtl doGet Run");
		
		    ServletUtility.forward(getView(), request, response);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		    System.out.println("WelcomeCtl doPost Run");
		
	}
	
	@Override
	protected String getView() {
		return ORSView.WELCOME_VIEW ;
			}

}
