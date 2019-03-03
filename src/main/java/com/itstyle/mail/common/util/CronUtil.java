package com.itstyle.mail.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ClassName CronUtil
 * @ProjectName spring-boot-mail
 * @Description TODO
 * @Author 万民
 * @Date 2018/12/20 22:43
 * @Version 1.0
 **/
public class CronUtil {
    private static final String FORMAT = "ss mm HH dd MM ? yyyy";
    public static String toCron(Date date){
        if(date == null)
            return null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT);
        return dateFormat.format(date);
    }
    public static Date toDate(String cron)throws ParseException{
        if(cron == null || cron.length()==0)
            return  null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT);
        return dateFormat.parse(cron);
    }
}
