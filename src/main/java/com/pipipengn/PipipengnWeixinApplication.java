package com.pipipengn;

import tk.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.pipipengn.mapper")
public class PipipengnWeixinApplication {

    public static void main(String[] args) {
        SpringApplication.run(PipipengnWeixinApplication.class, args);
    }

}
