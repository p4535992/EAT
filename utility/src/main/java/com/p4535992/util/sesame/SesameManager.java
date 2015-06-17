package com.p4535992.util.sesame;
import java.io.File;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.StringReader;
import java.io.InputStream;
import java.io.Reader;

import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;
import org.openrdf.http.client.SesameClientImpl;
import org.openrdf.model.Graph;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
import org.openrdf.repository.manager.LocalRepositoryManager;
import org.openrdf.repository.manager.RemoteRepositoryManager;
import org.openrdf.repository.manager.RepositoryManager;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.StatementCollector;
import org.openrdf.model.Resource;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.DelegatingRepository;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.DelegatingRepositoryImplConfig;
import org.openrdf.repository.config.RepositoryConfig;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.config.RepositoryConfigSchema;
import org.openrdf.repository.config.RepositoryConfigUtil;
import org.openrdf.repository.config.RepositoryFactory;
import org.openrdf.repository.config.RepositoryImplConfig;
import org.openrdf.repository.config.RepositoryRegistry;
import org.openrdf.sail.memory.model.MemValueFactory;
import java.util.Locale;
import org.openrdf.http.client.SesameClient;
import org.openrdf.model.BNode;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.BNodeImpl;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.Update;
/**
 * Created by 4535992 on 11/06/2015.
 * @author Johann Petrak
 * href https://gate.ac.uk/releases/gate-8.0-build4825-ALL/plugins/Ontology/src/gate/creole/ontology/impl/sesame/SesameManager.java
 */

public class SesameManager {
    private RepositoryConnection mRepositoryConnection;
    private Repository           mRepository;
    private RepositoryManager    mRepositoryManager;
    private String               mRepositoryLocation;
    private String               mRepositoryName;
    private static final Pattern TOKEN_PATTERN =
            Pattern.compile("\\{%[\\p{Print}&&[^\\}]]+%\\}");

    private boolean debug = true;

    private class SesameManagerException extends RuntimeException {
        private static final long serialVersionUID = 2L;
        public SesameManagerException() {}

        public SesameManagerException(String message) {
            super(message);
        }

        public SesameManagerException(String message, Throwable cause) {
            super(message, cause);
        }

        public SesameManagerException(Throwable e) {
            super(e);
        }
        /**
         * Overridden so we can print the enclosed exception's stacktrace too.
         */
        public void printStackTrace() {
            printStackTrace(System.err);
        }

        /**
         * Overridden so we can print the enclosed exception's stacktrace too.
         */
        public void printStackTrace(java.io.PrintStream s) {
            s.flush();
            super.printStackTrace(s);
            Throwable cause = getCause();
            if (cause != null) {
                s.print("Caused by:\n");
                cause.printStackTrace(s);
            }
        }

        /**
         * Overridden so we can print the enclosed exception's stacktrace too.
         */
        public void printStackTrace(java.io.PrintWriter s) {
            s.flush();
            super.printStackTrace(s);
            Throwable cause = getCause();
            if (cause != null) {
                s.print("Caused by:\n");
                cause.printStackTrace(s);
            }
        }
    }

    private Logger logger;

    public SesameManager() {
        logger = Logger.getLogger(this.getClass().getName());
    }



    boolean isManagedRepository = false;

    /**
     * Connect to a managed repository located at the given location
     * and connect to the repository with the given name.
     * The repository connection is assumed to be remote if it starts with
     * http:// or https://, otherwise the location is assumed to be a local
     * directory name.
     *
     * @param repositoryLocation repositorylocation.
     * @param repositoryName repositoryName.
     */
    public void connectToRepository(String repositoryLocation, String repositoryName) {
        // connect to location and get the manager
        closeRepository();
        connectToLocation(repositoryLocation);
        openRepository(repositoryName);
    }
    public void connectToRepository(java.net.URL repositoryLocation, String repositoryName) {
        // connect to location and get the manager
        closeRepository();
        connectToLocation(repositoryLocation);
        openRepository(repositoryName);
    }

