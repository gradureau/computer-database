package springConfig;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.zaxxer.hikari.HikariDataSource;

public class ConfigInitializer {
    public static void main(String...args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(
                ServiceConfig.class);
 
        HikariDataSource ds = applicationContext.getBean(HikariDataSource.class);
        System.out.println(ds);
        applicationContext.close();
    }
}
