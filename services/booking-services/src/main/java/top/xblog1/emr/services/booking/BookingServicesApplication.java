package top.xblog1.emr.services.booking;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "top.xblog1.emr.services.booking.openfeignClient")
@MapperScan("top.xblog1.emr.services.booking.dao.mapper")
public class BookingServicesApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookingServicesApplication.class, args);
    }

}