    /**
     * Connect to a managed repository location.
     * The repository connection is assumed to be remote if it starts with
     * http:// or https://, otherwise the location is assumed to be a local
     * directory name.
     *
     * @param repositoryLocation repositoryLocation.
     */
    public void connectToLocation(String repositoryLocation) {
        // if the location starts with http:// it will be assumed that this
        // is a remote location, otherwise it will be regarded as a directory
        // name.
        logger.debug("Calling SesameManager.connectToLocation with String: "+repositoryLocation);
        if(repositoryLocation.startsWith("http://") ||
                repositoryLocation.startsWith("https://")) {
            connectToRemoteLocation(repositoryLocation);
        } else {
            connectToLocalLocation(repositoryLocation,true);
        }
    }
    public void connectToLocation(java.net.URL repositoryLocation) {
        // if the location starts with http:// it will be assumed that this
        // is a remote location, otherwise it will be regarded as a directory
        // name.
        logger.debug("Calling SesameManager.connectToLocation with URL: "+repositoryLocation);
        logger.debug("Protocol is: "+repositoryLocation.getProtocol());
        if(repositoryLocation.getProtocol().startsWith("http")) {
            connectToRemoteLocation(repositoryLocation.toString());
        } else {
            connectToLocalLocation(repositoryLocation,true);
        }
    }

    /**
     * Connect to a remote managed repository location.
     *
     * @param url url.
     */
    public void connectToRemoteLocation(String url) {
        isManagedRepository = true;
        SesameClient sesameClient = new SesameClientImpl();
        sesameClient.createSparqlSession(url,url);
//        HTTPClient httpClient = new HTTPClient();
//        httpClient.setQueryURL(url);
//        httpClient.setUpdateURL(url);
        RemoteRepositoryManager mgr = new RemoteRepositoryManager(url);
        try {
            java.net.URL javaurl = new java.net.URL(url);
            String userpass = javaurl.getUserInfo();
            if(userpass != null) {
                String[] userpassfields = userpass.split(":");
                if(userpassfields.length != 2) {
                    throw new SesameManagerException("URL has login data but not username and password");
                } else {
                    mgr.setUsernameAndPassword(userpassfields[0], userpassfields[1]);
                }
            }
        } catch(Exception ex) {
            throw new SesameManagerException("Problem processing remote URL: "+ex);
        }
        setManager(mgr, url);

    }

    /**
     * Connect to a local repository location at the given directory.
     * If mustexist is true, it is an error if the directory is not found.
     *
     * @param dirname dirname.
     * @param mustexist mustexists.
     */
    public void connectToLocalLocation(String dirname, boolean mustexist) {
        isManagedRepository = true;
        //dirname = dirname.replaceAll("/$", "");
        File dir = new File(dirname);
        if (!dir.exists()) {
            throw new SesameManagerException("Specified path does not exist: " +
                    dir.getAbsolutePath());
        }
        if (!dir.isDirectory()) {
            throw new SesameManagerException("Specified path is not a directory: " +
                    dir.getAbsolutePath());
        }
        setManager(new LocalRepositoryManager(dir), dir.toString());

    }
    public void connectToLocalLocation(java.net.URL dirname, boolean mustexist) {
        isManagedRepository = true;
        logger.debug("Called connectToLocalLocation "+dirname+"/"+mustexist);
        File dir;
        try {
            dir = new File(dirname.toURI());
        } catch (URISyntaxException ex) {
            throw new SesameManagerException("Specified URL is invalid: "+dirname,ex);
        }
        if (!dir.exists()) {
            throw new SesameManagerException("Specified path does not exist: " +
                    dir.getAbsolutePath());
        }
        if (!dir.isDirectory()) {
            throw new SesameManagerException("Specified path is not a directory: " +
                    dir.getAbsolutePath());
        }
        setManager(new LocalRepositoryManager(dir), dir.toString());

    }

    /**
     * Disconnect from a local or remote repository manager.
     *
     */
    public void disconnect() {
        closeRepository();
        if (mRepositoryManager != null) {
            logger.debug("Shutting down the repository manager");
            mRepositoryManager.shutDown();
            logger.debug("manager is shut down");
            mRepositoryManager = null;
            mRepositoryLocation = null;
            logger.debug("manager and location set to null");
        }
    }

    private void setManager(RepositoryManager manager, String location) {
        logger.debug("setManager called");
        try {
            disconnect();
            manager.initialize();
            mRepositoryManager = manager;
            mRepositoryLocation = location;
        } catch (RepositoryException e) {
            throw new SesameManagerException("Error initializing manager: "+e);
        }
    }

    /**
     * Open a repository with the given name at the remote or local location
     * previously connected to.
     * An error is raised if no local or remote location was set prior to
     * calling this method.
     *
     * @param name name.
     */
    public void openRepository(String name) {
        logger.debug("Called openRespository with ID "+name);
        if(mRepositoryManager != null) {
            try {
                mRepository = mRepositoryManager.getRepository(name);
            } catch (Exception e) {
                throw new SesameManagerException("Could not get repository "+name+" error is "+e);
            }
            if(mRepository == null) {
                throw new SesameManagerException("Getting repository failed - no repository of this name found: "+name);
            }
            try {
                mRepositoryConnection = mRepository.getConnection();
                logger.debug("repository connection set");
            } catch (Exception e) {
                throw new SesameManagerException("Could not get connection "+name+" error is "+e);
            }
        } else {
            throw new SesameManagerException("Not connected to a repository location for openRepository "+name);
        }
    }

