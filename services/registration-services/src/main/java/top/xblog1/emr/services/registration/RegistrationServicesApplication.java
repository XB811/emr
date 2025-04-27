package top.xblog1.emr.services.registration;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan("top.xblog1.emr.services.registration.dao.mapper")
@EnableFeignClients
public class RegistrationServicesApplication {

    public static void main(String[] args) {
        SpringApplication.run(RegistrationServicesApplication.class, args);
    }

}
