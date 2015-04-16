package object.support;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marco on 02/04/2015.
 */
public class DBKit {

    //VARIABILI PER LA CONNESSIONE CON LA TABELLA SQL
    private String USER,PASS,DB,URL_DB;
    public List<TableKit> TableCollection = new ArrayList<>();

    private DriverKit dk = new DriverKit();
    private TableKit tab = new TableKit();

    public String getURL_DB() {
        setURL_DB(dk.getCONNECTION() + getDB());
        return this.URL_DB;
    }

    public void setURL_DB(String URL_DB) {
        this.URL_DB = URL_DB;
    }

    public DriverKit getDk() {
        return dk;
    }

    public void setDk(DriverKit dk) {
        this.dk = dk;
    }

    public TableKit getTab() {
        return tab;
    }

    public void setTab(TableKit tab) {
        this.tab = tab;
    }

    public String getUSER() {
        return USER;
    }

    public void setUSER(String USER) {
        this.USER = USER;
    }

    public String getPASS() {
        return PASS;
    }

    public void setPASS(String PASS) {
        this.PASS = PASS;
    }

    public String getDB() {
        return DB;
    }

    public void setDB(String DB) {
        this.DB = DB;
    }

    public DBKit(){};

    public DBKit(DriverKit dk,TableKit tab){
        this.dk = dk;
        this.tab =tab;
    }

    public String LOCAL_CFG(String localCfgFile){
        return System.getProperty("user.dir")+"\\src\\"+localCfgFile;
    }
}

class DriverKit{

    private String DRIVER_DB,DIALECT,HOST_DB,PORT_DB, CONNECTION;

    public String getCONNECTION() {
        return CONNECTION;
    }

    public void setCONNECTION(String CONNECTION) {
        this.CONNECTION = "" + DIALECT+ "://" +HOST_DB + ":" + PORT_DB + "/"; //"jdbc:mysql://localhost:3306/jdbctest";
    }

    public String getDRIVER_DB() {
        return DRIVER_DB;
    }

    public void setDRIVER_DB(String DRIVER_DB) {
        this.DRIVER_DB = DRIVER_DB;
    }

    public String getDIALECT() {
        return DIALECT;
    }

    public void setDIALECT(String DIALECT) {
        this.DIALECT = DIALECT;
    }

    public String getHOST_DB() {
        return HOST_DB;
    }

    public void setHOST_DB(String HOST_DB) {
        this.HOST_DB = HOST_DB;
    }

    public String getPORT_DB() {
        return PORT_DB;
    }

    public void setPORT_DB(String PORT_DB) {
        this.PORT_DB = PORT_DB;
    }



    public DriverKit(){

    }

}

class TableKit{
    public TableKit(){}
    public TableKit(Object[] columns,Object[] row){}

    public String getTABLE_NAME() {
        return TABLE_NAME;
    }

    public void setTABLE_NAME(String TABLE_NAME) {
        this.TABLE_NAME = TABLE_NAME;
    }

    private String TABLE_NAME;


}