    /**
     * Close the currently opened repository. This works for managed and
     * unmanaged repositories.
     */
    public void closeRepository() {
        if (mRepositoryConnection != null) {
            try {
                logger.debug("Commiting the connection");
                //mRepositoryConnection.commit();
                logger.debug("Closing the connection");
                mRepositoryConnection.close();
                logger.debug("Connection closed");
                // the following is NOT needed as the manager shutDown method
                // shuts down all repositories
                // mRepository.shutDown();
                logger.debug("Repository shut down");
            } catch (RepositoryException e) {
                logger.debug("!!!!! Error: ",e);
                // TODO: do not throw exception, might still need to disconnect
                // manager!
                // throw new SesameManagerException("Could not close Repository: "+e);
            }
            mRepositoryConnection = null;
            mRepository = null;
            mRepositoryName = null;
            logger.debug("connection, repository and name set to null");
        }
    }

    // create repository from a template, no substitution of variables
    // also opens the newly created repository
    /**
     * Create a new managed repository at the current remote or local location
     * using the configuration information passed on as a string.
     *
     * @param config config.
     */
    @SuppressWarnings("deprecation")
    public void createRepository(String config) {
        logger.debug("createRepository called");
        if(mRepositoryManager == null) {
            throw new SesameManagerException("No connect prior to createRepository");
        }
        Repository systemRepo = mRepositoryManager.getSystemRepository();

        ValueFactory vf = systemRepo.getValueFactory();
        Graph graph = new org.openrdf.model.impl.GraphImpl(vf);
        RDFParser rdfParser = Rio.createParser(RDFFormat.TURTLE, vf);
        rdfParser.setRDFHandler(new StatementCollector(graph));
        try {
            rdfParser.parse(new StringReader(config), RepositoryConfigSchema.NAMESPACE);
        } catch (Exception e) {
            throw new SesameManagerException("Error parsing the config string: ",e);
        }
        try {
            Resource repositoryNode =
                    org.openrdf.model.util.GraphUtil.getUniqueSubject(graph, RDF.TYPE,RepositoryConfigSchema.REPOSITORY);
            RepositoryConfig repConfig = RepositoryConfig.create(graph, repositoryNode);
            repConfig.validate();
            if (RepositoryConfigUtil.hasRepositoryConfig(systemRepo, repConfig.getID())) {
                throw new SesameManagerException("Repository already exists with ID "+
                        repConfig.getID());
            } else {
                RepositoryConfigUtil.updateRepositoryConfigs(systemRepo, repConfig);
                mRepository = mRepositoryManager.getRepository(repConfig.getID());
                // Sesame complains about the repository already being initialized
                // for native but not for OWLIM here ... can we always not initialize
                // here????
                try {
                    mRepository.initialize();
                } catch (IllegalStateException ex) {
                    System.err.println("Got an IllegalStateException, ignored: "+ex);
                    // we get this if the SAIL has already been initialized, just
                    // ignore and be happy that we can be sure that indeed it has
                }
                openRepository(repConfig.getID());
            }
        } catch (Exception e) {
            throw new SesameManagerException("Error creating repository",e);
        }
    }

    /**
     * Create an unmanaged repository with files stored in the directory
     * given from the configuration passed as a string.
     * @param repositoryDirFile repositoryDirFile.
     * @param configstring  configstring.
     */
    @SuppressWarnings("deprecation")
    public void createUnmanagedRepository(File repositoryDirFile, String configstring) {
        isManagedRepository = false;
        logger.debug("SesameManager: creating unmanaged repo, dir is "+repositoryDirFile.getAbsolutePath());
        ValueFactory vf = new MemValueFactory();
        Graph graph = parseRdf(configstring, vf, RDFFormat.TURTLE);
        Resource repositoryNode;
        try {
            repositoryNode = org.openrdf.model.util.GraphUtil.getUniqueSubject(graph, RDF.TYPE, RepositoryConfigSchema.REPOSITORY);
        } catch (org.openrdf.model.util.GraphUtilException ex) {
            throw new SesameManagerException("Could not get subject of config RDF",ex);
        }
        RepositoryConfig repConfig;
        try {
            repConfig = RepositoryConfig.create(graph, repositoryNode);
        } catch (RepositoryConfigException ex) {
            throw new SesameManagerException("Could not create repository from RDF graph",ex);
        }
        try {
            repConfig.validate();
        } catch (RepositoryConfigException ex) {
            throw new SesameManagerException("Could not validate repository",ex);
        }
        RepositoryImplConfig rpc = repConfig.getRepositoryImplConfig();
        Repository repo = createRepositoryStack(rpc);
        repo.setDataDir(repositoryDirFile);
        try {
            repo.initialize();
        } catch (RepositoryException ex) {
            throw new SesameManagerException("Could not initialize repository",ex);
        }
        try {
            mRepositoryConnection = repo.getConnection();
            logger.debug("Repo dir is "+repo.getDataDir().getAbsolutePath());
            logger.debug("Repo is writable "+repo.isWritable());
        } catch (RepositoryException ex) {
            throw new SesameManagerException("Could not get connection for unmanaged repository",ex);
        }
    }

