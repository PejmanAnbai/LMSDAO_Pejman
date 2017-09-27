package com.gcit.lms.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.gcit.lms.entity.BookCopies;
import com.gcit.lms.entity.BookLoans;
import com.gcit.lms.entity.LibraryBranch;

public class LibraryBranchDAO extends BaseDAO <LibraryBranch>{
	
	public LibraryBranchDAO(Connection conn) {
		super(conn);
	}

	public void saveLibraryBranch(LibraryBranch libraryBranch) throws SQLException {
		save("INSERT INTO tbl_library_branch (branchName, branchAddress) VALUES (?, ?)", new Object[] { libraryBranch.getBranchName(), libraryBranch.getBranchAddress() });
	}
	
	public void saveBookCopies(LibraryBranch libraryBranch) throws SQLException {
		for(BookCopies a: libraryBranch.getBookCopies()){
			save("INSERT INTO tbl_book_copies VALUES (?, ?, ?)", new Object[] { a.getBook().getBookId(), libraryBranch.getBranchId(), a.getNoOfCopies()});
		}
	}
	public void saveBookLoans(LibraryBranch libraryBranch) throws SQLException {
		for(BookLoans a: libraryBranch.getBookLoans()){
			save("INSERT INTO tbl_book_loans VALUES (?, ?, ?, ?, ?, ?)", new Object[] { a.getBook().getBookId(), libraryBranch.getBranchId(), a.getBorrower().getCardNo(), a.getDateOut(), a.getDueDate(), a.getDateIn()});
		}
	}
	
	public Integer saveLibraryBranchID(LibraryBranch libraryBranch) throws SQLException {
		return saveWithID("INSERT INTO tbl_library_branch (branchName, branchAddress) VALUES (?, ?)", new Object[] { libraryBranch.getBranchName(), libraryBranch.getBranchAddress() });
	}

	public void updateLibraryBranch(LibraryBranch libraryBranch) throws SQLException {
		save("UPDATE tbl_library_branch SET branchName = ?, branchAddress = ? WHERE branchId = ?", new Object[] { libraryBranch.getBranchName(), libraryBranch.getBranchAddress(), libraryBranch.getBranchId() });
	}

	public void deleteLibraryBranch(LibraryBranch libraryBranch) throws SQLException {
		save("DELETE FROM tbl_library_branch WHERE branchId = ?", new Object[] { libraryBranch.getBranchId() });
	}

	public List<LibraryBranch> readAllBranches() throws SQLException {
		return readAll("SELECT * FROM tbl_library_branch", null);
	}
	
	public List<LibraryBranch> readLibraryBranchByName(String branchName) throws SQLException {
		branchName = "%"+branchName+"%";
		return readAll("SELECT * FROM tbl_library_branch WHERE branchName like ?", new Object[]{branchName});
	}

	@Override
	public List<LibraryBranch> extractData(ResultSet rs) throws SQLException {
		BookCopiesDAO adao = new BookCopiesDAO(conn);
		BookLoansDAO cdao = new BookLoansDAO(conn);
		List<LibraryBranch> libraryBranch = new ArrayList<>();
		while (rs.next()) {
			LibraryBranch b = new LibraryBranch();
			b.setBranchId(rs.getInt("branchId"));
			b.setBranchName(rs.getString("branchName"));
			b.setBranchAddress(rs.getString("branchAddress"));
			b.setBookCopies(adao.readAllFirstLevel("SELECT * FROM tbl_book_copies WHERE branchId = ?)", new Object[]{b.getBranchId()}));
			b.setBookLoans(cdao.readAllFirstLevel("SELECT * FROM tbl_book_loans WHERE branchId = ?)", new Object[]{b.getBranchId()}));
			//do the same for genres
			//do the same for One Publisher
			libraryBranch.add(b);
		}
		return libraryBranch;
	}
	
	@Override
	public List<LibraryBranch> extractDataFirstLevel(ResultSet rs) throws SQLException {
		List<LibraryBranch> libraryBranch = new ArrayList<>();
		while (rs.next()) {
			LibraryBranch b = new LibraryBranch();
			b.setBranchId(rs.getInt("branchId"));
			b.setBranchName(rs.getString("branchName"));
			b.setBranchAddress(rs.getString("branchAddress"));
			libraryBranch.add(b);
		}
		return libraryBranch;
	}

}
