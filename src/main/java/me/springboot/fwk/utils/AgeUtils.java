package me.springboot.fwk.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AgeUtils {
    public AgeUtils() {
    }

    /**
     * 计算年龄，以date为出生日期，参照referDate的时候 是几年几个月几天
     * @param date 出生日期
     * @param referDate 参数日期
     * @return
     * Map<String,Integer>
     * YEAR 年
     * MONTH 月
     * DAY 日
     */
    public static Map<String,Integer> calcAge(Date date,Date referDate){
        boolean bigger = true;

        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
//		c1.setTime(referDate);
//		c2.setTime(date);
        if(referDate.getTime()>date.getTime()){

            c1.setTime(referDate);
            c2.setTime(date);
            bigger = true;
        }else{
            c1.setTime(date);
            c2.setTime(referDate);
            bigger = false;
        }

        int year = c1.get(Calendar.YEAR)- c2.get(Calendar.YEAR);
        int month = (c1.get(Calendar.MONTH)+1) - (c2.get(Calendar.MONTH)+1);
        int day = c1.get(Calendar.DAY_OF_MONTH) - c2.get(Calendar.DAY_OF_MONTH);

        if(day<0){
            //如果日期不够减，说明没达到完整月份数，需要将月份数减1
            month = month-1;
            //如果月份数为负数（出生第二天就是另一个月开始的情况），则将月份数置为0
            if(month<0){
                month=month+12;
                year = year -1;
            }

            //获取当前月份的前一个月(月份是从0开始的）
            int preMonth = (c1.get(Calendar.MONTH)+1)-1;
            //如果当前是1月，前一个月应该是12月份
            if(preMonth<=0)
                preMonth=12;
            //1,3,5,7,8,10,12月份是31天，应该用31减去c2对应的天数再加上c1的天数
            if(preMonth==1||preMonth==3||preMonth==5||preMonth==7||preMonth==8||preMonth==10||preMonth==12){
                day = 31 - c2.get(Calendar.DAY_OF_MONTH)+c1.get(Calendar.DAY_OF_MONTH) ;
            }
            if(preMonth==4||preMonth==6||preMonth==9||preMonth==11){
                day = 30 - c2.get(Calendar.DAY_OF_MONTH)+c1.get(Calendar.DAY_OF_MONTH) ;
            }
            if(preMonth==2){
                day = 28 - c2.get(Calendar.DAY_OF_MONTH)+c1.get(Calendar.DAY_OF_MONTH) ;
                int c1Year =  c1.get(Calendar.YEAR);
                //如果是闰年还需要加一天
                if(((c1Year % 100 == 0) && (c1Year % 400 == 0))|| ((c1Year % 100 != 0) && (c1Year % 4 == 0))){
                    day = day+1;
                }
            }

        }
        //借位后可能日期计算出来还是负数，如：生日2017-08-31，当前日期2018-03-02
        if(day<0){
            //如果日期不够减，说明没达到完整月份数，需要将月份数减1
            month = month-1;
            //如果月份数为负数（出生第二天就是另一个月开始的情况），则将月份数置为0
            if(month<0){
                month=month+12;
                year = year -1;
            }

            //获取当前月份的前一个月(月份是从0开始的）
            int preMonth = (c1.get(Calendar.MONTH)+1)-2;
            //如果当前是1月，前一个月应该是12月份
            if(preMonth<=0)
                preMonth=12;
            //1,3,5,7,8,10,12月份是31天，应该用31减去c2对应的天数再加上c1的天数
            if(preMonth==1||preMonth==3||preMonth==5||preMonth==7||preMonth==8||preMonth==10||preMonth==12){
                day = 31 +day ;
            }
            if(preMonth==4||preMonth==6||preMonth==9||preMonth==11){
                day = 30 +day;
            }
            if(preMonth==2){
                day = 28 +day;
                int c1Year =  c1.get(Calendar.YEAR);
                //如果是闰年还需要加一天
                if(((c1Year % 100 == 0) && (c1Year % 400 == 0))|| ((c1Year % 100 != 0) && (c1Year % 4 == 0))){
                    day = day+1;
                }
            }

        }

        if(month<0){
            year = year-1;
            month = month+12;
        }

        Map<String,Integer> map = new HashMap<String,Integer>();
        map.put("YEAR", year>0?year:0);
        map.put("MONTH", month>0? month :0);
        map.put("DAY",day);

        if(!bigger){
            map.put("YEAR", -map.get("YEAR"));
            map.put("MONTH",-map.get("MONTH"));
            map.put("DAY",-map.get("DAY"));
        }

        return map;
    }

    public static String calcAgeAsString(Date date, Date referDate) {
        Map<String, Integer> map = calcAge(date, referDate);
        int year = ((Integer)map.get("YEAR")).intValue();
        int month = ((Integer)map.get("MONTH")).intValue();
        int day = ((Integer)map.get("DAY")).intValue();
        StringBuffer result = new StringBuffer();
        result.append(year + "岁");
        result.append(month + "个月");
        result.append(day + "天");
        return result.toString();
    }
}
