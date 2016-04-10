package com.github.p4535992.extractor.estrattori.silk;

import com.github.p4535992.util.file.FileUtilities;
import de.fuberlin.wiwiss.silk.config.Blocking;
import de.fuberlin.wiwiss.silk.config.LinkingConfig;
import de.fuberlin.wiwiss.silk.config.Prefixes;
import de.fuberlin.wiwiss.silk.config.RuntimeConfig;

import de.fuberlin.wiwiss.silk.entity.*;
import de.fuberlin.wiwiss.silk.execution.ExecutionMethod;
import de.fuberlin.wiwiss.silk.execution.methods.*;
import de.fuberlin.wiwiss.silk.linkagerule.LinkageRule;
import de.fuberlin.wiwiss.silk.linkagerule.input.*;
import de.fuberlin.wiwiss.silk.linkagerule.similarity.*;
import de.fuberlin.wiwiss.silk.plugins.aggegrator.*;
import de.fuberlin.wiwiss.silk.plugins.distance.asian.CJKReadingDistance;
import de.fuberlin.wiwiss.silk.plugins.distance.tokenbased.CosineDistanceMetric;
import de.fuberlin.wiwiss.silk.plugins.transformer.normalize.AlphaReduceTransformer;
import de.fuberlin.wiwiss.silk.runtime.resource.ClasspathResource;
import de.fuberlin.wiwiss.silk.runtime.resource.FileResource;
import de.fuberlin.wiwiss.silk.runtime.resource.Resource;
import de.fuberlin.wiwiss.silk.util.DPair;
import de.fuberlin.wiwiss.silk.util.Identifier;
import de.fuberlin.wiwiss.silk.util.Uri;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import de.fuberlin.wiwiss.silk.datasource.DataSource;
import de.fuberlin.wiwiss.silk.datasource.Source;
import de.fuberlin.wiwiss.silk.plugins.datasource.CacheDataSource;
import de.fuberlin.wiwiss.silk.plugins.datasource.CsvDataSource;
import de.fuberlin.wiwiss.silk.plugins.datasource.SparqlDataSource;
import de.fuberlin.wiwiss.silk.output.DataWriter;
import de.fuberlin.wiwiss.silk.output.Output;
import de.fuberlin.wiwiss.silk.plugins.jena.FileDataSource;
import de.fuberlin.wiwiss.silk.plugins.jena.LinkedDataSource;
import de.fuberlin.wiwiss.silk.plugins.jena.RdfDataSource;
import de.fuberlin.wiwiss.silk.plugins.writer.FileWriter;
import de.fuberlin.wiwiss.silk.plugins.writer.MemoryWriter;
import de.fuberlin.wiwiss.silk.plugins.writer.SparqlWriter;

/**
 * Created by 4535992 on 25/01/2016.
 *
 *  de.fuberlin.wiwiss.silk.Silk.executeFile(
 *      new File(SILK_SLS_FILE), SILK_INTERLINK_ID, SILK_NUM_THREADS, SILK_LOGQUERIES);
 * @author 4535992.
 */
