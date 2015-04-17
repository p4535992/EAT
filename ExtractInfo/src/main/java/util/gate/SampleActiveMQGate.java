package util.gate;

/**
 * Created by Marco on 30/03/2015.
 */
public class SampleActiveMQGate {
    @SpringBootApplication
    @EnableJms
    @ImportResource("gate/gate-beans.xml")
    public class GateApp {

        public static void main(String... args) {

            SpringApplication.run(SampleActiveMQGate.class, args);
        }
    }
}