    @SuppressWarnings("deprecation")
    private Graph parseRdf(String config, ValueFactory vf, RDFFormat lang) {
        Graph graph = new org.openrdf.model.impl.GraphImpl(vf);
        RDFParser rdfParser = Rio.createParser(lang, vf);
        rdfParser.setRDFHandler(new StatementCollector(graph));
        try {
            rdfParser.parse(new StringReader(config), RepositoryConfigSchema.NAMESPACE);
        } catch (Exception e) {
            throw new SesameManagerException("Could not parse rdf: " + e);
        }
        return graph;
    }

    @SuppressWarnings("deprecation")
    private RepositoryConfig getConfig(String config) {
        Repository myRepository = new SailRepository(new MemoryStore());
        RepositoryConfig repConfig;
        try {
            myRepository.initialize();
        } catch (RepositoryException e) {
            throw new SesameManagerException("Error initializing memory store: "+e);
        }
        ValueFactory vf = myRepository.getValueFactory();
        Graph graph = new org.openrdf.model.impl.GraphImpl(vf);
        RDFParser rdfParser = Rio.createParser(RDFFormat.TURTLE, vf);
        rdfParser.setRDFHandler(new StatementCollector(graph));
        try {
            rdfParser.parse(new StringReader(config), RepositoryConfigSchema.NAMESPACE);
            Resource repositoryNode =
                    org.openrdf.model.util.GraphUtil.getUniqueSubject(graph, RDF.TYPE,RepositoryConfigSchema.REPOSITORY);
            repConfig = RepositoryConfig.create(graph, repositoryNode);
            repConfig.validate();

        } catch (Exception e) {
            throw new SesameManagerException("Error parsing the config string "+e);
        }
        return repConfig;
    }


    private Repository createRepositoryStack(RepositoryImplConfig config) {
        RepositoryFactory factory = RepositoryRegistry.getInstance().get(config.getType());
        if (factory == null) {
            throw new SesameManagerException("Unsupported repository type: " + config.getType());
        }

        Repository repository;
        try {
            repository = factory.getRepository(config);
        } catch (RepositoryConfigException ex) {
            throw new SesameManagerException("Could not get repository from factory",ex);
        }

        if (config instanceof DelegatingRepositoryImplConfig) {
            RepositoryImplConfig delegateConfig = ((DelegatingRepositoryImplConfig)config).getDelegate();

            Repository delegate = createRepositoryStack(delegateConfig);

            try {
                ((DelegatingRepository)repository).setDelegate(delegate);
            }
            catch (ClassCastException e) {
                throw new SesameManagerException(
                        "Delegate specified for repository that is not a DelegatingRepository: "
                                + delegate.getClass());
            }
        }

        return repository;
    }

    /**
     * Substitute variables in a configuration template string.
     *
     * @param configtemplate configtemplate.
     * @param variables variables.
     * @return string of congi template.
     */
    public static String substituteConfigTemplate(String configtemplate, Map<String,String> variables) {
        // replace all variables in the template then do the actual createRepository
        StringBuffer result = new StringBuffer(configtemplate.length()*2);
        Matcher matcher = TOKEN_PATTERN.matcher(configtemplate);
        while (matcher.find()) {
            String group = matcher.group();
            // get the variable name and default
            String[] tokensArray = group.substring(2, group.length() - 2).split("\\|");

            String var = tokensArray[0].trim();

            String value = variables.get(var);
            if(value == null) {
                // try to get the default
                if(tokensArray.length > 1) {
                    value = tokensArray[1].trim();
                } else {
                    value = "";
                }
            }
            matcher.appendReplacement(result, value);
        }

        matcher.appendTail(result);

        return result.toString();
    }

