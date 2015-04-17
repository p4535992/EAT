package object.impl;
import object.dao.IGeoDocumentDao;
import object.dao.IWebsiteDao;
import object.model.Website;
import org.hibernate.SessionFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.orm.hibernate4.HibernateTemplate;

import javax.sql.DataSource;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * Created by Marco on 31/03/2015.
 */
@org.springframework.stereotype.Component("WebsiteDao")
public class WebsiteDaoImpl extends GenericDaoImpl<Website> implements IWebsiteDao {

    private DriverManagerDataSource driverManag;
    private JdbcTemplate jdbcTemplate;
    private String myTable;
    private DataSource dataSource;
    private HibernateTemplate hibernateTemplate;
    private SessionFactory sessionFactory;
    private ClassPathXmlApplicationContext contextClassPath;

    @Override
    public void setDriverManager(String driver, String typeDb, String host,String port, String user, String pass, String database) {
        driverManag = new DriverManagerDataSource();
        driverManag.setDriverClassName(driver);//"com.mysql.jdbc.Driver"
        driverManag.setUrl("" + typeDb + "://" + host + ":" + port + "/" + database); //"jdbc:mysql://localhost:3306/jdbctest"
        driverManag.setUsername(user);
        driverManag.setPassword(pass);
        this.jdbcTemplate = new JdbcTemplate();
        this.dataSource = driverManag;
        this.jdbcTemplate.setDataSource(dataSource);
    }

