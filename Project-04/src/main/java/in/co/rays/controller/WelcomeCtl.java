package in.co.rays.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/WelcomeCtl")
public class WelcomeCtl extends BaseCtl{
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		    System.out.println("WelcomeCtl doGet Run");
		
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		    System.out.println("WelcomeCtl doPost Run");
		
	}
	
	@Override
	protected String getView() {
		// TODO Auto-generated method stub
		return null;
	}

}
