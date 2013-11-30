package com.voc_cards;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.servlet.ServletContext;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class Voc_cardsServlet extends HttpServlet {
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse resp)
			throws IOException {
		
		ServletContext context = request.getSession().getServletContext();
		URL resourceUrl = context.getResource("/index.html");
		InputStream resourceContent = context.getResourceAsStream("/index.html");

		resp.setContentType("text/plain");
		resp.getWriter().println("Hello, world");
	}
}
