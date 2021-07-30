package com.revature.iba.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import com.revature.iba.model.Customers;
import com.revature.iba.model.Employee;
import com.revature.iba.util.DbConnection;

public class EmployeeDAOImpl implements EmployeeDAO{
	
	//connecting to the database
	Connection connection=DbConnection.getDbConnection();
	Customers customers=new Customers();
	Employee employee=new Employee();
	
	//Sql Query statements based on our requirement
	private String GET_CUSTOMER_BALANCE="call iba.getCustomerBalance(?,?)";
	private String LOAD_CUSTOMER_BANK_ACCOUNTS="select * from iba.customers";
	private String LOAD_CUSTOMER_DETAILS="select * from iba.customers where accountNumber=?";
	private String VALIDATE_EMPLOYEE="select employeeUserName,employeePassword from iba.employee where employeeUserName=? and employeePassword=? ";
	private String GET_EMPLOYEE_NAME="select employeeFirstName,employeeLastName from iba.employee where employeeUserName=?";
	private String VALIDATE_CUSTOMER="select customerFirstName from iba.customers where accountNumber=?";
	private String GET_CIF_ID="select customerCifId from iba.customers where accountNumber=?";
	private String UPDATE_CIF_ID="update iba.customers set customerCifId=? where accountNumber=?";
	//calling stored procedure 
	
	//method to view account balance using account Number ,return balance
	public int viewAccountBalance(int accountNumber) {
		int accountBalance=-1;
		try {
//			System.out.println("hello");
			CallableStatement statement=connection.prepareCall(GET_CUSTOMER_BALANCE);
//			System.out.println("hi");
			statement.setInt(1, accountNumber);
			statement.registerOutParameter(2, Types.INTEGER);
			statement.setInt(2, accountBalance);
			statement.execute();
			accountBalance=statement.getInt(2);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		System.out.println(accountBalance);
		return accountBalance;
	}
	//method to get all the bank accounts of bank,prints all data in table
	public void getCustomerBankAccounts() {
		// TODO Auto-generated method stub
		ResultSet res=null;
		try {
			Statement statement=connection.createStatement();
			 res=statement.executeQuery(LOAD_CUSTOMER_BANK_ACCOUNTS);
			 ResultSetMetaData rsmd=res.getMetaData();
			 int columnCount=rsmd.getColumnCount();
//			 System.out.println(columnCount);
			 while(res.next()) {
				 for(int i=1;i<=columnCount;i++) {
					 System.out.print(res.getString(i)+"\t");
				 }
				 System.out.println();
			 }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	//method to get particular customer details using account number,return customers object with all the details
	public Customers getCustomerDetails(int accountNumber) {
		// TODO Auto-generated method stub
		ResultSet res=null;
		
		try {
			PreparedStatement statement=connection.prepareStatement(LOAD_CUSTOMER_DETAILS);
			statement.setInt(1, accountNumber);
			res=statement.executeQuery();
			if(res.next()) {
			 customers=new Customers(res.getLong(1),res.getString(2),
					res.getString(3),res.getString(4),res.getString(5),res.getLong(6),res.getInt(7));
			}
			else {
				customers=null;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Account Number does not exist, Please try again");
		}
		
//		System.out.println(customers);
//		System.out.println(customers.getCustomerFirstName());
		return customers;
		
		
	}
	//method to validate the employee ,if exists in the database then return  employee full name
	public String validateEmployee(String employeeUserName,String employeePassword) {
		// TODO Auto-generated method stub
		ResultSet res;
		int res1=1;
		try {
			PreparedStatement statement=connection.prepareStatement(VALIDATE_EMPLOYEE);
			statement.setString(1, employeeUserName);
			statement.setString(2, employeePassword);
			res=statement.executeQuery();
			if(res.next())
				res1=1;
			else 
				res1=0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			System.out.println("You entered Wrong UserName or Password, Please try again");
		}
		if(res1==1) {
//			return employee.getEmployeeFirstName();
			ResultSet res2=null;
			String employeeName=null;
			try {
				PreparedStatement statement=connection.prepareStatement(GET_EMPLOYEE_NAME);
				statement.setString(1, employeeUserName);
				res2=statement.executeQuery();
				if(res2.next()) {
					 employeeName=res2.getString(1)+" "+res2.getString(2);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return employeeName;
		}
		else {
			return null;
		}
	}
	//method to check the customer existence using account number ,return customer name
	public String isCustomerExist(int accountNumber) {
		// TODO Auto-generated method stub
		ResultSet res=null;
		String res1=null;
		try {
			PreparedStatement statement=connection.prepareStatement(VALIDATE_CUSTOMER);
			statement.setInt(1, accountNumber);
			res=statement.executeQuery();
			if(res.next())
				 res1=res.getString(1);
			else
				res1=null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res1;
	}
	public boolean approveAccount(int accountNumber) {
		// TODO Auto-generated method stub
		ResultSet res=null;
		int res1=0;
		int cif=0,newCif=0;
		try {
			PreparedStatement statement=connection.prepareStatement(GET_CIF_ID);
			statement.setInt(1, accountNumber);
			res=statement.executeQuery();
			if(res.next()) {
				cif=res.getInt(1);
			}
			newCif=cif+100000;
		PreparedStatement stat=connection.prepareStatement(UPDATE_CIF_ID);
		stat.setInt(1, newCif);
		stat.setInt(2, accountNumber);
		res1 =stat.executeUpdate();
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(res1!=1) {
			return false;
		}
		else {
			return true;
		}
		
	}
}
