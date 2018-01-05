package com.nvp.util;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import com.ibm.icu.text.SimpleDateFormat;
import com.mwt.roles.ProductionRoleModel;

public class DateUtil {

	/**
	 * 
	 * Convert a Java Date object into the standard date format of dd/mm/yyyy
	 * @param date
	 * @return
	 */
	public static String toString(Date date){
		
		if(null != date) {
			SimpleDateFormat dt = new SimpleDateFormat("dd/mm/yyyy");
			return dt.format(date);
		} else {
			return "";
		}
	}
	
	/**
	 * 
	 * Convert a string which contains a date into a date object
	 * @throws ParseException 
	 * 
	 */
	public static Date toDate(Serializable input) throws ParseException {
		
		String strDate = input.toString();
		if(null == strDate || strDate.isEmpty()){
			return null;
		}
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		if(strDate.length() > 10) {
		  return dateFormat.parse( strDate.substring(0, 10));
	    } else {
	      return dateFormat.parse( strDate );
	    }
	}
}
