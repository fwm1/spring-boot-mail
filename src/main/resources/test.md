### redisTemplate在项目中的使用
**项目介绍**：redis用一个名为cronKeys的List存储待发送Email的发送时间，并有若干个以cronKey为Key的Email列表存储待发送的Email。Email是一Jackson2序列化的对象格式。
**项目代码**  
* 将待发送邮件存入redis  
```
public Result scheduleMail(Email email, String dateStr) {
		/*
		* 编写cron到Date的转换工具类
		* */
		Date date;
		String cronKey;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateStr);
			cronKey = CronUtil.toCron(date);
			redisTemplate.opsForList().rightPush("cronKeys", cronKey);
			redisTemplate.opsForList().rightPush(cronKey, email);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error();
		}
		return Result.ok();
	}
```

* Spring定时任务将Email取出并发送
```
@Scheduled(cron = "0/10 * * * * ?")
    public void checkMail(){
        ObjectMapper mapper = new ObjectMapper();
        Date dateNow = new Date();
        System.out.println(CronUtil.toCron(dateNow));
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
``` 


* RedisTemplate设置  
```
  @Bean
	public RedisTemplate<Object, Object> redisTemplate(
			RedisConnectionFactory factory) {
		RedisTemplate<Object, Object> template = new RedisTemplate<Object, Object>();
		template.setConnectionFactory(factory);
		setSerializer(template); //使用Jackson序列化
		template.afterPropertiesSet();
		return template;
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void setSerializer(RedisTemplate template) {
		Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(
				Object.class);
		ObjectMapper om = new ObjectMapper();
		//om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		//om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		jackson2JsonRedisSerializer.setObjectMapper(om);
		template.setValueSerializer(jackson2JsonRedisSerializer);
	}
```