package com.techelevator.months;

public class MonthHandler {
	
	private final Month[] months;
	
	public MonthHandler(Month[] months) {
		this.months = months;
	}
	
	public String getMonthName(int month) {
		String monthName = null;
		for (Month name: months) {
			if (month == name.getNumberOfMonth()) {
				monthName = name.getName(); 
			} else {
				monthName = "";
			}
		}
		return monthName;
	}

}
