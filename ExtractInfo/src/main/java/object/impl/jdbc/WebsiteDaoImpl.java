package object.impl.jdbc;
import object.dao.jdbc.IWebsiteDao;
import object.impl.jdbc.generic.GenericDaoImpl;
import object.model.Website;
import org.hibernate.SessionFactory;
import extractor.estrattori.ExtractInfoSpring;

import javax.sql.DataSource;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marco on 31/03/2015.
 */
@org.springframework.stereotype.Component("WebsiteDao")
public class WebsiteDaoImpl extends GenericDaoImpl<Website> implements IWebsiteDao {

    @Override
    public void setDriverManager(String driver, String typeDb, String host,String port, String user, String pass, String database) {
        super.setDriverManager(driver,typeDb, host, port,user,  pass, database);
    }

    @Override
    public void setDataSource(DataSource ds) {
        super.dataSource = ds;
    }

    @Override
    public void setTableSelect(String nameOfTable){
        super.mySelectTable = nameOfTable;
    }

    @Override
    public void loadSpringConfig(String filePathXml) {

    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }


//    @Override
//    public List<String> select(String column,int limit,int offset) {
////       return this.jdbcTemplate.query("select " + column + " from " + mySelectTable + " LIMIT " + limit + " OFFSET " + offset + "",
////                new RowMapper<String>() {
////                    @Override
////                    public String mapRow(ResultSet rs, int rowNum) throws SQLException {
////                        return rs.getURL("url").toString();
////                    }
////                }
////        );
//        List<String> list = super.select(column,limit,offset);
//        return list;
//    }

    @Override
    public List<URL> selectAllUrl(String column,int limit,int offset) throws MalformedURLException {
        List<String> listStringUrl = super.select(column,limit,offset);
        List<URL> listUrl = new ArrayList<URL>();
        for(String sUrl : listStringUrl){
            URL u;
            if(!(sUrl.contains("http://"))){
                u = new URL("http://"+sUrl);
            }else{
                u = new URL(sUrl);
            }
            listUrl.add(u);
        }
        listStringUrl.clear();

        for(URL url : listUrl){
            try {
                if (ExtractInfoSpring.getGeoDocumentDao().verifyDuplicate(column, url.toString())) {
                    listStringUrl.add(url.toString());
                }
            }catch(org.springframework.jdbc.BadSqlGrammarException e){
                listStringUrl.add(url.toString());
            }
        }
        for(int i = 0; i < listStringUrl.size(); i++){
            listUrl.remove(new URL(listStringUrl.get(i)));
        }
        listStringUrl.clear();
        return listUrl;
    }


    public URL selectURL(String column, String column_where, String value_where){
        URL newURL;
        try {
            String url =(String) super.select(column,column_where,value_where,String.class);
            newURL =  new URL(url);
        }catch(MalformedURLException ue){
            newURL = null;
        }
        return newURL;
    }

    @Override
    public boolean verifyDuplicate(String columnWhereName,String valueWhereName){
        return super.verifyDuplicate(columnWhereName,valueWhereName);
    }



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
