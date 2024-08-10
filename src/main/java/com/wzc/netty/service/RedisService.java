package com.wzc.netty.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

 public interface RedisService {

     boolean expire(String key, long time);

     long getTime(String key);

     boolean hasKey(String key);

     boolean persist(String key);

     Object get(String key);

     void set(String key, String value);

     void set(String key, String value, long time);

     void batchSet(Map<String, String> keyAndValue);

     void batchSetIfAbsent(Map<String, String> keyAndValue);

     Long increment(String key, long number);

     Double increment(String key, double number);

     void sSet(String key, String value);

     Set<Object> members(String key);

     void randomMembers(String key, long count);

     Object randomMember(String key);

     Object pop(String key);

     long size(String key);

     boolean sHasKey(String key, Object value);

     boolean isMember(String key, Object obj);

     boolean move(String key, String value, String destKey);

     void remove(String key, Object... values);

     Set<Set> difference(String key, String destKey);

     void add(String key, Map<String, String> map);

     Map<Object, Object> getHashEntries(String key);

     boolean hashKey(String key, String hashKey);

     Object hGet(String key, String hashKey);

     boolean hSet(String key, String hashKey, Object value, long time);

     String popValue(String key);

     Long delete(String key, String... hashKeys);

     Long increment(String key, String hashKey, long number);

     Double increment(String key, String hashKey, Double number);

     Set<Object> hashKeys(String key);

     Long hashSize(String key);

     void leftPush(String key, Object value);

     Object index(String key, long index);

     List<Object> range(String key, long start, long end);

     void leftPush(String key, String pivot, String value);

     void leftPushAll(String key, String... values);

     void leftPushAll(String key, String value);

     void rightPushAll(String key, String... values);

     void rightPushIfPresent(String key, Object value);

     long listLength(String key);

     void leftPop(String key);

     void leftPop(String key, long timeout, TimeUnit unit);

     void rightPop(String key);

     void rightPop(String key, long timeout, TimeUnit unit);
}
