package top.xblog1.emr.services.evaluation;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("top.xblog1.emr.services.evaluation.dao.mapper")
public class EvaluationServicesApplication {

    public static void main(String[] args) {
        SpringApplication.run(EvaluationServicesApplication.class, args);
    }

}
