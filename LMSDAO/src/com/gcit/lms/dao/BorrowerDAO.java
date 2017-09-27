package com.gcit.lms.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.gcit.lms.entity.BookLoans;
import com.gcit.lms.entity.Borrower;

public class BorrowerDAO extends BaseDAO <Borrower>{
	
	public BorrowerDAO(Connection conn) {
		super(conn);
	}

	public void saveBorrower(Borrower borrower) throws SQLException {
		save("INSERT INTO tbl_borrower (name, address, phone) VALUES (?, ?, ?)", new Object[] { borrower.getName(), borrower.getAddress(), borrower.getPhone() });
	}
	
	public void saveBookLoans(Borrower borrower) throws SQLException {
		for(BookLoans a: borrower.getBookLoans()){
			save("INSERT INTO tbl_book_loans VALUES (?, ?, ?, ?, ?, null)", new Object[] { a.getBook().getBookId(), a.getLibraryBranch(), borrower.getCardNo(), a.getDateOut(), a.getDueDate()});
		}
	}
	
	public Integer saveBookID(Borrower borrower) throws SQLException {
		return saveWithID("INSERT INTO tbl_borrower (name, address, phone) VALUES (?, ?, ?)", new Object[] { borrower.getName(), borrower.getAddress(), borrower.getPhone() });
	}

	public void updateBook(Borrower borrower) throws SQLException {
		save("UPDATE tbl_borrower SET name = ?, address = ?, phone = ? WHERE cardNo = ?", new Object[] { borrower.getName(), borrower.getAddress(), borrower.getPhone(), borrower.getCardNo() });
	}

	public void deleteBook(Borrower borrower) throws SQLException {
		save("DELETE FROM tbl_borrower WHERE cardNo = ?", new Object[] {borrower.getCardNo() });
	}

	public List<Borrower> readAllBorrowers() throws SQLException {
		return readAll("SELECT * FROM tbl_borrower", null);
	}
	
	public List<Borrower> readBorrowerByName(String borrowerName) throws SQLException {
		borrowerName = "%"+borrowerName+"%";
		return readAll("SELECT * FROM tbl_borrower WHERE name like ?", new Object[]{borrowerName});
	}
	public List<Borrower> readBorrowerByAddress(String borrowerAddress) throws SQLException {
		borrowerAddress = "%"+borrowerAddress+"%";
		return readAll("SELECT * FROM tbl_borrower WHERE address like ?", new Object[]{borrowerAddress});
	}
	public List<Borrower> readBorrowerByPhone(String borrowerPhone) throws SQLException {
		borrowerPhone = "%"+borrowerPhone+"%";
		return readAll("SELECT * FROM tbl_borrower WHERE phone like ?", new Object[]{borrowerPhone});
	}

	@Override
	public List<Borrower> extractData(ResultSet rs) throws SQLException {
		BookLoansDAO adao = new BookLoansDAO(conn);
		List<Borrower> borrowers = new ArrayList<>();
		while (rs.next()) {
			Borrower b = new Borrower();
			b.setCardNo(rs.getInt("cardNo"));
			b.setName(rs.getString("name"));
			b.setAddress(rs.getString("address"));
			b.setPhone(rs.getString("phone"));
			b.setBookLoans(adao.readAllFirstLevel("SELECT * FROM tbl_book_loans WHERE cardNo = ?)", new Object[]{b.getCardNo()}));
			//do the same for genres
			//do the same for One Publisher
			borrowers.add(b);
		}
		return borrowers;
	}
	
	@Override
	public List<Borrower> extractDataFirstLevel(ResultSet rs) throws SQLException {
		List<Borrower> borrowers = new ArrayList<>();
		while (rs.next()) {
			Borrower b = new Borrower();
			b.setCardNo(rs.getInt("cardNo"));
			b.setName(rs.getString("name"));
			b.setAddress(rs.getString("address"));
			b.setPhone(rs.getString("phone"));
			borrowers.add(b);
		}
		return borrowers;
	}

}
