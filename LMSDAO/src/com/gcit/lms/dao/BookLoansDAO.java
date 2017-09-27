package com.gcit.lms.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.gcit.lms.entity.BookLoans;

public class BookLoansDAO extends BaseDAO <BookLoans>{
	public BookLoansDAO(Connection conn) {
		super(conn);
	}

	public void saveBookCopies(BookLoans bookLoans) throws SQLException {
		save("INSERT INTO tbl_book_loans VALUES (?, ?, ?, ?, ?, ?)", new Object[] { bookLoans.getBook().getBookId(), bookLoans.getLibraryBranch().getBranchId(), bookLoans.getBorrower().getCardNo(), bookLoans.getDateOut(), bookLoans.getDueDate(), bookLoans.getDateIn() });
	}
	
//	public void saveBookAuthor(BookLoans bookLoans) throws SQLException {
//		for(Author a: book.getAuthors()){
//			save("INSERT INTO tbl_book_authors VALUES (?, ?)", new Object[] { book.getBookId(), a.getAuthorId()});
//		}
//	}
//	
//	public Integer saveBookID(BookLoans bookLoans) throws SQLException {
//		return saveWithID("INSERT INTO tbl_book (bookName) VALUES (?)", new Object[] { book.getTitle() });
//	}

	public void updateDueDate(BookLoans bookLoans) throws SQLException {
		save("UPDATE tbl_book_loans SET dueDate = ? WHERE bookId = ? AND branchId = ? AND cardNo = ?", new Object[] { bookLoans.getBook().getBookId(), bookLoans.getLibraryBranch().getBranchId(), bookLoans.getBorrower().getCardNo() });
	}
	public void updateDueIn(BookLoans bookLoans) throws SQLException {
		save("UPDATE tbl_book_loans SET dueIn = ? WHERE bookId = ? AND branchId = ? AND cardNo = ?", new Object[] { bookLoans.getBook().getBookId(), bookLoans.getLibraryBranch().getBranchId(), bookLoans.getBorrower().getCardNo() });
	}

	public void deleteBook(BookLoans bookLoans) throws SQLException {
		save("DELETE FROM tbl_book_loans WHERE bookId = ? AND branchId = ? AND cardNo = ?", new Object[] { bookLoans.getBook().getBookId(), bookLoans.getLibraryBranch().getBranchId(), bookLoans.getBorrower().getCardNo() });
	}
//
//	public List<BookLoans> readAllBooks() throws SQLException {
//		return readAll("SELECT * FROM tbl_book", null);
//	}
	
	public List<BookLoans> readBooksByTitle(String bookTitle, String branchName, String borrowerName) throws SQLException {
		return readAll("SELECT * FROM tbl_book_loans WHERE bookId IN (SELECT bookId FROM tbl_book WHERE title = ? ) AND branchId IN (SELECT branchId FROM tbl_library_branch WHERE branchName = ?) AND cardNo IN (SELECT cardNo FROM tbl_borrower WHERE name = ?)", new Object[]{bookTitle, branchName, borrowerName});
	}

	@Override
	public List<BookLoans> extractData(ResultSet rs) throws SQLException {
		BookDAO adao = new BookDAO(conn);
		BorrowerDAO bdao = new BorrowerDAO(conn);
		LibraryBranchDAO cdao = new LibraryBranchDAO(conn);
		List<BookLoans> bookLoans = new ArrayList<>();
		while (rs.next()) {
			BookLoans b = new BookLoans();
			b.setDateIn(rs.getString("dateIn"));
			b.setDueDate(rs.getString("dueDate"));
			b.setDateOut(rs.getString("dateOut"));
			b.setBook((adao.readAllFirstLevel("SELECT * FROM tbl_book WHERE bookId = ?)", new Object[]{b.getBook().getBookId()})).get(0));
			b.setBorrower((bdao.readAllFirstLevel("SELECT * FROM tbl_borrower WHERE cardNo = ?)", new Object[]{b.getBorrower().getCardNo()})).get(0));
			b.setLibraryBranch((cdao.readAllFirstLevel("SELECT * FROM tbl_library_branch WHERE branchId = ?)", new Object[]{b.getLibraryBranch().getBranchId()})).get(0));
			//do the same for genres
			//do the same for One Publisher
			bookLoans.add(b);
		}
		return bookLoans;
	}
	
	@Override
	public List<BookLoans> extractDataFirstLevel(ResultSet rs) throws SQLException {
		List<BookLoans> bookLoans = new ArrayList<>();
		while (rs.next()) {
			BookLoans b = new BookLoans();
			b.setDateIn(rs.getString("dateIn"));
			b.setDueDate(rs.getString("dueDate"));
			b.setDateOut(rs.getString("dateOut"));
			bookLoans.add(b);
		}
		return bookLoans;
	}

}
