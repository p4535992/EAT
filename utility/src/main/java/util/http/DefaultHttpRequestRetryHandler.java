
package util.http;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLHandshakeException;

import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.ExecutionContext;

/**
 * The default {@link HttpRequestRetryHandler} used by request executors.
 */
public class DefaultHttpRequestRetryHandler implements HttpRequestRetryHandler {

    /** the number of times a method will be retried */
    private final int retryCount;
    
    /** Whether or not methods that have successfully sent their request will be retried */
    private final boolean requestSentRetryEnabled;
    
    /**
     * Default constructor
     */
    public DefaultHttpRequestRetryHandler(int retryCount, boolean requestSentRetryEnabled) {
        super();
        this.retryCount = retryCount;
        this.requestSentRetryEnabled = requestSentRetryEnabled;
    }
    
    /**
     * Default constructor
     */
    public DefaultHttpRequestRetryHandler() {
        this(3, false);
    }
    /** 
     * Used <code>retryCount</code> and <code>requestSentRetryEnabled</code> to determine
     * if the given method should be retried.
     */
    public boolean retryRequest(
            final IOException exception, 
            int executionCount,
            final HttpContext context) {
        if (exception == null) {
            throw new IllegalArgumentException("Exception parameter may not be null");
        }
        if (context == null) {
            throw new IllegalArgumentException("HTTP context may not be null");
        }
        if (executionCount > this.retryCount) {
            // Do not retry if over max retry count
            return false;
        }
        if (exception instanceof NoHttpResponseException) {
            // Retry if the server dropped connection on us
            return true;
        }
        if (exception instanceof InterruptedIOException) {
            // Timeout
            return false;
        }
        if (exception instanceof UnknownHostException) {
            // Unknown host
            return false;
        }
        if (exception instanceof SSLHandshakeException) {
            // SSL handshake exception
            return false;
        }
        Boolean b = (Boolean)
            context.getAttribute(ExecutionContext.HTTP_REQ_SENT);
        boolean sent = (b != null && b.booleanValue());
        if (!sent || this.requestSentRetryEnabled) {
            // Retry if the request has not been sent fully or
            // if it's OK to retry methods that have been sent
            return true;
        }
        // otherwise do not retry
        return false;
    }
    
    /**
     * @return <code>true</code> if this handler will retry methods that have 
     * successfully sent their request, <code>false</code> otherwise
     */
    public boolean isRequestSentRetryEnabled() {
        return requestSentRetryEnabled;
    }

    /**
     * @return the maximum number of times a method will be retried
     */
    public int getRetryCount() {
        return retryCount;
    }
    
    /*
    static HttpMethodRetryHandler myretryhandler = new HttpMethodRetryHandler() {
            public boolean retryMethod(
                final HttpMethod method, 
                final IOException exception, 
                int executionCount) {
                if (executionCount >= 5) {
                    // Do not retry if over max retry count
                    return false;
                }
                if (exception instanceof NoHttpResponseException) {
                    // Retry if the server dropped connection on us
                    return true;
                }
                if (!method.isRequestSent()) {
                    // Retry if the request has not been sent fully or
                    // if it's OK to retry methods that have been sent
                    return true;
                }
                // otherwise do not retry
                return false;
            }
        };
    */
}
