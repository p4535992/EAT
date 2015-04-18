package util.gate;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.boot.SpringApplication;
/**
 * Created by Marco on 30/03/2015.
 */
@SpringBootApplication
@EnableJms
@ImportResource("gate/gate-beans.xml")
public class GateApp {

    public static void main(String... args) {

        SpringApplication.run(GateApp.class, args);
    }
}

