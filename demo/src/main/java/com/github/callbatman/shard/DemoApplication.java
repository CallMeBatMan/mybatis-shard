package com.github.callbatman.shard;

import com.github.callbatman.shard.demo.dao.User;
import com.github.callbatman.shard.demo.dao.UserMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.util.List;

@SpringBootApplication
@EnableScheduling
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Resource
    private UserMapper userMapper;

    @Scheduled(fixedDelay = 2111)
    public void dd() {
        // 模拟按用户标志分表
        List<User> allUsers = userMapper.getAllUser("99");
        System.out.println(allUsers);
    }
}
