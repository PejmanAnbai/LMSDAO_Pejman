package com.gcit.lms.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.gcit.lms.entity.Book;
import com.gcit.lms.entity.Genre;

public class GenreDAO extends BaseDAO <Genre> {
	public GenreDAO(Connection conn) {
		super(conn);
	}

	public void saveGenre(Genre genre) throws SQLException {
		save("INSERT INTO tbl_genre (genre_name) VALUES (?)", new Object[] { genre.getGenreName() });
	}
	
	public void saveGenreAuthor(Genre genre) throws SQLException {
		for(Book a: genre.getBooks()){
			save("INSERT INTO tbl_book_genres VALUES (?, ?)", new Object[] { genre.getGenreId(), a.getBookId()});
		}
	}
	
	public Integer saveGenreID(Genre genre) throws SQLException {
		return saveWithID("INSERT INTO tbl_genre (genre_name) VALUES (?)", new Object[] { genre.getGenreName() });
	}

	public void updateGenre(Genre genre) throws SQLException {
		save("UPDATE tbl_genre SET genre_name = ? WHERE genre_id = ?", new Object[] { genre.getGenreName(), genre.getGenreId() });
	}

	public void deleteGenre(Genre genre) throws SQLException {
		save("DELETE FROM tbl_genre WHERE genre_id = ?", new Object[] { genre.getGenreId() });
	}

	public List<Genre> readAllGenre() throws SQLException {
		return readAll("SELECT * FROM tbl_genre", null);
	}
	
	public List<Genre> readGenresByName(String genreName) throws SQLException {
		genreName = "%"+ genreName +"%";
		return readAll("SELECT * FROM tbl_genre WHERE genre_name like ?", new Object[]{genreName});
	}

	@Override
	public List<Genre> extractData(ResultSet rs) throws SQLException {
		BookDAO adao = new BookDAO(conn);
		List<Genre> genre = new ArrayList<>();
		while (rs.next()) {
			Genre b = new Genre();
			b.setGenreId(rs.getInt("genre_id"));
			b.setGenreName(rs.getString("genre_name"));
			b.setBooks(adao.readAllFirstLevel("SELECT * FROM tbl_book WHERE bookId IN (SELECT bookId FROM tbl_book_genre WHERE genre_id = ?)", new Object[]{b.getGenreId()}));
			//do the same for genres
			//do the same for One Publisher
			genre.add(b);
		}
		return genre;
	}
	
	@Override
	public List<Genre> extractDataFirstLevel(ResultSet rs) throws SQLException {
		List<Genre> genres = new ArrayList<>();
		while (rs.next()) {
			Genre b = new Genre();
			b.setGenreId(rs.getInt("genre_id"));
			b.setGenreName(rs.getString("genre_name"));
			genres.add(b);
		}
		return genres;
	}
}
