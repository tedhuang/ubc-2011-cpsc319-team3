package classes;

import java.util.*;

import servlets.ServletJobAd.mysqlCmd;

public class DbQuery{

	public final String SELECT			="SELECT ";		//CAUTION: SPACE IMPORTANT
	public final String INSERT			="INSERT ";		//CAUTION: SPACE IMPORTANT
	public final String UPDATE			="UPDATE ";		//CAUTION: SPACE IMPORTANT
	public final String DEL				="DELETE ";		//CAUT CE IMPORTANT
	
	public final String AS				= " AS "; 		//CAUTION: SPACE IMPORTANT
	public final String IN 				= " IN ";		//CAUTION: SPACE IMPORTANT
	public final String OR				= " OR "; 		//CAUTION: SPACE IMPORTANT
	public final String ON				= " ON ";		//CAUTION: SPACE IMPORTANT
	public final String AND 			= " AND ";		//CAUTION: SPACE IMPORTANT
	public final String SET				= " SET ";		//CAUTION: SPACE IMPORTANT
	public final String LIKE			= " LIKE "; 	//CAUTION: SPACE IMPORTANT
	public final String REGEXP		 	= " REGEXP ";	//CAUTION: SPACE IMPORTANT
	public final String WHERE			= " WHERE ";	//CAUTION: SPACE IMPORTANT
	public final String ORDERBY			= " ORDER BY "; //CAUTION: SPACE IMPORTANT
	public final String LIMIT			= " LIMIT "; 	//CAUTION: SPACE IMPORTANT
	public final String DESC			= " DESC "; 		//CAUTION: SPACE IMPORTANT
	public final String INTO			= " INTO "; 		//CAUTION: SPACE IMPORTANT
	public final String FROM			= " FROM "; 		//CAUTION: SPACE IMPORTANT
	public final String VALUES			= " VALUES "; 	//CAUTION: SPACE IMPORTANT
	
	public final String COMA			= ", ";		//CAUTION: SPACE IMPORTANT
	public final String PRNTHS			= "() ";	//CAUTION: SPACE IMPORTANT, insert-pos:1
//	public final String RPRNTHS			= ") ";
	public final String SQUO			= "'";
	public final String EQ				= "=";
	public final String GRTR			= ">";
	public final String SMLR			= "<";
	public final String BR				= "&lt;br /&gt;";
	
	public final StringBuffer wordRegExBuffer 	 =  new StringBuffer("'[[:<:]][[:>:]]' "); //CAUTION: SPACE IMPORTANT, insert-pos:8
	public final StringBuffer SQ				 =  new StringBuffer("'' ");//CAUTION: SPACE IMPORTANT, insert-pos:1
	public final StringBuffer CDATA              =  new StringBuffer("<![CDATA[]]>"); //insert-pos:9
	public final StringBuffer BRKT               =  new StringBuffer("()"); //insert-pos:1
	
	public DbQuery(){
		
	}
/**************************************************************************************************************************************
 * 					GENERIC SESSIONKEY AUTHENTICATION QUERY
 * <==========REMEMBER TO REMOVE bracket when used alone====================>
 * @param sKey
 * @param getArr
 * @param userType
 * @return
 ****************************************************************************************************************************************/
	public StringBuffer sessAuthQuery(String sKey, String[]getArr, String userType){
		
		StringBuffer qSessTb =new StringBuffer();
		StringBuffer qAcctTb =new StringBuffer();
		StringBuffer infoToGet =new StringBuffer();
		for(String str:getArr){
			infoToGet.append(str+ COMA);
		}
		infoToGet.delete(infoToGet.length()-COMA.length(), infoToGet.length());
		qSessTb.append(PRNTHS);
		qAcctTb.append(PRNTHS); //append brackets 
		
		qSessTb.insert(1, SELECT + "idAccount" + FROM + "tableSession" + 
						  WHERE +"sessionKey" + EQ + SQUO +sKey + SQUO);
		qAcctTb.insert(1, SELECT + infoToGet + FROM + "tableAccount " + 
				  		  WHERE +"idAccount" + EQ);
		qAcctTb.insert(qAcctTb.length()-2, qSessTb + AND + "type" + EQ + SQUO + userType + SQUO+ 
										   AND + "status" +EQ +"'active'");
		return qAcctTb;
	}
	
public StringBuffer sessAdminAuthQuery(String sKey, String[]getArr){
		
		StringBuffer qSessTb =new StringBuffer();
		StringBuffer qAcctTb =new StringBuffer();
		StringBuffer infoToGet =new StringBuffer();
		for(String str:getArr){
			infoToGet.append(str+ COMA);
		}
		infoToGet.delete(infoToGet.length()-COMA.length(), infoToGet.length());
		qSessTb.append(PRNTHS);
		qAcctTb.append(PRNTHS); //append brackets 
		
		qSessTb.insert(1, SELECT + "idAccount" + FROM + "tableSession" + 
						  WHERE +"sessionKey" + EQ + SQUO +sKey + SQUO);
		qAcctTb.insert(1, SELECT + infoToGet + FROM + "tableAccount " + 
				  		  WHERE +"idAccount" + EQ);
		qAcctTb.insert(qAcctTb.length()-2, qSessTb + AND + "type" + IN + "('superAdmin', 'admin')" +
										   AND + "status" +EQ +"'active'");
		return qAcctTb;
	}
	
	public  StringBuffer [] buidlSelQuery( String tableName, String[]colToGet, Map<String, Object>conditionMap ){
		StringBuffer[]outputQueries =new StringBuffer[2];
		StringBuffer stmt1 =new StringBuffer();
		StringBuffer conditionBuf =new StringBuffer();
		StringBuffer columns =new StringBuffer();
		for(String str:colToGet){
			columns.append(str+ COMA);
		}
		columns.delete(columns.length()-COMA.length(), columns.length());
		for(Map.Entry<String, Object> entry : conditionMap.entrySet()){
			//colOprt in condition map will consistent with columnName and operators
			String colOprt = entry.getKey();
    		Object value = entry.getValue();
    		
    		if(value!=null){
    			conditionBuf.append(colOprt + SQUO + value+ SQUO);
    		}
    	}
		
		stmt1.append(SELECT + columns + FROM + tableName + WHERE);
		outputQueries[0]=stmt1;
		outputQueries[1]=conditionBuf;
		
		return outputQueries;
	}
}
	