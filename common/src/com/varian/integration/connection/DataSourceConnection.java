package com.varian.integration.connection;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import com.sap.me.frame.BaseDataSource;
import com.sap.me.frame.Data;
import com.sap.me.frame.jdbc.DynamicQuery;
import com.sap.me.frame.jdbc.DynamicQueryFactory;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

public class DataSourceConnection {
	@SuppressWarnings("unused")
	private static Statement st=null;
	private Connection con ;
	@SuppressWarnings("unused")
	private DynamicQuery sb = DynamicQueryFactory.newInstance();
	private Data stepdata = null;
	private BaseDataSource bds = DataSourceConnection.getDSConnection();
	private static Location logger = Location.getLocation(DataSourceConnection.class.getName());

	public static BaseDataSource getDSConnection(){
		BaseDataSource baseDS = new BaseDataSource();
		//The init method initializes with data source
		baseDS.init("jdbc/wipPool",true);
		
		return baseDS;
	}
	

	public static Connection getSQLConnection(){
		BaseDataSource baseDS = new BaseDataSource();
		Connection conn;
		//The init method initializes with data source
		baseDS.init("jdbc/wipPool",true);
		conn = baseDS.getDBConnection();
		return conn;		
	}
	
   @SuppressWarnings("finally")
   public Data executedbquery(DynamicQuery db){
	   con = getSQLConnection();
	  
	   try{
		st=con.createStatement();
	    stepdata=bds.executeQuery(db);
			
	   }catch(SQLException e){
			e.printStackTrace();
			SimpleLogger.log(Severity.ERROR,Category.SYS_SERVER,logger,"MESDK:CUSTOM MSG - ",e.getMessage());
		}
	   
	   finally {
			try {
				con.close();
			} 
			catch (Exception e) {
				SimpleLogger.log(Severity.ERROR,Category.SYS_SERVER,logger,"MESDK:CUSTOM MSG - ",e.getMessage());
			}
	   return stepdata;
   }
   }	
}