    @Override
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = new JdbcTemplate();
        this.jdbcTemplate.setDataSource(dataSource);
    }

    @Override
    public void setHibernateTemplate(HibernateTemplate ht) {
        this.hibernateTemplate = ht;
    }

    @Override
    public void setHibernateTemplate(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @Override
    public void setDataSource(DataSource ds) {
        this.dataSource = ds;
    }

    @Override
    public void setTable(String nameOfTable){
        this.myTable = nameOfTable;
    }

    @Override
    public void loadSpringConfig(String filePathXml) {

    }


    @Override
    public void loadHibernateConfig(String filePathXml) {
        contextClassPath = new ClassPathXmlApplicationContext(filePathXml);
        Website w = contextClassPath.getBean(Website.class);
    }


    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @Override
    public int getCount() {
        return this.jdbcTemplate.queryForObject("select count(*) from " + myTable + "", Integer.class);
    }

    @Override
    public List<Map<String,Object>> getAll() {
        return this.jdbcTemplate.queryForList("select * from " + myTable + "");
    }

    @Override
    public Number getAutoGeneratedKey(final String idColumn){
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        PreparedStatement ps = connection.prepareStatement("insert into "+myTable+" ("+ idColumn+") values(?)", new String[]{"id"});
                        ps.setString(1, "Marco");
                        return ps;
                    }
                },
                keyHolder);

        // keyHolder.getKey() now contains the generated key
        return keyHolder.getKey();
    }

    @Override
    public List<Website> selectAll() {
        return this.jdbcTemplate.query("select FIRSTNAME, LASTNAME from PERSON",
                new RowMapper<Website>() {
                    @Override
                    public Website mapRow(ResultSet rs, int rowNum) throws SQLException {
                        final Website w = new Website();
                        ResultSetExtractor extractor = new ResultSetExtractor() {

                            @Override
                            public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
                                w.setId(rs.getString("id"));
                                w.setCity(rs.getString("city"));
                                w.setUrl(rs.getString("url"));
                                return w;
                            }
                        };
                        return w;
                    }
                }
        );
    }

    @Override
    public List<String> selectAllString(String column,String limit, String offset) {
       return this.jdbcTemplate.query("select " + column + " from " + myTable + " LIMIT " + limit + " OFFSET " + offset + "",
                new RowMapper<String>() {
                    @Override
                    public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return rs.getURL("url").toString();
                    }
                }
        );
    }

    @Override
    public List<URL> selectAllUrl(String column,String limit,String offset) throws MalformedURLException {
        List<String> listStringUrl = selectAllString(column,limit, offset);
        List<URL> listUrl = new ArrayList<URL>();
        for(String sUrl : listStringUrl){
            URL u;
            if(!(sUrl.contains("http://")) && !(sUrl.contains("www"))){
                u = new URL("http://www."+sUrl);
            }
            else if(!(sUrl.contains("http://"))){
                u = new URL("http://"+sUrl);
            }else{
                u = new URL(sUrl);
            }
            listUrl.add(u);
        }
        listStringUrl.clear();

        IGeoDocumentDao geoDoc = new GeoDocumentDaoImpl();
        for(URL url : listUrl){
            if(geoDoc.verifyDuplicate(column, url.toString())==true){
                listStringUrl.add(url.toString());
            }
        }
        for(int i = 0; i < listStringUrl.size(); i++){
            listUrl.remove(new URL(listStringUrl.get(i)));
        }
        listStringUrl.clear();
        return listUrl;
    }


    @Override
    public boolean verifyDuplicate(String columnWhereName,String valueWhereName){
        int c = this.jdbcTemplate.queryForObject("select count(*) from "+myTable+" where "+columnWhereName+"='"+valueWhereName+"'", Integer.class);
        boolean b = false;
        if(c > 0){
            b = true;
        }
        return b;
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("DELETE from "+myTable+"");
    }




    @Override
    public void create() throws Exception {

    }

    @Override
    public void create(boolean erase) throws Exception {

    }

    ///////////////////////
    //HIBERNATE
    //////////////////////

    //method to save
    @Override
    public void saveH(Website g ){
        hibernateTemplate.save(g);
    }
    //method to update
    @Override
    public void updateH(Website g){
        hibernateTemplate.update(g);
    }
    //method to delete
    @Override
    public void deleteH(Website g){
        hibernateTemplate.delete(g);
    }
    //method to return one of given id
    @Override
    public Website  getHByColumn(String column){
        Website g = hibernateTemplate.get(Website.class,column);
        return g;
    }
    //method to return all
    @Override
    public List<Website> getAllH(){
        List<Website> list = new ArrayList<Website>();
        list = hibernateTemplate.loadAll(Website.class);
        return list;
    }



   /*
    public String getUrl() {
        return this.jdbcTemplate.queryForObject("select url from "+myTable+"", String.class);
    }
    */

      /*
    @Override
    public void create(String firstName, String lastName) {
        jdbcTemplate.update("INSERT INTO PERSON (FIRSTNAME, LASTNAME) VALUES(?,?)",
                new Object[] { firstName, lastName });
    }
  */

    /*
    @Override
    public List<Website> select(String firstname, String lastname) {
        return this.jdbcTemplate.query("select FIRSTNAME, LASTNAME from PERSON where FIRSTNAME = ? AND LASTNAME= ?",
                new Object[]{firstname, lastname},
                new RowMapper<Website>() {
                    @Override
                    public Website mapRow(ResultSet rs, int rowNum) throws SQLException {
                        final Website w = new Website();
                        ResultSetExtractor extractor = new ResultSetExtractor() {

                            @Override
                            public Object extractData(ResultSet rs) throws SQLException, DataAccessException {

                                w.setId(rs.getString("id"));
                                w.setCity(rs.getString("city"));
                                w.setUrl(rs.getString("url"));
                                return w;
                            }
                        };
                        return w;
                    }
                }
        );
    }
    */

    /*
    @Override
    public void delete(String firstName, String lastName) {
        jdbcTemplate.update("DELETE from PERSON where FIRSTNAME= ? AND LASTNAME = ?",
                new Object[] { firstName, lastName });
    }
    */

    /*
    public class WebsiteResultSetExtractor implements ResultSetExtractor {

        @Override
        public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
            Website w = new Website();
            w.setCity(rs.getString(1));
            return w;
        }
    }


    public class PersonRowMapper implements RowMapper {
        @Override
        public Object mapRow(ResultSet rs, int line) throws SQLException {
            WebsiteResultSetExtractor extractor = new WebsiteResultSetExtractor();
            return extractor.extractData(rs);
        }
    }

     */


    /*
    public int saveWesite(Website w){
        String query= String.format("insert into website values('%s','%s','%s','%s','%s','%s','%s')",
                w.getId(), w.getCity(), w.getUrl(), w.getCrawling_date(), w.getCrawling_date(),
                w.getDate_of_booking(), w.getProcessing_status());
        return jdbcTemplate.update(query);
    }

    public void saveWesite2(Website w){
            String sql = "INSERT INTO website " +
                    "(id, city, url,crawling_date,date_of_booking,processing_status) VALUES (?,?,?,?,?,?)";
            jdbcTemplate.update(sql, new Object[] {
                    w.getId(), w.getCity(), w.getUrl(), w.getCrawling_date(),
                    w.getDate_of_booking(), w.getProcessing_status()
                     }
            );
    }
    public int updateWebsiteUrl(Website w,String column){
        String query= String.format("update website set column='%s',salary='%s' where id='%s' ",
                w.getUrl(), w.getDate_of_booking(), w.getProcessing_status());
        return jdbcTemplate.update(query);
    }
    public int deleteEmployee(Website w){
        String query="delete from employee where id='"+w.getId()+"' ";
        return jdbcTemplate.update(query);
    }
    */
}
