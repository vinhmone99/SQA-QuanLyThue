package test;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import dao.BillDao;
import dao.DAO;
import model.Bill;

public class TestKhaiBao extends TestDriver{
	ChromeDriver driver = getDriver();
	private BillDao khaibaothuetest = new BillDao();
	String date = "01-01-2021";
	String songuoi = "0";
	String luong = "11000000";
	String mst = "12345678";
	String tienThue = "0";
	Bill bill1 = new Bill(date, luong, songuoi, mst, tienThue);
	Bill bill2 = new Bill();
	DAO dao = new DAO();
	Connection con = dao.con;
	
	@Test
	public void khaibao_test() throws SQLException {
		driver.get("http://localhost:8080/ThueSQA/login.jsp");
		WebElement username = driver.findElement(By.name("mst"));
		username.sendKeys(mst);
		WebElement password = driver.findElement(By.name("password"));
		password.sendKeys("1");
		driver.findElement(By.name("login_btn")).click();
//		String title1 = driver.getTitle();
//		String expect1 = "Trang chủ khai báo thuế";
//		assertEquals(expect1, title1);
		
		driver.get("http://localhost:8080/ThueSQA/khaibaothue.jsp");
		WebElement Date = driver.findElement(By.name("date"));
		Date.sendKeys(date);
		WebElement Luong = driver.findElement(By.name("luong"));
		Luong.sendKeys(luong);
		WebElement Songuoi = driver.findElement(By.name("songuoi"));
		Songuoi.sendKeys(songuoi);
//		con.setAutoCommit(false);
		driver.findElement(By.name("submit_btn")).click();
//		DAO.con.rollback();
		String sql = "SELECT * FROM bill WHERE date = ? AND luong = ? AND songuoi = ? AND tienthue = ? AND maSoThue = ?";
	    try (
	            // Step 2:Create a statement using connection object
	            PreparedStatement preparedStatement = con.prepareStatement(sql)) {
	            preparedStatement.setString(1, bill1.getDate());
	            preparedStatement.setString(2, bill1.getLuong());
	            preparedStatement.setString(3, bill1.getSonguoi());
	            preparedStatement.setString(4, bill1.getTienThue());
	            preparedStatement.setString(5, bill1.getMst());

	            System.out.println(preparedStatement);
	            // Step 3: Execute the query or update query
	            ResultSet rs = preparedStatement.executeQuery();
	            
	            while (rs.next()) {
	            	bill2 = new Bill(
	            			rs.getString(2),
	            			rs.getString(3),
	            			rs.getString(4),
	            			rs.getString(5),
	            			rs.getString(6),
	            			rs.getBoolean(7));
	            	System.out.println(bill2.toString());
	            }
	            
	    	    assertEquals(bill2.getDate(), bill1.getDate());
	    	    assertEquals(bill2.getLuong(), bill1.getLuong());
	    	    assertEquals(bill2.getSonguoi(), bill1.getSonguoi());
	    	    assertEquals(bill2.getTienThue(), bill1.getTienThue());
	    	    assertEquals(bill2.getMst(), bill1.getMst());
	    	    assertEquals(bill2.isStatus(), bill1.isStatus());

	        } catch (SQLException e) {
	            // process sql exception
	            e.printStackTrace();
	        } finally {
//	        	con.rollback();
	        	String rollback_sql = "Delete From bill where date = ? AND luong = ? AND songuoi = ? AND tienthue = ? AND maSoThue = ?";
	        	 RollbackDB(rollback_sql);
	        	 driver.close();
	        }
	    
		
	}
	
	@Test
	public void dongthue_test() throws SQLException {
		String rollback_update = "update bill set status = false where idBill = ? ";
	
		driver.get("http://localhost:8080/ThueSQA/login.jsp");
		WebElement username = driver.findElement(By.name("mst"));
		username.sendKeys(mst);
		WebElement password = driver.findElement(By.name("password"));
		password.sendKeys("1");
		driver.findElement(By.name("login_btn")).click();
		driver.get("http://localhost:8080/ThueSQA/listBill.jsp");
		driver.findElement(By.xpath("//a[@href='dongthue.jsp?ID=2']")).click();
//		con.setAutoCommit(false);
		driver.findElement(By.name("dongthue_btn")).click();
		checkDB("2");
		RollbackUpdate(rollback_update, "2");
		driver.close();
	}
	
	public void checkDB(String id) throws SQLException {
 		String sql = "SELECT status FROM bill WHERE idBill = ?";
	    try (
	            // Step 2:Create a statement using connection object
	            PreparedStatement preparedStatement = con.prepareStatement(sql)) {
	            preparedStatement.setString(1, id);

	            System.out.println(preparedStatement);
	            // Step 3: Execute the query or update query
	            ResultSet rs = preparedStatement.executeQuery();
	            
	            while (rs.next()) {
	            	assertEquals(true, rs.getBoolean(1));
	            }

	        } catch (SQLException e) {
	            // process sql exception
	            e.printStackTrace();
	        } finally {
//	        	 con.rollback();
	        }
 		
	}
	public void RollbackUpdate(String rollback_update, String id) throws SQLException {
		int result =0;
		con.setAutoCommit(true);
 		try(
 				PreparedStatement preparedStatement = con.prepareStatement(rollback_update)){
 				preparedStatement.setString(1, id);
 				result = preparedStatement.executeUpdate();
 				System.out.println(preparedStatement);
 				System.out.println(result);
 			}
 			catch (SQLException e) {
 				 e.printStackTrace();}
 		
	}
	public void RollbackDB(String rollback_sql) throws SQLException {
//	String rollback_sql = "Delete From bill where date = ? AND luong = ? AND songuoi = ? AND tienthue = ? AND maSoThue = ?";
		int result = 0;
		con.setAutoCommit(true);
	try (
            // Step 2:Create a statement using connection object
            PreparedStatement preparedStatement = con.prepareStatement(rollback_sql)) {
            preparedStatement.setString(1, date);
            preparedStatement.setString(2, luong);
            preparedStatement.setString(3, songuoi);
            preparedStatement.setString(4, tienThue);
            preparedStatement.setString(5, mst);

            System.out.println(preparedStatement);
            // Step 3: Execute the query or update query
            result = preparedStatement.executeUpdate();
            System.out.println("result:::::::::::::::::" + result);

        } catch (SQLException e) {
            // process sql exception
            e.printStackTrace();
        }
	}
}
