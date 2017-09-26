package com.gcit.lms.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.gcit.lms.entity.Author;
import com.gcit.lms.entity.Book;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class BookDAO extends BaseDAO {

	public BookDAO(Connection conn) {
		super(conn);
	}

	public void saveBook(Book book) throws SQLException {
		save("INSERT INTO tbl_book (bookName) VALUES (?)", new Object[] { book.getTitle() });
	}
	
	public void saveBookAuthor(Book book) throws SQLException {
		for(Author a: book.getAuthors()){
			save("INSERT INTO tbl_book_authors VALUES (?, ?)", new Object[] { book.getBookId(), a.getAuthorId()});
		}
	}
	
	public Integer saveBookID(Book book) throws SQLException {
		return saveWithID("INSERT INTO tbl_book (bookName) VALUES (?)", new Object[] { book.getTitle() });
	}

	public void updateBook(Book book) throws SQLException {
		save("UPDATE tbl_book SET bookName = ? WHERE bookId = ?", new Object[] { book.getTitle(), book.getBookId() });
	}

	public void deleteBook(Book book) throws SQLException {
		save("DELETE FROM tbl_book WHERE bookId = ?", new Object[] { book.getBookId() });
	}

	public List<Book> readAllBooks() throws SQLException {
		return readAll("SELECT * FROM tbl_book", null);
	}
	
	public List<Book> readBooksByTitle(String bookTitle) throws SQLException {
		bookTitle = "%"+bookTitle+"%";
		return readAll("SELECT * FROM tbl_book WHERE title like ?", new Object[]{bookTitle});
	}

	@Override
	public List extractData(ResultSet rs) throws SQLException {
		AuthorDAO adao = new AuthorDAO(conn);
		List<Book> books = new ArrayList<>();
		while (rs.next()) {
			Book b = new Book();
			b.setBookId(rs.getInt("bookId"));
			b.setTitle(rs.getString("title"));
			b.setAuthors(adao.readAllFirstLevel("SELECT * FROM tbl_author WHERE authorId IN (SELECT authorId FROM tbl_book_authors WHERE bookId = ?)", new Object[]{b.getBookId()}));
			//do the same for genres
			//do the same for One Publisher
			books.add(b);
		}
		return books;
	}
	
	@Override
	public List extractDataFirstLevel(ResultSet rs) throws SQLException {
		List<Book> books = new ArrayList<>();
		while (rs.next()) {
			Book b = new Book();
			b.setBookId(rs.getInt("bookId"));
			b.setTitle(rs.getString("title"));
			books.add(b);
		}
		return books;
	}
}
