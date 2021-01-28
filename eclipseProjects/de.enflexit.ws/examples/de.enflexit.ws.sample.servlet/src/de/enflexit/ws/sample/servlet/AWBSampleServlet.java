package de.enflexit.ws.sample.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The Class AWBSampleServlet.
 * @author Christian Derksen - SOFTEC - ICB - University of Duisburg-Essen
 */
public class AWBSampleServlet extends HttpServlet{

	private static final long serialVersionUID = -2011085717792093929L;

	public AWBSampleServlet() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.getWriter().println("Welcome to the OSGI-based AWB-Webserver & the " + this.getClass().getSimpleName());
	}

}