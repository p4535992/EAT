package util.gate;

import gate.Document;
import gate.Factory;
import gate.util.DocumentProcessor;

/**
 * Created by 4535992 on 17/04/2015.
 */
public class GATEProcessor {

    @Component
    public class GATEProcessor {

        @Autowired
        private DocumentProcessor documentProcessor;

        @JmsListener(destination = "${gate.queueName}", concurrency = "${gate.numThreads}")
        public void receive(String message) {
            try {
                Document doc = Factory.newDocument(message);
                try {
                    documentProcessor.processDocument(doc);
                    // do whatever you need to do with the results
                } finally {
                    Factory.deleteResource(doc);
                }
            } catch(Exception e) {
                // handle the exception somehow
            }
        }
    }
}
