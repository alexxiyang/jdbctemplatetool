package org.crazycake.jdbcTemplateTool.utils;

import java.util.ArrayList;
import java.util.List;

import org.crazycake.jdbcTemplateTool.model.SqlParamsPairs;

/**
 * Used to deal with "IN" in sql.
 * @author alexxiyang (https://github.com/alexxiyang)
 *
 */
public class InUtils {
	
	/**
	 * Change sql if found array in params
	 * @param sql
	 * @param params
	 * @return
	 */
	public static SqlParamsPairs handleIn(String sql, Object[] params){
		
		//split with question mark placeholder
		String[] sqlPieces = sql.split("\\?");
		
		//question mark placeholder list
		String[] questionPlaceholders;
		if(sql.endsWith("?")){
			questionPlaceholders = new String[sqlPieces.length];
		}else{
			questionPlaceholders = new String[sqlPieces.length-1];
		}
		for(int i=0;i<questionPlaceholders.length;i++){
			questionPlaceholders[i]="?";
		}
		
		List<Object> plist = new ArrayList<Object>();
		for(int i=0; i<params.length; i++){
			Object p = params[i];
			if(p.getClass().equals(ArrayList.class)){
				
				//change ? => (?,?,?...)
				//change list to objects
				StringBuilder sb = new StringBuilder();
				sb.append("(");
				ArrayList inParams = (ArrayList)p;
				for(int j=0;j<inParams.size();j++){
					if(j!=0){
						sb.append(",");
					}
					sb.append("?");
					//split list to objects
					plist.add(inParams.get(j));
				}
				sb.append(")");
				questionPlaceholders[i] = sb.toString();
				
			}else{
				plist.add(p);
			}
		}
		
		//join sql
		StringBuilder sqlsb = new StringBuilder();
		for(int i=0;i<sqlPieces.length;i++){
			
			sqlsb.append(sqlPieces[i]);
			if(i<questionPlaceholders.length){
				sqlsb.append(questionPlaceholders[i]);
			}
		}
		
		
		SqlParamsPairs spPairs = new SqlParamsPairs(sqlsb.toString(),plist.toArray());
		
		return spPairs;
	}
}
