package springConfig;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;

import javax.servlet.ServletContext;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import com.excilys.gradureau.computer_database.service.ICrudCDB;

public class MyWebAppInitializer implements WebApplicationInitializer {
    /*
     * https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/WebApplicationInitializer.html
     * @see org.springframework.web.WebApplicationInitializer#onStartup(javax.servlet.ServletContext)
     */

    @Override
    public void onStartup(ServletContext container) {
        // Create the 'root' Spring application context
        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.register(ServiceConfig.class);
        rootContext.refresh();
        
        ICrudCDB service = rootContext.getBean(ICrudCDB.class);
        System.out.println("\nXXXXXXXXX\n\n"+service+"\n\nXXXXXXXX\n");
        System.out.println("\nXXXXXXXXX\n\n"+service.listComputers().getContent()+"\n\nXXXXXXXX\n");
        
        @SuppressWarnings("unchecked")
        Supplier<Connection> supplier = (Supplier<Connection>)rootContext.getBean("connectionSupplier");
        System.out.println("\nXXXXXXXXX\n\n"+supplier+"\n\nXXXXXXXX\n");
        Connection co = supplier.get();
        System.out.println("\nXXXXXXXXX\n\n"+co+"\n\nXXXXXXXX\n");
        try {
            co.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Manage the lifecycle of the root application context
        container.addListener(new ContextLoaderListener(rootContext));
    }

}