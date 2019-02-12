package jp.cpm.com.xxonmobil.multiselectionspin;

import java.util.ArrayList;

import jp.cpm.com.xxonmobil.gsonGetterSetter.StoreCategoryMaster;

/**
 * Created by jeevanp on 2/2/2018.
 */

public interface SpinnerListener {
    void onItemsSelected(ArrayList<StoreCategoryMaster> items);
}