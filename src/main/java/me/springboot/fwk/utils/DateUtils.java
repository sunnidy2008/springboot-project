package me.springboot.fwk.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateUtils {
	public static int DATE_TYPE_YEAR = Calendar.YEAR;
	public static int DATE_TYPE_MONTH = Calendar.MONTH;
	public static int DATE_TYPE_DAY = Calendar.DAY_OF_MONTH;
	public static int DATE_TYPE_HOUR = Calendar.HOUR;
	public static int DATE_TYPE_MINUTE = Calendar.MINUTE;
	public static String DATA_TIME_CONSTANT = "yyyy-MM-dd HH:mm:ss";
	public static String DATE_CONSTANT = "yyyy-MM-dd";
	public static String DATE_CONSTANT2 = "yyyy年MM月dd日";
	public static String DATE_CONSTANT3 = "yyyyMMdd";

	public DateUtils() {

	}
	
	//获取两个时间之间的天数差（可能为负数）
	public static int getDays(long begin,long end){
		Calendar beginCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();
        beginCalendar.setTimeInMillis(begin);
        endCalendar.setTimeInMillis(end);
        int days = 0;
        while(beginCalendar.before(endCalendar)){
            days++;
            beginCalendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        if(days==0){
            beginCalendar.setTimeInMillis(end);
            endCalendar.setTimeInMillis(begin);
            while(beginCalendar.before(endCalendar)){
                days++;
                beginCalendar.add(Calendar.DAY_OF_YEAR, 1);
            }
            days = 0-days;
        }
        return days;
	}

	/**
     * 获取当前日期是星期几<br>
     * 
     * @param dt
     * @return 当前日期是星期几
     */
    public static String getWeekOfDate(Date dt) {
    	Locale.setDefault(Locale.CHINA); 
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }
    
    public static void main(String args[]) throws Exception{
    	Date d = DateUtils.strToDate("2015-10-11");
    	String x = DateUtils.getWeekOfDate(d)+"";
    }
    
    /**
     * 获取当前日期是星期几<br>
     * 
     * @param dt
     * @return 当前日期是星期几的数值，星期一是1，星期天是7
     */
    public static int getNumWeekOfDate(Date dt) {
    	Locale.setDefault(Locale.CHINA); 
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w == 0)
            w = 7;
        return w;
    }

    public static Date strToDate(String date) throws Exception {
		String pattern = getDefautFormat(date);
		return strToDate(date, pattern);
	}

	/**
	 * ?????????????????
	 * 
	 * @return Date
	 */
	public static Date strToDate(String date, String pattern) throws Exception {

		try {
			if (pattern == null || pattern.equals("")) {
				pattern = "yyyy-MM-dd";
			} else if (pattern.equals(DATA_TIME_CONSTANT)) {

			}
			SimpleDateFormat sdf = new SimpleDateFormat(pattern.trim());
			return sdf.parse(date.trim());
		} catch (Exception ex) {
			throw new Exception("???" + pattern + "????????" + date + "???????�{");
		}
	}

	public static String dateToStr(Date date) {
		if (date == null) {
			return "";
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat(DATA_TIME_CONSTANT);
			log.debug(date.getClass().getName());
			return sdf.format(date);
		}
	}

	/**
	 * ?????????????????
	 * 
	 * @param date
	 *            Date
	 * @param pattern
	 *            String
	 * @return String
	 */
	public static String dateToStr(Date date, String pattern) {
		if (date == null)
			return "";
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}

	/**
	 * ???????????????????????
	 * 
	 * @param pattern
	 *            String
	 * @return String
	 * @throws Exception
	 */
	public static String getSystemDateStr(String pattern) throws Exception {
		try {
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			return sdf.format(date);
		} catch (Exception ex) {
			throw new Exception("???" + pattern + "??????????????????" + pattern
					+ "????????");
		}
	}

	public static Date getSystemDate() {
		Date d = new Date(System.currentTimeMillis());
		return d;
	}

	/**
	 * ?????????
	 * 
	 * @param type
	 *            int
	 * @param dt
	 *            Date
	 * @param count
	 *            int
	 * @return Date
	 */
	public static Date dateAdd(int type, Date dt, int count) {
		GregorianCalendar g = new GregorianCalendar();
		g.setTime(dt);
		g.add(type, count);

		return g.getTime();
	}

	public static String getDefautFormat(String date) throws Exception {
		// 200-06-05 21:89:89.8
		date = date.trim();
		String format = "";
		String dCode[] = new String[] { "yyyy", "-MM", "-dd" };
		String tCode[] = new String[] { " HH", ":mm", ":ss" };
		int dF = StringUtils.split(date, "-").length;
		date = StringUtils.replace(date, ":", "#");
		int tF = StringUtils.split(date, "#").length;
		int dtF = StringUtils.split(date, " ").length;
		for (int i = 0; i < dF; i++) {
			format += dCode[i];
		}
		for (int j = 0; j < tF && tF > 1; j++) {
			format += tCode[j];
		}
		if (dtF > 1 && tF < 1) {
			format += tCode[0];
		}
		if (StringUtils.split(date, ".").length > 1) {
			format += ".SSS";
		}

		if (format.equals("")) {
			throw new Exception("????????????????? ????=" + date);
		}
		return format;
	}

}
