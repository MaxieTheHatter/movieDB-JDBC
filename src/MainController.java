
//STEP 1. Import required packages
import java.sql.*;
import java.util.Scanner;

public class MainController {
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/moviedb";
	static Scanner keyboard = new Scanner(System.in);

	// Database credentials
	static final String USER = "root";
	static final String PASS = "";

	public static void main(String[] args) {
		Connection conn = null;
		Statement stmt = null;
		try {
			// STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// STEP 3: Open a connection
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			// STEP 4: Execute a query
			System.out.println("Creating statement...");
			stmt = conn.createStatement();
			mainMenu(stmt);
			stmt.close();
			conn.close();
		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException se2) {
			} // nothing we can do
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			} // end finally try
		} // end try
		System.out.println("Goodbye!");
	}// end main

	//main menu, asks user what they want to do and executes the appropriate method
	private static void mainMenu(Statement stmt) throws SQLException, InterruptedException {
		System.out.println("Welcome to the movie database!");
		System.out.println("What would you like to do? \n1. Browse by movie genre \n2. Search by movie title");
		System.out.println("3. Browse by movie rating \n4. List all movies \n5. Manage database (add or remove movie)");
		System.out.println("6. Exit program");
		//Takes user input while making sure it's a number
		while (!keyboard.hasNextInt()) {
			System.out.println("Invalid input, please use numbers");
			keyboard.next();
		}
		int choice = keyboard.nextInt();
		keyboard.nextLine();
		switch (choice) {
		case (1):
			listByGenre(stmt);
			break;
		case (2):
			searchByTitle(stmt);
			break;
		case (3):
			sortByRating(stmt);
			break;
		case (4):
			listAll(stmt);
			break;
		case (5):
			manageMovies(stmt);
			break;
		case (6):
			System.out.println("Thank you for using the Personal Movie DataBase! \nShutting down...");
			System.exit(0);
			break;
		default:
			System.out.println("Invalid choice");
			mainMenu(stmt); //returns to the menu if no valid choice is entered
			break;
		}
	}

	//sub-menu where the user can choose between different management options
	private static void manageMovies(Statement stmt) throws SQLException, InterruptedException {
		System.out.println("Please choose action:");
		System.out.println("1. Add movie \n2. remove movie \n3. Update movie \n4. Back to main menu");
		while (!keyboard.hasNextInt()) {
			System.out.println("Invalid input, please use numbers");
			keyboard.next();
		}
		int choice = keyboard.nextInt();
		keyboard.nextLine();
		switch (choice) {
		case (1):
			addMovie(stmt);
			break;
		case (2):
			deleteMovie(stmt);
			break;
		case (3):
			editMovie(stmt);
			break;
		case (4):
			mainMenu(stmt);
			break;
		default:
			System.out.println("Invalid choice");
			manageMovies(stmt); //resets the menu and asks the user again
			break;
		}
	}

	private static void listAll(Statement stmt) throws SQLException, InterruptedException {
		String sql = "SELECT title, releaseYear, genres.type, concat(directors.firstName, ' ', directors.lastName),"
				+ "concat(actors.firstName, ' ', actors.lastName),"
				+ "rating FROM movies JOIN genres ON movies.genreID = genres.genreID "
				+ "JOIN directors ON movies.directorID = directors.ID JOIN actors ON movies.actorID = actors.ID;";
		ResultSet rs = stmt.executeQuery(sql);

		while (rs.next()) {
			// Retrieve by column name
			String title = rs.getString("title");
			int released = rs.getInt("releaseYear");
			String leadActor = rs.getString("concat(actors.firstName, ' ', actors.lastName)");
			String director = rs.getString("concat(directors.firstName, ' ', directors.lastName)");
			int rating = rs.getInt("rating");
			String genre = rs.getString("genres.type");

			// Display values
			System.out.print("Movie title: " + title + " | ");
			System.out.print("Genre: " + genre + " | ");
			System.out.print("Released: " + released + " | ");
			System.out.print("Lead actor: " + leadActor + " | ");
			System.out.print("Directed by: " + director + " | ");
			System.out.println("Rating: " + rating);
		}
		System.out.println("Listing done. Returning to main menu...");
		Thread.sleep(3000); //added delay, just to slow down the program (mostly to try it out)
		rs.close();
		mainMenu(stmt);
	}

	//lists movies by genre by users choice
	private static void listByGenre(Statement stmt) throws SQLException, InterruptedException {
		System.out.println(
				"Choose genre to list: \n1. Action \n2. Comedy \n3. Horror \n4. Romantic \n5. Thriller \n6. Go back to main menu");
		while (!keyboard.hasNextInt()) {
			System.out.println("Invalid input, please use numbers");
			keyboard.next();
		}
		int choice = keyboard.nextInt();
		keyboard.nextLine(); //clear scanner buffer, otherwise Strings registers wrong value
		if (choice == 6) {
			mainMenu(stmt);
		} else if (choice < 1 || choice > 5) {
			System.out.println("Invalid entry, please try again");
			listByGenre(stmt);
		}

		String sql = "SELECT title, releaseYear, genres.type, concat(directors.firstName, ' ', directors.lastName),"
				+ "concat(actors.firstName, ' ', actors.lastName),"
				+ "rating FROM movies JOIN genres ON movies.genreID = genres.genreID "
				+ "JOIN directors ON movies.directorID = directors.ID JOIN actors ON movies.actorID = actors.ID "
				+ "WHERE movies.genreID = " + choice + ";";
		ResultSet rs = stmt.executeQuery(sql);

		while (rs.next()) {
			// Retrieve by column name
			String title = rs.getString("title");
			int released = rs.getInt("releaseYear");
			String leadActor = rs.getString("concat(actors.firstName, ' ', actors.lastName)");
			String director = rs.getString("concat(directors.firstName, ' ', directors.lastName)");
			int rating = rs.getInt("rating");
			String genre = rs.getString("genres.type");

			// Display values
			System.out.print("Movie title: " + title + " | ");
			System.out.print("Genre: " + genre + " | ");
			System.out.print("Released: " + released + " | ");
			System.out.print("Lead actor: " + leadActor + " | ");
			System.out.print("Directed by: " + director + " | ");
			System.out.println("Rating: " + rating);
		}
		System.out.println("Listing done. Returning to main menu...");
		Thread.sleep(3000);
		rs.close();
		mainMenu(stmt);

	}

	//sorts movies by rating, highest to lowest
	private static void sortByRating(Statement stmt) throws SQLException, InterruptedException {
		String sql = "SELECT title, releaseYear, genres.type, concat(directors.firstName, ' ', directors.lastName),"
				+ "concat(actors.firstName, ' ', actors.lastName),"
				+ "rating FROM movies JOIN genres ON movies.genreID = genres.genreID "
				+ "JOIN directors ON movies.directorID = directors.ID JOIN actors ON movies.actorID = actors.ID "
				+ "ORDER BY rating DESC;";
		ResultSet rs = stmt.executeQuery(sql);

		while (rs.next()) {
			// Retrieve by column name
			String title = rs.getString("title");
			int released = rs.getInt("releaseYear");
			String leadActor = rs.getString("concat(actors.firstName, ' ', actors.lastName)");
			String director = rs.getString("concat(directors.firstName, ' ', directors.lastName)");
			int rating = rs.getInt("rating");
			String genre = rs.getString("genres.type");

			// Display values
			System.out.print("Movie title: " + title + " | ");
			System.out.print("Genre: " + genre + " | ");
			System.out.print("Released: " + released + " | ");
			System.out.print("Lead actor: " + leadActor + " | ");
			System.out.print("Directed by: " + director + " | ");
			System.out.println("Rating: " + rating);
		}
		System.out.println("Listing done. Returning to main menu...");
		Thread.sleep(3000);
		rs.close();
		mainMenu(stmt);
	}

	//Prompts user for input and makes a search for movie titles containing the input
	private static void searchByTitle(Statement stmt) throws SQLException, InterruptedException {
		System.out.println("Enter search phrase (Only movie title implemented)");
		String search = keyboard.nextLine();
		String sql = "SELECT title, releaseYear, genres.type, concat(directors.firstName, ' ', directors.lastName),"
				+ "concat(actors.firstName, ' ', actors.lastName),"
				+ "rating FROM movies JOIN genres ON movies.genreID = genres.genreID "
				+ "JOIN directors ON movies.directorID = directors.ID JOIN actors ON movies.actorID = actors.ID WHERE movies.title LIKE '%"
				+ search + "%';";
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			// Retrieve by column name
			String title = rs.getString("title");
			int released = rs.getInt("releaseYear");
			String leadActor = rs.getString("concat(actors.firstName, ' ', actors.lastName)");
			String director = rs.getString("concat(directors.firstName, ' ', directors.lastName)");
			int rating = rs.getInt("rating");
			String genre = rs.getString("genres.type");

			// Display values
			System.out.print("Movie title: " + title + " | ");
			System.out.print("Genre: " + genre + " | ");
			System.out.print("Released: " + released + " | ");
			System.out.print("Lead actor: " + leadActor + " | ");
			System.out.print("Directed by: " + director + " | ");
			System.out.println("Rating: " + rating);
		}
		System.out.println("<--- End of search ---> \n \nGoing back to main menu..");
		Thread.sleep(3000);
		rs.close();
		mainMenu(stmt);
	}

	//adds movies by sending parameters to addMovie() stored in the database
	private static void addMovie(Statement stmt) throws SQLException, InterruptedException {
		Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
		System.out.println("Enter movie title:");
		String title = keyboard.nextLine();

		System.out.println("What genre is the movie? \n1. Action \n2. Comedy \n3. Horror \n4. Romantic \n5. Thriller");
		int genre = keyboard.nextInt();

		System.out.println("Enter release year:");
		int released = keyboard.nextInt();
		keyboard.nextLine(); //clear scanner buffer

		System.out.println("Enter directors first name:");
		String directorFname = keyboard.nextLine();

		System.out.println("Enter directors surname:");
		String directorLname = keyboard.nextLine();

		System.out.println("Enter lead actors first name:");
		String actorFname = keyboard.nextLine();

		System.out.println("Enter lead actors surname:");
		String actorLname = keyboard.nextLine();

		System.out.println("How would you rate the movie? (1-10)");
		int rating = keyboard.nextInt();
		keyboard.nextLine();

		System.out.println("Connecting to database...");

		CallableStatement cstmt = null;
		try {
			String sql = "{call moviedb.addMovie(?, ?, ?, ?, ?, ?, ?, ?)}";
			cstmt = conn.prepareCall(sql);
			cstmt.setString(1, title);
			cstmt.setInt(2, released);
			cstmt.setInt(3, rating);
			cstmt.setInt(4, genre);
			cstmt.setString(5, directorFname);
			cstmt.setString(6, directorLname);
			cstmt.setString(7, actorFname);
			cstmt.setString(8, actorLname);
			cstmt.execute();
		} catch (SQLException e) {
		} finally {
		}
		System.out.println("Movie " + title + " added to database. \n Returning to main menu");
		Thread.sleep(3000);
		mainMenu(stmt);
	}

	//deletes an entry from table movies, chosen by movie ID
	private static void deleteMovie(Statement stmt) throws SQLException, InterruptedException {
		System.out.println("Choose movie by ID to delete or type exit to go back to the main menu");

		String sql = "SELECT ID, title FROM movies;";
		ResultSet rs = stmt.executeQuery(sql);

		while (rs.next()) {
			// Retrieve by column name
			int ID = rs.getInt("ID");
			String title = rs.getString("title");

			// Display values
			System.out.print("Movie ID: " + ID + " | ");
			System.out.println("Movie title: " + title);

		}

		String choice = keyboard.nextLine();
		if (choice.equals("exit")) {
			mainMenu(stmt);
		} else if (choice.matches("\\d+")) {
			sql = "DELETE FROM movies WHERE ID = " + choice + ";";
			stmt.executeUpdate(sql);
			System.out.println("Deleting movie...");
			Thread.sleep(1000);
			System.out.println("Done! returning to main menu");
		} else {
			System.out.println("Invalid choice");
			deleteMovie(stmt);
		}
		rs.close();
		Thread.sleep(2000);
		mainMenu(stmt);
	}

	//allows user to edit the title of a movie
	private static void editMovie(Statement stmt) throws SQLException, InterruptedException {
		System.out.println("Choose movie by ID to edit or type exit to go back to the main menu");

		String sql = "SELECT ID, title FROM movies;";
		ResultSet rs = stmt.executeQuery(sql);

		while (rs.next()) {
			// Retrieve by column name
			int ID = rs.getInt("ID");
			String title = rs.getString("title");

			// Display values
			System.out.print("Movie ID: " + ID + " | ");
			System.out.println("Movie title: " + title);

		}

		String choice = keyboard.nextLine();
		if (choice.equals("exit")) {
			mainMenu(stmt);
		} else if (choice.matches("\\d+")) {
			System.out.println("Enter new movie title:");
			String newTitle = keyboard.nextLine();
			sql = "UPDATE movies SET title = '" + newTitle + "' WHERE ID = " + choice + ";";
			stmt.executeUpdate(sql);
			System.out.println("updating movietitle...");
			Thread.sleep(1000);
			System.out.println("Done! returning to main menu");
		} else {
			System.out.println("Invalid choice");
			deleteMovie(stmt);
		}
		rs.close();
		Thread.sleep(2000);
		mainMenu(stmt);
	}
}