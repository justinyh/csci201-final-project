package classpage;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import obj.DAO;

/**
 * Servlet implementation class CommentServlet
 */
@WebServlet("/CommentServlet")
public class CommentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CommentServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		int courseID = Integer.parseInt(request.getParameter("courseID"));
		int userID = Integer.parseInt(request.getParameter("userID"));
		String body = request.getParameter("description");
		double grade = Double.parseDouble(request.getParameter("grade")); 
		int usefulness = Integer.parseInt(request.getParameter("usefulness"));
		int difficulty = Integer.parseInt(request.getParameter("difficulty"));
		DAO dao;
		try {
			dao = new DAO();
			dao.addComment(courseID, userID, body);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		response.sendRedirect("classpage?class=" + courseID);
	}

}