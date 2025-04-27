package top.xblog1.emr.services.notice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("top.xblog1.emr.services.notice.dao.mapper")
public class NoticeServicesApplication {

    public static void main(String[] args) {
        SpringApplication.run(NoticeServicesApplication.class, args);
    }

}
