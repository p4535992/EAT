package com.github.p4535992.extractor.object.impl.jdbc;
import com.github.p4535992.extractor.object.dao.jdbc.IWebsiteDao;
import com.github.p4535992.extractor.object.impl.jdbc.generic.GenericDaoImpl;
import com.github.p4535992.extractor.object.model.Website;
import com.github.p4535992.util.string.StringUtilities;
import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;
import org.hibernate.SessionFactory;
import com.github.p4535992.extractor.estrattori.ExtractInfoSpring;

import javax.sql.DataSource;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marco on 31/03/2015.
 * @author 4535992.
 * @version 2015-06-30.
 */
@org.springframework.stereotype.Component("WebsiteDao")
public class WebsiteDaoImpl extends GenericDaoImpl<Website> implements IWebsiteDao {

    @Override
    public void setDriverManager(String driver, String dialectDB, String host,String port, String user, String pass, String database) {
        super.setDriverManager(driver, dialectDB, host, port,user,  pass, database);
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

    @Override
    public List<URL> selectAllUrl(String column,Integer limit,Integer offset) throws MalformedURLException {
        //List<String> listStringUrl = super.trySelect(new String[]{column},new String[]{column}, null, limit, offset, "AND");
        List<Object> listStringUrl = super.select(column,null,null,limit,offset,null);
        List<URL> listUrl = new ArrayList<>();
        for(Object sUrl : listStringUrl){
            URL u;
            //if (!sUrl.toString().matches("^(https?|ftp)://.*$")) {
            if(StringUtilities.isURLWithoutProtocol(String.valueOf(sUrl))){
                u = new URL("http://"+sUrl);
            }else{
                u = new URL(sUrl.toString());
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
        if(!listStringUrl.isEmpty()) {
            for (Object aListStringUrl : listStringUrl) {
                listUrl.remove(new URL(aListStringUrl.toString()));
            }
        }
        listStringUrl.clear();
        return listUrl;
    }


    public URL selectURL(String column, String column_where, String value_where){
        URL newURL;
        try {
            String url = (String) super.select(column, column_where, value_where);
            newURL =  new URL(url);
        }catch(MalformedURLException ue){
            newURL = null;
        }
        return newURL;
    }

    @Override
    public boolean verifyDuplicate(String column_where,String value_where) throws MySQLSyntaxErrorException {
        return super.verifyDuplicate(column_where, value_where);
    }

    /*
    @Override
    public List<Website> trySelectWithRowMap(String firstname, String lastname) {
        return this.jdbcTemplate.query("trySelectWithRowMap FIRSTNAME, LASTNAME from PERSON where FIRSTNAME = ? AND LASTNAME= ?",
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
