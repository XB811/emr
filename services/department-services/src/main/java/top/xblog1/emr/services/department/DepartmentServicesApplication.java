package top.xblog1.emr.services.department;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("top.xblog1.emr.services.department.dao.mapper")
public class DepartmentServicesApplication {

    public static void main(String[] args) {
        SpringApplication.run(DepartmentServicesApplication.class, args);
    }

}
