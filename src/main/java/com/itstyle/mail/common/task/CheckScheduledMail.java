package com.itstyle.mail.common.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itstyle.mail.common.model.Email;
import com.itstyle.mail.common.util.CronUtil;
import com.itstyle.mail.service.IMailService;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @ClassName CheckScheduledMail
 * @ProjectName spring-boot-mail
 * @Description TODO
 * @Author 万民
 * @Date 2018/12/20 22:14
 * @Version 1.0
 **/
@Component
public class CheckScheduledMail {
    @Autowired
    RedisTemplate<Object, Object> template;
    @Autowired
    IMailService mailService;

    @Scheduled(cron = "0/10 * * * * ?")
    public void checkMail(){
        ObjectMapper mapper = new ObjectMapper();
        Date dateNow = new Date();
        Date d;
        List<String> cronKeys = (List<String>) (Object) template.opsForList().range("cronKeys", 0, -1);
        if (cronKeys!=null && cronKeys.size()>0) {
            try {
                for(String cronKey: cronKeys){
                    d = CronUtil.toDate(cronKey);
                    if(d.before(dateNow)){
                        template.opsForList().remove("cronKeys", 0, cronKey);//从cronKeys列表中移除到期的cronKey
                        List<Object> emails = template.opsForList().range(cronKey, 0, -1);
                        if (emails!=null && emails.size()>0) {
                            for(Object email: emails){
                                /*
                                * Jackson已经满足了大部分的序列化和反序列化工作，
                                * 但是对于复杂的泛型实体估计未必能如愿的正常反序列，
                                * 而此时对于一些泛型里面的实体对象就会反序列化成LinkedHashMap类型的.解决办法：
                                * 使用自带的convertValue方法将这个LinkedHashMap以Object类型转成想要的实体
                                * */
                                Email email1 = mapper.convertValue(email, Email.class);
                                mailService.sendFreemarker(email1);
                            }
                        }
                        template.opsForList().trim(cronKey,1,0);/*删除cronKey List中所有数据。1>0，表示删除所有*/
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
