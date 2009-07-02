
				/*
 *  Adito
 *
 *  Copyright (C) 2003-2006 3SP LTD. All Rights Reserved
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2 of
 *  the License, or (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
			
package com.adito.util.datetime;

import java.util.Calendar;
import java.util.Date;

/**
 * Helper method for working with Java Dates.  Surely it has to be easier than this!
 * Some of this looks complicated, but I can't find an easier way to do it.
 * Weeks can start on different days so we can't hard code the first day of the week
 * to always be MONDAY.  If you change this class, be careful!
 */
public class DateGenerator {

    /**
     * @return Date
     */
    public static Date todayStart() {
        return toStart(Calendar.getInstance());
    }

    /**
     * @return Date
     */
    public static Date todayEnd() {
        return toEnd(Calendar.getInstance());
    }

    /**
     * @return Date
     */
    public static Date yesterdayStart() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_WEEK, -1);
        return toStart(calendar);
    }

    /**
     * @return Date
     */
    public static Date yesterdayEnd() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_WEEK, -1);
        return toEnd(calendar);
    }

    /**
     * @return Date
     */
    public static Date thisWeekStart() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        return toStart(calendar);
    }
    
    /**
     * @return Date
     */
    public static Date thisWeekEnd() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.WEEK_OF_MONTH, 1);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        calendar.add(Calendar.DAY_OF_WEEK, -1);
        return toEnd(calendar);
    }
    
    /**
     * @return Date
     */
    public static Date lastWeekStart() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.WEEK_OF_MONTH, -1);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        return toStart(calendar);
    }
    
    /**
     * @return Date
     */
    public static Date lastWeekEnd() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        calendar.add(Calendar.DAY_OF_WEEK, -1);
        return toEnd(calendar);
    }

    /**
     * @return Date
     */
    public static Date thisMonthStart() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return toStart(calendar);
    }
    
    /**
     * @return Date
     */
    public static Date thisMonthEnd() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        return toEnd(calendar);
    }

    /**
     * @return Date
     */
    public static Date lastMonthStart() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return toStart(calendar);
    }
    
    /**
     * @return Date
     */
    public static Date lastMonthEnd() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        return toEnd(calendar);
    }
    
    /**
     * @param from
     * @return Date
     */
    public static Date toStart(Date from) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(from);
        return toStart(calendar);
    }
    
    private static Date toStart(Calendar from) {
        from.set(Calendar.HOUR_OF_DAY, 0);
        from.set(Calendar.MINUTE, 0);
        from.set(Calendar.SECOND, 0);
        from.set(Calendar.MILLISECOND, 0);
        return from.getTime();
    }

    /**
     * @param end
     * @return Date
     */
    public static Date toEnd(Date end) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(end);
        return toEnd(calendar);
    }
    
    private static Date toEnd(Calendar end) {
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        end.set(Calendar.MILLISECOND, 0);
        return end.getTime();
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        println("Today", todayStart(), todayEnd());
        println("Yesterday", yesterdayStart(), yesterdayEnd());
        println("This Week", thisWeekStart(), thisWeekEnd());
        println("Last Week", lastWeekStart(), lastWeekEnd());
        println("This Month", thisMonthStart(), thisMonthEnd());
        println("Last Month", lastMonthStart(), lastMonthEnd());
    }
    
    private static void println(String title, Date fromDate, Date toDate) {
        System.out.println(title);
        System.out.println("Starts on '" + fromDate + "'");
        System.out.println("Ends on '" + toDate + "'\n");
    }
}