public class SilkUtilities {

    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SilkUtilities.class);

    private SilkUtilities() {
    }

    private static SilkUtilities instance = null;

    public static SilkUtilities getInstance() {
        if (instance == null) {
            instance = new SilkUtilities();
        }
        return instance;
    }

    public static SilkUtilities getNewInstance() {
        instance = new SilkUtilities();
        return instance;
    }

    public void generateRDF(File silkXMLConfiguration, String id_linking_rule, int numberOfThreads, boolean enableLog)
            throws IOException {
        logger.info("Start the Linking with SILK on the File configuration SLS "+silkXMLConfiguration.getAbsolutePath()+"...");
        try {
            if (FileUtilities.isFileExists(silkXMLConfiguration.getAbsolutePath())) {
                //file,id del link, num thread, log
                //de.fuberlin.wiwiss.silk
                //de.fuberlin.wiwiss.silk.Silk.executeFile(silkXMLConfiguration, "interlink_location", 2, true);
                de.fuberlin.wiwiss.silk.Silk.executeFile(silkXMLConfiguration, id_linking_rule, numberOfThreads, enableLog);
                logger.info("... finished with success the Linking with SILK on the File configuration SLS " + silkXMLConfiguration.getAbsolutePath());
            } else {
                throw new IOException("File configuration SLS not found on " + silkXMLConfiguration.getAbsolutePath());
            }
        }catch(Exception e){
            logger.error(e.getMessage(),e);
        }
    }

    public void generateRDF(LinkingConfig silkXMLConfiguration, String interlink_id, int numberOfThreads, boolean enableLog){
        logger.info("Start the Linking with SILK on the File configuration SLS...");
        try {
            //file,id del link, num thread, log
            //de.fuberlin.wiwiss.silk.Silk.executeFile(silkXMLConfiguration, "interlink_location", 2, true);
            de.fuberlin.wiwiss.silk.Silk.executeConfig(silkXMLConfiguration, interlink_id, numberOfThreads, enableLog);
            logger.info("... finished with success the Linking with SILK on the File configuration SLS");
        }catch(Exception e){
            logger.error(e.getMessage(),e);
        }
    }

    //@@ TODO Complete the use of SILK with java api when you have time.
    /*public de.fuberlin.wiwiss.silk.config.LinkingConfig createLinkingConfig(
            Map<String, String> prefixesSparql, File fileSourceInput, String id,
            DataSource dataSourceSilk, DataSource dataTargetSilk) {

        File homeDir = FileUtilities.getDirectoryFile(fileSourceInput);
        Prefixes prefixes = doPrefixes(prefixesSparql);
        List<PathOperator> operators = new ArrayList<>();
        operators.add(doPathOperatorBackward("uri_reference"));
        de.fuberlin.wiwiss.silk.entity.Path sourceKey = doPath("variable1", operators);
        de.fuberlin.wiwiss.silk.entity.Path targetKey = doPath("variable2", operators);

        List<SimpleTransformer> simpleTransformerList = new ArrayList<>();
        simpleTransformerList.add(doSimpleTransformerAlphaReduce());

        ExecutionMethod executionMethod = doExecutionMethodBlocking(sourceKey, targetKey, 2, simpleTransformerList);
        Blocking blocking = doBlocking(true, 2);

        RuntimeConfig runtimeConfig = doRuntimeConfig(
                executionMethod, blocking, true, true, true, 2048, 2, false, homeDir.getAbsolutePath(), java.util.logging.Level.ALL);

        Source source = doSource("id", dataSourceSilk);
        List<Source> s = new ArrayList<>();
        s.add(source);

        Traversable<Source> sourceTraversable = convertToScalaTraversable(s);

        //sourceTraversable[0] = source;
        LinkSpecification linkSpecification = null;
        Output output = doOutput(doIdentifier("id"),doMemoryWriter(),0.7,0.9);

        LinkingConfig linkingConfig = new LinkingConfig(prefixes, runtimeConfig, sourceTraversable,
                convertToScalaTraversable(linkSpecification),convertToScalaTraversable(output)
                );
        return null;
    }*/

    public ExecutionMethod doExecutionMethodBlocking(Path sourceKey, Path targetKey, int q, List<SimpleTransformer> simpleTransformerList) {
        return doExecutionMethod(ExecutionMethodType.BLOCKING, sourceKey, targetKey, q, simpleTransformerList);
    }

    public ExecutionMethod doExecutionMethodCompositeBlocking(Path sourceKey, Path targetKey) {
        return doExecutionMethod(ExecutionMethodType.COMPOSITEBLOCKING, sourceKey, targetKey);
    }

    public ExecutionMethod doExecutionMethodFull() {
        return doExecutionMethod(ExecutionMethodType.FULL);
    }

    public ExecutionMethod doExecutionMethodMultiBlock() {
        return doExecutionMethod(ExecutionMethodType.MULTIBLOCK);
    }

    public ExecutionMethod doExecutionMethodMultiPassBlocking(List<Path> blockingKeys) {
        return doExecutionMethod(ExecutionMethodType.MULTIPASSBLOCKING,blockingKeys);
    }

    public ExecutionMethod doExecutionMethodQGrams(Path sourceKey, Path targetKey, int q, double t) {
        return doExecutionMethod(ExecutionMethodType.QGRAMS, sourceKey, targetKey, q, t);
    }

    public ExecutionMethod doExecutionMethodSortedBlocks(Path sourceKey, Path targetKey,double overlap) {
        return doExecutionMethod(ExecutionMethodType.SORTEDBLOCK, sourceKey, targetKey, overlap);
    }

    public ExecutionMethod doExecutionMethodStringMap(Path sourceKey, Path targetKey, int distThreshold, double thresholdPercentage) {
        return doExecutionMethod(ExecutionMethodType.QGRAMS, sourceKey, targetKey, distThreshold, thresholdPercentage);
    }

    @SuppressWarnings("unchecked")
    public ExecutionMethod doExecutionMethod(ExecutionMethodType executionMethodType, Object... params) {
        switch (executionMethodType) {
            case BLOCKING:
                //noinspection unchecked
                return new de.fuberlin.wiwiss.silk.execution.methods.Blocking(
                        (Path) params[0],
                        (Path) params[1],
                        Integer.parseInt(params[2].toString()),
                        convertToScalaList((List<SimpleTransformer>) params[3]));
            case COMPOSITEBLOCKING:
                return new CompositeBlocking(
                        (Path) params[0],
                        (Path) params[1]);
            case FULL: return new Full();
            case MULTIBLOCK:return new MultiBlock();
            case MULTIPASSBLOCKING:
                //noinspection unchecked
                //noinspection unchecked
                return new MultiPassBlocking(convertToScalaSet((List<Path>) params[0]));
            case QGRAMS: return new QGrams(
                    (Path) params[0],
                    (Path) params[1],
                    Integer.parseInt(params[2].toString()),
                    Double.parseDouble(params[3].toString()));
            case SORTEDBLOCK:return new SortedBlocks(
                    (Path) params[0],
                    (Path) params[1],
                    Double.parseDouble(params[2].toString()));
            case STRINGMAP:return new StringMap(
                    (Path) params[0],
                    (Path) params[1],
                    Integer.parseInt(params[2].toString()),
                    Double.parseDouble(params[3].toString()));
            default:
                return null;
        }
    }


    public Prefixes doPrefixes(Map<String, String> prefixesSparql) {
        return new Prefixes(convertToScalaMap(prefixesSparql));
    }

    public RuntimeConfig doRuntimeConfig(ExecutionMethod executionMethod,
                                         Blocking blocking, boolean indexOnly, boolean useFileCache, boolean reloadCache,
                                         int partitionSize, int threads, boolean generateLinkWithEntities,
                                         String sHomeDir, java.util.logging.Level level) {
        return new RuntimeConfig(
                executionMethod, blocking, indexOnly, useFileCache, reloadCache,
                partitionSize, threads, generateLinkWithEntities, sHomeDir, level);
    }

    public PathOperator doPathOperatorBackward(String uri) {
        return doPathOperator(PathOperatorType.Backward, uri);
    }

    public PathOperator doPathOperatorForward(String uri) {
        return doPathOperator(PathOperatorType.Forward, uri);
    }

    private PathOperator doPathOperator(PathOperatorType pathOperatorType, Object... params) {
        switch (pathOperatorType) {
            case Backward:
                return new BackwardOperator(new Uri((String) params[0]));
            case Forward:
                return new ForwardOperator(new Uri((String) params[0]));
            case LanguageFilter:
                return new LanguageFilter((String) params[0], (String) params[1]);
            case PropertyFilter:
                return new PropertyFilter(new Uri((String) params[0]), (String) params[1], (String) params[2]);
            default:
                return null;
        }
    }

    public DataSource doDataSourceFILE(Resource file, String format, String graph) {
        return doDataSource(DataSourceSilkType.FILE, file, format, graph);
    }

    public DataSource doDataSourceCACHE(String dir) {
        return doDataSource(DataSourceSilkType.CACHE, dir);
    }

    public DataSource doDataSourceCSV(Resource file, String properties, char separator, String prefix) {
        return doDataSource(DataSourceSilkType.CSV, file, properties, separator, prefix);
    }

    public DataSource doDataSourceLINKEND() {
        return doDataSource(DataSourceSilkType.LINKEND);
    }

    public DataSource doDataSourceRDF(String input, String format) {
        return doDataSource(DataSourceSilkType.RDF, input, format);
    }

    public DataSource doDataSourceSPARQL(String endpointURI, String login,
                                         String password, String graph, int pageSize,
                                         String entityList, int pauseTime, int retryCount,
                                         int retryPause, String queryParameters, boolean parallel) {
        return doDataSource(DataSourceSilkType.SPARQL, endpointURI, login, password, graph, pageSize,
                entityList, pauseTime, retryCount, retryPause, queryParameters, parallel);
    }


    private DataSource doDataSource(DataSourceSilkType dataSourceSilkType, Object... params) {
        switch (dataSourceSilkType) {
            /*OLD 2.6.0*/
            /*case FILE:
                return new FileDataSource((Resource) params[0], (String) params[1], (String) params[2]);
            case CACHE:
                return new CacheDataSource((String) params[0]);
            case CSV:
                return new CsvDataSource((Resource) params[0], (String) params[1], params[2].toString().charAt(0), (String) params[3]);
            case LINKEND:
                return new LinkedDataSource();
            case RDF:
                return new RdfDataSource((String) params[0], (String) params[1]);
            case SPARQL:
                return new SparqlDataSource((String) params[0], (String) params[1],
                        (String) params[2], (String) params[3],  Integer.parseInt(params[4].toString()),
                        (String) params[5], Integer.parseInt(params[6].toString()), Integer.parseInt(params[7].toString()),
                        Integer.parseInt(params[8].toString()), (String) params[9],
                        Boolean.parseBoolean(params[10].toString().toLowerCase()));*/
            default:
                return null;
        }
    }

    public Path doPath(String variable, List<PathOperator> operators) {
        return new Path(variable, convertToScalaList(operators));
    }

    public Blocking doBlocking(boolean isEnabled, int blocks) {
        return new Blocking(isEnabled, blocks);
    }

    public Resource doResourceFile(File file) {
        return new FileResource(file.getName(), file);
    }

    public Resource doResourceClassPath(String filePath) {
        return new ClasspathResource(FileUtilities.getFilename(filePath), filePath);
    }

    /* 2.6.0 */
    /*public Source doSource(String id, DataSource dataSource) {
        Identifier identifier = new Identifier(id);
        return new Source(identifier, dataSource);
    }*/

    public SimpleTransformer doSimpleTransformerAlphaReduce() {
        return doSimpleTransformer(SimpleTransformerType.AlphaReduce);
    }

    private SimpleTransformer doSimpleTransformer(SimpleTransformerType simpleTransformerType, Object... params) {
        //@@ TODO to finish
        switch (simpleTransformerType) {
            case AlphaReduce:
                return new AlphaReduceTransformer();
            default:
                return null;
        }
    }

    public <T> DPair<T> doDPair(T source, T target) {
        return new DPair<>(source, target);
    }

    public Aggregator doAggregator(AggregatorType aggregatorType) {
        switch (aggregatorType) {
            case Average:
                return new AverageAggregator();
            case GeometricMean:
                return new GeometricMeanAggregator();
            case QuadraticMean:
                return new QuadraticMeanAggregator();
            case Maximum:
                return new MaximumAggregator();
            case Minimum:
                return new MinimumAggregator();
            default:
                return null;
        }
    }

    public SimilarityOperator doAggregation(
            Identifier id, boolean required, int weight,
            AggregatorType aggregatorType, List<SimilarityOperator> similarityOperators) {
        return new Aggregation(id, required, weight, doAggregator(aggregatorType), convertToScalaSeq(similarityOperators));
    }

    public SimilarityOperator doComparison(
            Identifier id, boolean required, int weight, double treshold, boolean indexing,
            DistanceMeasure distanceMeasure, DPair<Input> inputs) {
        return new Comparison(id, required, weight, treshold, indexing, distanceMeasure, inputs);
    }

    public Input doPathInput(Identifier id, Path path) {
        return new PathInput(id, path);
    }

    public Input doTransformInput(Identifier id, Transformer transformer, List<Input> inputs) {
        return new TransformInput(id, transformer, convertToScalaSeq(inputs));
    }

    /*public DataWriter doFileWriter(String file,String format){
        return new FileWriter(file,format);
    }*/

    /*public DataWriter doMemoryWriter(){
        return new MemoryWriter();
    }*/

    /*public DataWriter doSparqlWriter(String uri, String login, String password, String parameter, String graphUri){
        return new SparqlWriter(uri,login,password,parameter,graphUri);
    }*/

    /*public Output doOutput(Identifier id, DataWriter dataWriter,Object minConfidence,Object maxConfidence){
        return new Output(
                id,dataWriter,convertToToScalaOption(minConfidence),convertToToScalaOption(maxConfidence));
    }*/

    public Identifier doIdentifier(String name){
        return new Identifier(name);
    }

    public DistanceMeasure doDistanceMeasureCJKReadingDistance(char minChar, char maxChar) {
        return doDistanceMeasure(DistanceMeasureType.CJKReadingDistance, minChar, maxChar);
    }

    public DistanceMeasure doDistanceMeasureCosineDistanceMetric(int k) {
        return doDistanceMeasure(DistanceMeasureType.CosineDistanceMetric, k);
    }

    private DistanceMeasure doDistanceMeasure(DistanceMeasureType distanceMeasureType, Object... params) {
        //@@ TODO to finish
        switch (distanceMeasureType) {
            case CJKReadingDistance:
                return new CJKReadingDistance((char) params[0], (char) params[1]);
            case CosineDistanceMetric:
                return new CosineDistanceMetric((int) params[0]);
            default:
                return null;
        }
    }


    public LinkageRule doLinkageRule(SimilarityOperator similarityOperator, String uriLinkType) {
        return new LinkageRule(convertToToScalaOption(similarityOperator), null, new Uri(uriLinkType));
    }

   /* public LinkSpecification doLinkSpecification(
            Identifier id, Dataset source, Dataset target,LinkageRule linkageRule,List<Output> outputs) {
        return new LinkSpecification(id, doDPair(source, target), linkageRule,convertToScalaTraversable(outputs));
    }*/

    public enum PathOperatorType {Backward, Forward, LanguageFilter, PropertyFilter}

    public enum DataSourceSilkType {FILE, SPARQL, LINKEND, RDF, CSV, CACHE}

    public enum ExecutionMethodType {BLOCKING, COMPOSITEBLOCKING, FULL, MULTIBLOCK, MULTIPASSBLOCKING, QGRAMS, SORTEDBLOCK, STRINGMAP}

    public enum SimpleTransformerType {
        ConvertCharset, AlphaReduce, Capitalize, LowerCase, RemoveBlanks, RemoveParentheses, RemoveSpecialChars,
        TimestampToDate, UpperCase, Logarithm, MetaPhone, NormalizeChars, NumOperation, NumReduce, Nysiis, RegexReplace, Replace,
        Soundex, Stemmer, StripPostFix, StripPrefix, StripUriPrefix, Substring, UntilCharacter
    }

    public enum AggregatorType {Average, GeometricMean, Maximum, Minimum, QuadraticMean}

    public enum DistanceMeasureType {
        SimpleDistance, CJKReadingDistance, KoreanPhonemeDistance,
        KoreanTranslitDistance, CosineDistanceMetric, DiceCoefficient,
        TokenwiseStringDistance, DateMetric, DateTimeMetric, GeographicDistanceMetric, NumMetric, EqualityMetric,
        InequalityMetric, LowerThanMetric, RelaxedEqualityMetric, JaccardDistance, JaroDistanceMetric, JaroWinklerDistance,
        LevenshteinDistance, LevenshteinMetric, SoftJaccardDistance, SubStringDistance,
    }
   /* public de.fuberlin.wiwiss.silk.entity.Path createPathForRuntimeConfig(String variable, List<PathOperator> operators ){
        *//*List<PathOperator> operators = new ArrayList<>();
        operators.add(doPathOperatorBackward("uri_reference"));*//*
        return doPath(variable,operators);
    }*/

    private <K, V> scala.collection.immutable.Map<K, V> convertToScalaMap(Map<K, V> m) {
        return scala.collection.JavaConverters$.MODULE$.mapAsScalaMapConverter(m).asScala().toMap(
                scala.Predef$.MODULE$.<scala.Tuple2<K, V>>conforms()
        );
    }

    private <T> scala.collection.immutable.List<T> convertToScalaList(List<T> l) {
        return scala.collection.JavaConverters$.MODULE$.asScalaBufferConverter(l).asScala().toList();
    }

    private <T> scala.collection.mutable.Buffer<T> convertToScalaBuffer(List<T> l) {
        return scala.collection.JavaConverters$.MODULE$.asScalaBufferConverter(l).asScala().toBuffer();
    }

    private <T> scala.collection.Traversable<T> convertToScalaTraversable(List<T> l) {
        return scala.collection.JavaConverters$.MODULE$.asScalaIteratorConverter(l.iterator()).asScala().toTraversable();
    }

    private <T> scala.collection.Traversable<T> convertToScalaTraversable(T l) {
        return convertToScalaTraversable(Collections.singletonList(l));
    }

    private <T> scala.collection.Seq<T> convertToScalaSeq(List<T> l) {
        return scala.collection.JavaConverters$.MODULE$.asScalaIteratorConverter(l.iterator()).asScala().toSeq();
    }

    private <T> scala.collection.immutable.Set<T> convertToScalaSet(List<T> l) {
        return scala.collection.JavaConverters$.MODULE$.asScalaIteratorConverter(l.iterator()).asScala().toSet();
    }

    private <T> scala.collection.immutable.Set<T> convertToScalaSet(T l) {
        return convertToScalaSet(Collections.singletonList(l));
    }

    private <T> scala.Option<T> convertToToScalaOption(T object) {
        return scala.Option.apply(object);
    }


    /**
     * Check jena version for use silk
     */
    public static void checkJenaVersionForWorkWithSilk(){
        try{
            com.hp.hpl.jena.sparql.core.DatasetGraph dsg = com.hp.hpl.jena.sparql.core.DatasetGraphFactory.createMem();
            //com.hp.hpl.jena.query.Dataset  ds = com.hp.hpl.jena.query.DatasetFactory.createMem();
            //com.hp.hpl.jena.query.DatasetFactory.createMem()
        }catch(Exception e){
            logger.error(e.getMessage(),e);
        }
    }
}
