package p4535992.util.http;

//import org.apache.commons.httpclient.NTCredentials;
//import org.apache.http.client.protocol.HttpClientContext;
//import org.apache.http.conn.ssl.SSLConnectionSocketFactory;

//import org.apache.http.impl.client.HttpClientBuilder;

/**
 * Funziona solo con la versione 4.4 di Http Components
 * @author Marco
 */
public class SSLCertificate {
    /*
    void setCredential() {
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(AuthScope.ANY, (Credentials) new NTCredentials("Grigoriy.Polyakov","password", "", "domain.kz"));

        HttpClientContext context = HttpClientContext.create();
        context.setCredentialsProvider(credsProvider);

        HttpGet httpget = new HttpGet("https://serveer.domain.kz");

       try{
        HttpClient client = HttpClientBuilder.create()
                .setSSLSocketFactory(getFactory())
                .setDefaultCredentialsProvider(credsProvider)
                .setSslcontext(getContext())
                .build();
        System.out.println(client.execute(httpget,context).getStatusLine());
       }catch(Exception e){e.printStackTrace();}

        
    }
    */
    
    /*
    SSLContext getContext() throws NoSuchAlgorithmException, KeyStoreException, IOException, KeyManagementException, CertificateException, UnrecoverableKeyException {
        //new File("key/keystore.p12"), "1234"
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        KeyStore keyStore = KeyStore.getInstance("PKCS12");

        InputStream keyInput = new FileInputStream(new File("/kvv/keystore.p12"));
        keyStore.load(keyInput, "1234".toCharArray());
        keyInput.close();

        keyManagerFactory.init(keyStore, "1234".toCharArray());

        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            
            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws CertificateException {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) throws CertificateException {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
                }
        };

        SSLContext context = SSLContext.getInstance("TLS");
        context.init(keyManagerFactory.getKeyManagers(), trustAllCerts, new SecureRandom());

        return context;
    }

    SSLConnectionSocketFactory getFactory() throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        return new SSLConnectionSocketFactory(getContext());
    }
    */
}
