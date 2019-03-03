package com.itstyle.mail.common.redis;

import java.lang.reflect.Method;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.itstyle.mail.common.model.Email;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {
	/**
	 * 自定义key(消息队列 暂时用不到 自行忽略)
     * 此方法将会根据类名+方法名+所有参数的值生成唯一的一个key,即使@Cacheable中的value属性一样，key也会不一样。
	 * @Author  科帮网
	 * @return 
	 * @Date	2017年8月13日
	 * 更新日志
	 * 2017年8月13日  科帮网 首次创建
	 *
	 */
	@Bean
	public KeyGenerator keyGenerator() {
		return new KeyGenerator() {
			@Override
			public Object generate(Object target, Method method,
					Object... params) {
				StringBuilder sb = new StringBuilder();
				sb.append(target.getClass().getName());
				sb.append(method.getName());
				for (Object obj : params) {
					sb.append(obj.toString());
				}
				return sb.toString();
			}
		};
	}
    /**
     * 缓存管理器
     * @Author  科帮网
     * @param redisTemplate
     * @return  CacheManager
     * @Date	2017年8月13日
     * 更新日志
     * 2017年8月13日  科帮网 首次创建
     */
	@SuppressWarnings("rawtypes")
	@Bean
	public CacheManager cacheManager(RedisTemplate redisTemplate) {
		return new RedisCacheManager(redisTemplate);
	}
    /**
     * 序列化Java对象
     * @Author  科帮网
     * @param factory
     * @return  RedisTemplate<String,String>
     * @Date	2017年8月13日
     * 更新日志
     * 2017年8月13日  科帮网 首次创建
     *
     */
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
}