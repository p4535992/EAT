package p4535992.util.sql;

import java.util.Arrays;
import java.util.List;

/**
 * Created by 4535992 on 06/05/2015.
 */
public class SQLSupport{

    private String[] COLUMNS;
    private Object[] VALUES;
    private int[] TYPES;

    public String[] getCOLUMNS() {
        return COLUMNS;
    }

    public void setCOLUMNS(String[] COLUMNS) {
        this.COLUMNS = COLUMNS;
    }

    public Object[] getVALUES() {
        return VALUES;
    }

    public void setVALUES(Object[] VALUES) {
        this.VALUES = VALUES;
    }

    public int[] getTYPES() {
        return TYPES;
    }

    public void setTYPES(int[] TYPES) {
        this.TYPES = TYPES;
    }

    SQLSupport(String[] columns,Object[] values,int[] types){
        this.COLUMNS=columns;
        this.VALUES = values;
        this.TYPES = types;
    }

    SQLSupport(List<String> columns,List<Object> values,List<Integer> types){
        String[] acolumns = new String[columns.size()];
        Object[] avalues = new Object[values.size()];
        int[] atypes = new int[types.size()];
        for(int i = 0; i < columns.size(); i++) {
            acolumns[i] = columns.get(i);
            avalues[i] = values.get(i);
            atypes[i] = types.get(i);
        }
        this.COLUMNS=acolumns;
        this.VALUES = avalues;
        this.TYPES = atypes;
    }

    @Override
    public String toString() {
        return "SQLSupport{" +
                "COLUMNS=" + Arrays.toString(COLUMNS) +
                ", VALUES=" + Arrays.toString(VALUES) +
                ", TYPES=" + Arrays.toString(TYPES) +
                '}';
    }

}