    /**
     * Delete the managed repository with that name.
     *
     * @param name name.
     */
    public void deleteRepository(String name) {
        if(mRepositoryManager != null) {
            closeRepository();
            try {
                boolean done = mRepositoryManager.removeRepository(name);
            } catch (RepositoryException e) {
                throw new SesameManagerException("Could not delete repository "+name+": "+e);
            } catch (RepositoryConfigException e) {
                throw new SesameManagerException("Could not delete repository "+name+": "+e);
            }
        } else {
            throw new SesameManagerException("Must be connected to a location");
        }
    }

    /**
     * Clear the current repository and remove all data from it.
     *
     */
    public void clearRepository() {
        try {
            mRepositoryConnection.clear();
        } catch (RepositoryException e) {
            throw new SesameManagerException("Could not clear repository: "+e);
        }
    }


    /**
     * Load data into the current repository from a file.
     *
     * @param from from.
     * @param baseURI baseURI.
     * @param format format.
     */
    @SuppressWarnings("deprecation")
    public void importIntoRepository(File from, String baseURI, String format) {
        if(mRepositoryConnection != null) {
            RDFFormat sesameFormat = RDFFormat.valueOf(format);
            if(sesameFormat==null) {
                throw new SesameManagerException(
                        "Could not import - format not supported: "+format);
            }
            try {
                mRepositoryConnection.add(from,baseURI,sesameFormat);
            } catch(Exception e) {
                throw new SesameManagerException("Could not import",e);
            }
        } else {
            throw new SesameManagerException("Cannot import, no connection");
        }
    }

    /**
     * Load data into the current repository from a stream.
     *
     * @param from from.
     * @param baseURI baseURI.
     * @param format format.
     */
    @SuppressWarnings("deprecation")
    public void importIntoRepository(InputStream from, String baseURI, String format) {
        if(mRepositoryConnection != null) {
            RDFFormat sesameFormat = RDFFormat.valueOf(format);
            try {
                mRepositoryConnection.add(from,baseURI,sesameFormat);
            } catch(Exception e) {
                throw new SesameManagerException("Could not import: "+e);
            }
        } else {
            throw new SesameManagerException("Cannot import, no connection");
        }
    }

    /**
     * Load data into the current repository from a reader
     *
     * @param from from.
     * @param baseURI baseURI.
     * @param format format.
     */
    @SuppressWarnings("deprecation")
    public void importIntoRepository(Reader from, String baseURI, String format) {
        if(mRepositoryConnection != null) {
            RDFFormat sesameFormat = RDFFormat.valueOf(format);
            try {
                mRepositoryConnection.add(from,baseURI,sesameFormat);
            } catch(Exception e) {
                throw new SesameManagerException("Could not import: "+e);
            }
        } else {
            throw new SesameManagerException("Cannot import, no connection");
        }
    }

    
     //Create a query object for the current repository.
     
//    public OntologyTupleQuery createQuery(String query) {
//        if(mRepositoryConnection != null) {
//            return new UtilTupleQueryIterator(
//                    this,
//                    query,
//                    OConstants.QueryLanguage.SPARQL);
//        } else {
//            throw new SesameManagerException("Cannot create a query, no connection");
//        }
//    }

    public BooleanQuery createAskQuery(String query) {
        if(mRepositoryConnection != null) {
            try {
                return mRepositoryConnection.prepareBooleanQuery(QueryLanguage.SPARQL, query);
            } catch (Exception ex) {
                throw new SesameManagerException("Could not prepare BooleanQuery",ex);
            }
        } else {
            throw new SesameManagerException("Could not create an ask query, no connection");
        }
    }

    public Update createUpdate(String query) {
        if(mRepositoryConnection != null) {
            try {
                return mRepositoryConnection.prepareUpdate(QueryLanguage.SPARQL, query);
            } catch (Exception ex) {
                throw new SesameManagerException("Could not prepare an Update operation",ex);
            }
        } else {
            throw new SesameManagerException("Cannot create an update operation, no connection");
        }
    }

    public Set<String> getRepositories() {
        if(mRepositoryManager == null) {
            return new HashSet<String>();
        }
        try {
            return mRepositoryManager.getRepositoryIDs();
        } catch (RepositoryException ex) {
            throw new SesameManagerException("Could not get repository IDs: ",ex);
        }
    }

    /**
     * Obtain the repository connection object.
     *
     * @return RepositoryConnection
     */
    public RepositoryConnection getRepositoryConnection() {
        return mRepositoryConnection;
    }
}