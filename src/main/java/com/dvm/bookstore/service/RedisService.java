package com.dvm.bookstore.service;

import com.dvm.bookstore.dto.response.BookDetailResponse;
import com.dvm.bookstore.entity.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void setValue(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }
    public Object getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteValue(String key) {
        redisTemplate.delete(key);
    }

    public void setListBook(String key, List<BookDetailResponse> bookList) {
        redisTemplate.opsForValue().set(key, bookList);
    }
    public <T> List<T> getListBook(String key) {
        List<T> result= Collections.emptyList();
        try {
            result = (List<T>) redisTemplate.opsForValue().get(key);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return result;
    }
    public void deleteListBook(String key) {
        redisTemplate.opsForHash().delete("hash", key);
    }
    public <T> void set(String key, T value, Long... timeout) {
        redisTemplate.opsForValue().set(key, value);
        if (timeout.length > 0) {
            redisTemplate.expire(key, timeout[0], TimeUnit.SECONDS);
        }
        redisTemplate.opsForHash().put("hash", key, value);
    }
}
