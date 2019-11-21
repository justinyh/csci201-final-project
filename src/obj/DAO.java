package obj;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class DAO {
	Connection conn = null;
	String url = "jdbc:mysql://localhost:3306/FinalProject?useSSL=false";
	String username;
	String password;
	/**
	 * Constructs a DAO object given the database username and password.
	 * @param username
	 * @param password
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public DAO(String username, String password) throws SQLException{
		try {
			Class.forName("com.mysql.jdbc.Driver");
			this.username = username;
			this.password = password;
			conn = DriverManager.getConnection(url, this.username, this.password);
		} catch(SQLException e) {
			System.out.println( e.getMessage());
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		} finally {
			if (conn != null) conn.close();
		}
	}
	
	public Course getCourseInfo(int CourseID, int UserID) throws SQLException {
		Statement st = null;
		ResultSet rs = null;
		String query = "SELECT c.*, d.*, u.UserName, l.UserID AS LikeUser, l.LikeValue FROM Courses c "+
				"LEFT JOIN Comments d ON c.CourseID=d.CourseID " +
			    "LEFT JOIN Likes l ON d.CommentID=l.CommentID " +
			    "LEFT JOIN Users u ON d.UserID=u.UserID " +
			    "WHERE c.CourseID='" + CourseID + "';";
		Course curr = null;
		try {
			conn = DriverManager.getConnection(url, username, password);
			st = conn.createStatement();
			rs = st.executeQuery(query);
			if (!rs.next()) return null;
			curr = new Course(rs);
			while (!rs.isAfterLast()) {
				int currCommentID = rs.getInt("CommentID");
				if (rs.wasNull()) {
					rs.next();
					continue;
				}
				Comment currComment = new Comment(rs);
				if (currComment.getUserID() == UserID) curr.toggleCurrUser();
				int likeCounter = 0;
				while (!rs.isAfterLast() && rs.getInt("CommentID") == currCommentID) {
					int currLikeValue = rs.getInt("LikeValue");
					if (rs.getInt("LikeUser") == UserID) currComment.setUserLikeValue(currLikeValue);
					likeCounter += currLikeValue;
					rs.next();
				}
				currComment.setTotalLikes(likeCounter);
				curr.addComment(currComment);
			}
		} finally {
			if (conn != null) conn.close();
			if (st != null) st.close();
			if (rs != null) rs.close();
		}
		return curr;
	}
	
	
	/**
	 * Executes a like or dislike in the database given the UserID, CommentID, and LikeValue.
	 * @param UserID
	 * @param CommentID
	 * @param LikeValue
	 * @throws SQLException
	 */
	public void updateLikes(int UserID, int CommentID, int LikeValue) throws SQLException {
		Statement st = null;
		ResultSet rs = null;
		String query = "SELECT * FROM Likes WHERE UserID='" + UserID + "' AND CommentID='" + CommentID + "'; ";
		try {
			conn = DriverManager.getConnection(url, username, password);
			st = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
			rs = st.executeQuery(query);
			if (!rs.next()) {
				st.execute("INSERT INTO Likes (UserID, CommentID, LikeValue) values (" + UserID + ", " + CommentID + ", " + LikeValue + ");");
			}
			else {
				int currLike = rs.getInt("LikeValue");
				if (currLike == LikeValue) rs.deleteRow();
				else {
					rs.updateInt("LikeValue", LikeValue);
					rs.updateRow();
				}
			}
		}
		finally {
			conn.close();
			st.close();
			rs.close();
		}
	}
}