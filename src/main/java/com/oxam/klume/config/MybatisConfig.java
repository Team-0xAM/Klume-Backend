package com.oxam.klume.config;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@MapperScan(basePackages = "com.oxam.klume", annotationClass = Mapper.class)
@Configuration
public class MybatisConfig {

}