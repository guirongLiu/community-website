package com.example.springblog;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication
@MapperScan("com.example.springblog.mapper")
public class SpringblogApplication {

	//private static final Logger log = LoggerFactory.getLogger(SpringblogApplication.class);
	//@Autowired
	//private UserDao userDao;

	public static void main(String[] args) {
		SpringApplication.run(SpringblogApplication.class, args);
	}


}
