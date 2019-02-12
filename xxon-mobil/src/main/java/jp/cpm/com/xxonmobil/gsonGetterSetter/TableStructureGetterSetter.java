
package jp.cpm.com.xxonmobil.gsonGetterSetter;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TableStructureGetterSetter {

    @SerializedName("Table_Structure")
    @Expose
    private List<TableStructure> tableStructure = null;

    public List<TableStructure> getTableStructure() {
        return tableStructure;
    }

    public void setTableStructure(List<TableStructure> tableStructure) {
        this.tableStructure = tableStructure;
    }

}
