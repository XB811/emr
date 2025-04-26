package top.xblog1.emr.services.emr;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "top.xblog1.emr.services.emr.openfeignClient")
@MapperScan("top.xblog1.emr.services.emr.dao.mapper")
public class EmrServicesApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmrServicesApplication.class, args);
    }

}
