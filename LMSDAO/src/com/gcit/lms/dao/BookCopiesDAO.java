package com.gcit.lms.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.gcit.lms.entity.BookCopies;
public class BookCopiesDAO extends BaseDAO<BookCopies>{
	
	public BookCopiesDAO(Connection conn) {
		super(conn);
	}

	public void saveBookCopies(BookCopies bookCopies) throws SQLException {
		save("INSERT INTO tbl_book_copies noOfCopies VALUES (?, ?, ?)", new Object[] { bookCopies.getBook().getBookId(), bookCopies.getLibraryBranch().getBranchId(), bookCopies.getNoOfCopies() });
	}
	
//	public void saveBookAuthor(BookCopies bookCopies) throws SQLException {
//		for(Author a: book.getAuthors()){
//			save("INSERT INTO tbl_book_authors VALUES (?, ?)", new Object[] { book.getBookId(), a.getAuthorId()});
//		}
//	}
	
//	public Integer saveBookID(BookCopies bookCopies) throws SQLException {
//		return saveWithID("INSERT INTO tbl_book (bookName) VALUES (?)", new Object[] { book.getTitle() });
//	}

	public void updateBookCopies(BookCopies bookCopies) throws SQLException {
		save("UPDATE tbl_book_copies SET noOfCopies = ? WHERE bookId = ? AND branchId =?", new Object[] { bookCopies.getNoOfCopies(),  bookCopies.getBook().getBookId(), bookCopies.getLibraryBranch().getBranchId()});
	}

	public void deleteBookCopies(BookCopies bookCopies) throws SQLException {
		save("DELETE FROM tbl_book_copies WHERE bookId = ? AND branchId =?", new Object[] { bookCopies.getBook().getBookId(), bookCopies.getLibraryBranch().getBranchId() });
	}

//	public List<BookCopies> readAllBooks() throws SQLException {
//		return readAll("SELECT * FROM tbl_book", null);
//	}
	
	public List<BookCopies> readBookCopiesByTitleAndBranchName(String bookTitle, String branchName) throws SQLException {
		return readAll("SELECT * FROM tbl_book_copies WHERE bookId IN (SELECT bookId FROM tbl_book WHERE title = ? ) AND branchId IN (SELECT branchId FROM tbl_library_branch WHERE branchName = ?)", new Object[]{bookTitle, branchName});
	}

	@Override
	public List<BookCopies> extractData(ResultSet rs) throws SQLException {
		BookDAO adao = new BookDAO(conn);
		LibraryBranchDAO bdao = new LibraryBranchDAO(conn);
		List<BookCopies> bookCopies = new ArrayList<>();
		while (rs.next()) {
			BookCopies b = new BookCopies();
			b.setNoOfCopies(rs.getInt("noOfCopies"));
			b.setBook((adao.readAllFirstLevel("SELECT * FROM tbl_book WHERE bookId = ?", new Object[]{b.getBook().getBookId()})).get(0));
			b.setLibraryBranch((bdao.readAllFirstLevel("SELECT * FROM tbl_library_branch WHERE branchId = ?)", new Object[]{b.getLibraryBranch().getBranchId()}).get(0)));
			bookCopies.add(b);
		}
		return bookCopies;
	}
	
	@Override
	public List<BookCopies> extractDataFirstLevel(ResultSet rs) throws SQLException {
		List<BookCopies> bookCopies = new ArrayList<>();
		while (rs.next()) {
			BookCopies b = new BookCopies();
			b.setNoOfCopies(rs.getInt("noOfCopies"));
			bookCopies.add(b);
		}
		return bookCopies;
	}

}
