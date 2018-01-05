package com.nvp.util;

import java.util.Calendar;
import java.util.Date;

public class PaymentValueUtil {

	public static float getTotalPaymentDayRate(Date startDate, Date endDate, float dayRate, int workingDaysPerFortnight) {
        
		if( null == startDate ) {
			return 0;
		}
		
		if( null == endDate ) {
			return 0;
		}
		
		Integer workingDays = getWorkingDays(startDate, endDate);
		return workingDays * dayRate;
	}
	
	private static int getWorkingDays(Date startDate, Date endDate) {
		
		Calendar startCal = Calendar.getInstance();
	    startCal.setTime(startDate);        

	    Calendar endCal = Calendar.getInstance();
	    endCal.setTime(endDate);

	    int workDays = 0;

	    //Return 0 if start and end are the same
	    if (startCal.getTimeInMillis() == endCal.getTimeInMillis()) {
	        return 0;
	    }

	    if (startCal.getTimeInMillis() > endCal.getTimeInMillis()) {
	        startCal.setTime(endDate);
	        endCal.setTime(startDate);
	    }

	    do {
	       //excluding start date
	        startCal.add(Calendar.DAY_OF_MONTH, 1);
	        if (startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
	            ++workDays;
	        }
	    } while (startCal.getTimeInMillis() < endCal.getTimeInMillis()); //excluding end date

	    return workDays;
	}
}
