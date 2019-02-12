package jp.cpm.com.xxonmobil.Database;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

import android.util.Log;

import java.util.HashMap;
import java.util.List;

import jp.cpm.com.xxonmobil.constant.CommonString;

import jp.cpm.com.xxonmobil.delegates.CoverageBean;
import jp.cpm.com.xxonmobil.gettersetter.GeotaggingBeans;
import jp.cpm.com.xxonmobil.gettersetter.StoreProfileGetterSetter;
import jp.cpm.com.xxonmobil.gsonGetterSetter.BeatWiseReport;
import jp.cpm.com.xxonmobil.gsonGetterSetter.AllObjectGetterSetter;
import jp.cpm.com.xxonmobil.gsonGetterSetter.DSRwiseReport;
import jp.cpm.com.xxonmobil.gsonGetterSetter.JCPGetterSetter;
import jp.cpm.com.xxonmobil.gsonGetterSetter.JourneyPlan;
import jp.cpm.com.xxonmobil.gsonGetterSetter.LastVisitPosmExecution;
import jp.cpm.com.xxonmobil.gsonGetterSetter.MappingPosm;
import jp.cpm.com.xxonmobil.gsonGetterSetter.MappingPosmChecklist;
import jp.cpm.com.xxonmobil.gsonGetterSetter.MappingUserCity;
import jp.cpm.com.xxonmobil.gsonGetterSetter.NonPosmReason;
import jp.cpm.com.xxonmobil.gsonGetterSetter.NonWorkingReason;
import jp.cpm.com.xxonmobil.gsonGetterSetter.NonWorkingReasonGetterSetter;
import jp.cpm.com.xxonmobil.gsonGetterSetter.PosmChecklist;
import jp.cpm.com.xxonmobil.gsonGetterSetter.PosmMaster;
import jp.cpm.com.xxonmobil.gsonGetterSetter.PosmMasterGetterSetter;
import jp.cpm.com.xxonmobil.gsonGetterSetter.ReportGetterSetter;

import static jp.cpm.com.xxonmobil.constant.CommonString.KEY_POSM_CD;

/**
 * /**
 * Created by jeevanp on 15-12-2017.
 */
@SuppressLint("LongLogTag")
public class Xxon_Database extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "XxonMobil_Dababa";
    public static final int DATABASE_VERSION = 2;
    private SQLiteDatabase db;
    Context context;

    public Xxon_Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public void open() {
        try {
            db = this.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CommonString.CREATE_TABLE_INSERT_MERCHANDISER_ATTENDENCE_TABLE);
            db.execSQL(CommonString.CREATE_TABLE_COVERAGE_DATA);
            db.execSQL(CommonString.CREATE_TABLE_POSM_OPENINGHEADER_DATA);
            db.execSQL(CommonString.CREATE_TABLE_STORE_POSM_DATA);
            db.execSQL(CommonString.CREATE_TABLE_STORE_PROFILE_DATA);
            db.execSQL(CommonString.CREATE_TABLE_STORE_GEOTAGGING);
            db.execSQL(CommonString.CREATE_TABLE_STORE_DEPLOYMENT_DATA);
        } catch (SQLException e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    public void deleteSpecificStoreData(String Store_Id) {
        try {
            db.delete(CommonString.TABLE_COVERAGE_DATA, CommonString.KEY_STORE_CD + "='" + Store_Id + "'", null);
            db.delete(CommonString.TABLE_INSERT_POSM_OPENINGHEADER_DATA, CommonString.KEY_STORE_CD + "='" + Store_Id + "'", null);
            db.delete(CommonString.TABLE_STORE_POSM_DATA, CommonString.KEY_STORE_CD + "='" + Store_Id + "'", null);
            db.delete(CommonString.TABLE_STORE_PROFILE_DATA, CommonString.KEY_STORE_CD + "='" + Store_Id + "'", null);
            db.delete(CommonString.TABLE_STORE_GEOTAGGING, CommonString.KEY_STORE_CD + "='" + Store_Id + "'", null);
            db.delete(CommonString.TABLE_STORE_DEPLOYMENT_DATA, CommonString.KEY_STORE_CD + "='" + Store_Id + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void deleteAllTables() {
        try {
            db.delete(CommonString.TABLE_COVERAGE_DATA, null, null);
            db.delete(CommonString.TABLE_INSERT_POSM_OPENINGHEADER_DATA, null, null);
            db.delete(CommonString.TABLE_STORE_POSM_DATA, null, null);
            db.delete(CommonString.TABLE_STORE_PROFILE_DATA, null, null);
            db.delete(CommonString.TABLE_STORE_GEOTAGGING, null, null);
            db.delete(CommonString.TABLE_STORE_DEPLOYMENT_DATA, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void deletePreviousUploadedData(String visit_date) {
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * from COVERAGE_DATA where VISIT_DATE < '" + visit_date + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                int icount = dbcursor.getCount();
                dbcursor.close();
                if (icount > 0) {
                    db.delete(CommonString.TABLE_COVERAGE_DATA, null, null);
                    db.delete(CommonString.TABLE_INSERT_POSM_OPENINGHEADER_DATA, null, null);
                    db.delete(CommonString.TABLE_STORE_POSM_DATA, null, null);
                    db.delete(CommonString.TABLE_STORE_PROFILE_DATA, null, null);
                    db.delete(CommonString.TABLE_STORE_GEOTAGGING, null, null);
                    db.delete(CommonString.TABLE_STORE_DEPLOYMENT_DATA, null, null);
                }
                dbcursor.close();
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Coverage Data!!!!!!!!!!!!!!!!!!!!!", e.toString());
            Crashlytics.logException(e);
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public int createtable(String sqltext) {
        try {
            db.execSQL(sqltext);
            return 1;
        } catch (Exception ex) {
            ex.printStackTrace();
            Crashlytics.logException(ex);
            return 0;
        }
    }


    public boolean insertmappinguserCity(AllObjectGetterSetter dsr) {
        db.delete("Mapping_User_City", null, null);
        List<MappingUserCity> list = dsr.getMappingUserCity();

        ContentValues values = new ContentValues();
        try {
            if (list.size() == 0) {
                return false;
            }
            for (int i = 0; i < list.size(); i++) {
                values.put("City_Id", list.get(i).getCityId());
                values.put("City", list.get(i).getCity());
                values.put("Visit_Date", list.get(i).getVisitDate());
                long id = db.insert("Mapping_User_City", null, values);
                if (id == -1) {
                    throw new Exception();
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("Exception ", " in Posm_Master " + ex.toString());
            return false;
        }
    }


    public ArrayList<MappingUserCity> getcitymapping(String visit_date) {
        ArrayList<MappingUserCity> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * FROM Mapping_User_City WHERE Visit_Date ='" + visit_date + "' Order by City", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    MappingUserCity sb = new MappingUserCity();
                    sb.setCityId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("City_Id")));
                    sb.setCity(dbcursor.getString(dbcursor.getColumnIndexOrThrow("City")));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception get JCP!", e.toString());
            Crashlytics.logException(e);
            return list;
        }

        return list;
    }


    public long deleteJourneyPlan() {
        long l = 0;
        l = db.delete("Journey_Plan", null, null);
        return l;
    }

    public boolean insertJCPData(JCPGetterSetter data) {
        db.delete("Journey_Plan", null, null);
        List<JourneyPlan> jcpList = data.getJourneyPlan();

        ContentValues values = new ContentValues();
        try {
            if (jcpList.size() == 0) {
                return false;
            }
            for (int i = 0; i < jcpList.size(); i++) {
                values.put("Store_Id", jcpList.get(i).getStoreId());
                values.put("Visit_Date", jcpList.get(i).getVisitDate());
                values.put("Distributor", jcpList.get(i).getDistributor());
                values.put("Store_Name", jcpList.get(i).getStoreName());
                values.put("Address", jcpList.get(i).getAddress());
                values.put("Pincode", jcpList.get(i).getPincode());
                values.put("Contact_Person", jcpList.get(i).getContactPerson());
                values.put("Contact_No", jcpList.get(i).getContactNo());
                values.put("Mobile_No", jcpList.get(i).getMobileNo());
                values.put("Store_Email", jcpList.get(i).getStoreEmail());
                values.put("Visiting_Card_Pic", jcpList.get(i).getVisitingCardPic());
                values.put("City", jcpList.get(i).getCity());
                values.put("Latitude", jcpList.get(i).getLatitude());
                values.put("Longitude", jcpList.get(i).getLongitude());
                values.put("Store_Category", jcpList.get(i).getStoreCategory());
                values.put("Store_Category_Id", jcpList.get(i).getStoreCategoryId());
                values.put("Store_Type", jcpList.get(i).getStoreType());
                values.put("Store_Type_Id", jcpList.get(i).getStoreTypeId());
                values.put("Distributor_Id", jcpList.get(i).getDistributorId());
                values.put("Upload_Status", jcpList.get(i).getUploadStatus());
                values.put("State_Id", jcpList.get(i).getStateId());
                values.put("Region_Id", jcpList.get(i).getRegionId());
                values.put("Last_Visit_Date", jcpList.get(i).getLastVisitDate());
                values.put("GeoTag", jcpList.get(i).getGeoTag());
                values.put("Ageing_Of_Branding", jcpList.get(i).getAgeing_Of_Branding());
                long id = db.insert("Journey_Plan", null, values);
                if (id == -1) {
                    throw new Exception();
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("Exception in Jcp", ex.toString());
            Crashlytics.logException(ex);
            return false;
        }
    }


    public boolean insertJCPDataforaudit(AllObjectGetterSetter data) {
        db.delete("Journey_Plan", null, null);
        List<JourneyPlan> jcpList = data.getJourneyPlan();

        ContentValues values = new ContentValues();
        try {
            if (jcpList.size() == 0) {
                return false;
            }
            for (int i = 0; i < jcpList.size(); i++) {
                values.put("Store_Id", jcpList.get(i).getStoreId());
                values.put("Visit_Date", jcpList.get(i).getVisitDate());
                values.put("Distributor", jcpList.get(i).getDistributor());
                values.put("Store_Name", jcpList.get(i).getStoreName());
                values.put("Address", jcpList.get(i).getAddress());
                values.put("Pincode", jcpList.get(i).getPincode());
                values.put("Contact_Person", jcpList.get(i).getContactPerson());
                values.put("Contact_No", jcpList.get(i).getContactNo());
                values.put("Mobile_No", jcpList.get(i).getMobileNo());
                values.put("Store_Email", jcpList.get(i).getStoreEmail());
                values.put("Visiting_Card_Pic", jcpList.get(i).getVisitingCardPic());
                values.put("City", jcpList.get(i).getCity());
                values.put("Latitude", jcpList.get(i).getLatitude());
                values.put("Longitude", jcpList.get(i).getLongitude());
                values.put("Store_Category", jcpList.get(i).getStoreCategory());
                values.put("Store_Category_Id", jcpList.get(i).getStoreCategoryId());
                values.put("Store_Type", jcpList.get(i).getStoreType());
                values.put("Store_Type_Id", jcpList.get(i).getStoreTypeId());
                values.put("Distributor_Id", jcpList.get(i).getDistributorId());
                values.put("Upload_Status", jcpList.get(i).getUploadStatus());
                values.put("State_Id", jcpList.get(i).getStateId());
                values.put("Region_Id", jcpList.get(i).getRegionId());
                values.put("Last_Visit_Date", jcpList.get(i).getLastVisitDate());
                values.put("GeoTag", jcpList.get(i).getGeoTag());
                values.put("Ageing_Of_Branding", jcpList.get(i).getAgeing_Of_Branding());
                long id = db.insert("Journey_Plan", null, values);
                if (id == -1) {
                    throw new Exception();
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("Exception in Jcp", ex.toString());
            Crashlytics.logException(ex);
            return false;
        }
    }


    public boolean insertNonWorkingData(NonWorkingReasonGetterSetter nonWorkingdata) {
        db.delete("Non_Working_Reason", null, null);
        ContentValues values = new ContentValues();
        List<NonWorkingReason> data = nonWorkingdata.getNonWorkingReason();
        try {
            if (data.size() == 0) {
                return false;
            }

            for (int i = 0; i < data.size(); i++) {
                values.put("Reason_Id", data.get(i).getReasonId());
                values.put("Reason", data.get(i).getReason());
                values.put("Entry_Allow", data.get(i).getEntryAllow());
                values.put("Image_Allow", data.get(i).getImageAllow());
                values.put("GPS_Mandatory", data.get(i).getGPSMandatory());
                values.put("For_Coverage", data.get(i).getForCoverage());
                values.put("For_Attendance", data.get(i).getForAttendance());
                long id = db.insert("Non_Working_Reason", null, values);
                if (id == -1) {
                    throw new Exception();
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("Database Exception  ", ex.toString());
            return false;
        }
    }

    public boolean insertPosmMaster(PosmMasterGetterSetter posmmaster) {
        db.delete("Posm_Master", null, null);
        List<PosmMaster> list = posmmaster.getPosmMaster();

        ContentValues values = new ContentValues();
        try {
            if (list.size() == 0) {
                return false;
            }
            for (int i = 0; i < list.size(); i++) {
                values.put("Posm_Id", list.get(i).getPosmId());
                values.put("Posm", list.get(i).getPosm());
                values.put("Posm_Type_Id", list.get(i).getPosmTypeId());
                values.put("Posm_Type", list.get(i).getPosmType());
                values.put("Ref_Image", list.get(i).getRefImage());
                long id = db.insert("Posm_Master", null, values);
                if (id == -1) {
                    throw new Exception();
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("Exception ", " in Posm_Master " + ex.toString());
            return false;
        }
    }


    public boolean insertmappingchecklistposm(PosmMasterGetterSetter posmmaster) {
        db.delete("Mapping_Posm_Checklist", null, null);
        List<MappingPosmChecklist> list = posmmaster.getMappingPosmChecklist();

        ContentValues values = new ContentValues();
        try {
            if (list.size() == 0) {
                return false;
            }
            for (int i = 0; i < list.size(); i++) {
                values.put("Posm_Id", list.get(i).getPosmId());
                values.put("Checklist_Id", list.get(i).getChecklistId());
                long id = db.insert("Mapping_Posm_Checklist", null, values);
                if (id == -1) {
                    throw new Exception();
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("Exception ", " in Mapping_Posm_Checklist " + ex.toString());
            return false;
        }
    }


    public boolean insertposm_checklist(PosmMasterGetterSetter posmmaster) {
        db.delete("Posm_Checklist", null, null);
        List<PosmChecklist> list = posmmaster.getPosmChecklist();

        ContentValues values = new ContentValues();
        try {
            if (list.size() == 0) {
                return false;
            }
            for (int i = 0; i < list.size(); i++) {
                values.put("Checklist_Id", list.get(i).getChecklistId());
                values.put("Checklist", list.get(i).getChecklist());
                values.put("Answer_Id", list.get(i).getAnswerId());
                values.put("Answer", list.get(i).getAnswer());
                long id = db.insert("Posm_Checklist", null, values);
                if (id == -1) {
                    throw new Exception();
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("Exception ", " in Posm_Checklist " + ex.toString());
            return false;
        }
    }


    public boolean insertmappingposm(JCPGetterSetter mappingposm) {
        db.delete("Mapping_Posm", null, null);
        List<MappingPosm> list = mappingposm.getMappingPosm();

        ContentValues values = new ContentValues();
        try {
            if (list.size() == 0) {
                return false;
            }
            for (int i = 0; i < list.size(); i++) {
                values.put("Posm_Id", list.get(i).getPosmId());
                values.put("Region_Id", list.get(i).getRegionId());
                values.put("Store_Category_Id", list.get(i).getStoreCategoryId());
                long id = db.insert("Mapping_Posm", null, values);
                if (id == -1) {
                    throw new Exception();
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("Exception ", " in Posm_Master " + ex.toString());
            return false;
        }
    }


    public boolean insertuploadedposmData(JCPGetterSetter posm_execution) {
        db.delete("Last_Visit_Posm_Execution", null, null);
        List<LastVisitPosmExecution> list = posm_execution.getLastVisitPosmExecution();

        ContentValues values = new ContentValues();
        try {
            if (list.size() == 0) {
                return false;
            }
            for (int i = 0; i < list.size(); i++) {
                values.put("Store_Id", list.get(i).getStoreId());
                values.put("MID", list.get(i).getMID());
                values.put("Posm_Id", list.get(i).getPosmId());
                values.put("Installed", list.get(i).getInstalled());
                values.put("Preason_Id", list.get(i).getPreasonId());
                values.put("Image_Url", list.get(i).getImageUrl());
                values.put("Image_Path", list.get(i).getImagePath());
                values.put("Remark", list.get(i).getRemark());
                long id = db.insert("Last_Visit_Posm_Execution", null, values);
                if (id == -1) {
                    throw new Exception();
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("Exception ", " in Posm_Master " + ex.toString());
            return false;
        }
    }


    public boolean insertnonposmReason(PosmMasterGetterSetter auditdata) {
        db.delete("Non_Posm_Reason", null, null);
        ContentValues values = new ContentValues();
        List<NonPosmReason> data = auditdata.getNonPosmReason();
        try {
            if (data.size() == 0) {
                return false;
            }

            for (int i = 0; i < data.size(); i++) {
                values.put("Preason_Id", data.get(i).getPreasonId());
                values.put("Preason", data.get(i).getPreason());
                values.put("Editable", data.get(i).getEditable());
                values.put("Remark", data.get(i).getRemark());

                long id = db.insert("Non_Posm_Reason", null, values);
                if (id == -1) {
                    throw new Exception();
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("Database Exception  ", ex.toString());
            Crashlytics.logException(ex);
            return false;
        }
    }

    public ArrayList<BeatWiseReport> getbeatwiseORDSRWiseReport(boolean flag) {
        ArrayList<BeatWiseReport> list = new ArrayList<BeatWiseReport>();
        Cursor dbcursor = null;
        try {
            if (flag) {
                dbcursor = db.rawQuery("SELECT * FROM BeatWise_Report", null);
            } else {
                dbcursor = db.rawQuery("SELECT * FROM DSRwise_Report", null);
            }

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    if (flag) {
                        BeatWiseReport sb = new BeatWiseReport();
                        sb.setBeatName(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Beat_Name")));
                        sb.setTargetOutlet(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Target_Outlet")));
                        sb.setMerchandised(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Merchandised")));
                        sb.setOutletAch(dbcursor.getDouble(dbcursor.getColumnIndexOrThrow("Outlet_Ach")));
                        sb.setOutletPerfect((dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Outlet_Perfect"))));
                        sb.setPefectAch(dbcursor.getDouble(dbcursor.getColumnIndexOrThrow("Perfect_Ach")));
                        list.add(sb);
                        dbcursor.moveToNext();
                    } else {
                        BeatWiseReport sb = new BeatWiseReport();
                        sb.setBeatName(dbcursor.getString(dbcursor.getColumnIndexOrThrow("DSR_Code")));
                        sb.setTargetOutlet(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Target_Outlet")));
                        sb.setMerchandised(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Merchandised")));
                        sb.setOutletAch(dbcursor.getDouble(dbcursor.getColumnIndexOrThrow("Outlet_Ach")));
                        sb.setOutletPerfect((dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Outlet_Perfect"))));
                        sb.setPefectAch(dbcursor.getDouble(dbcursor.getColumnIndexOrThrow("Perfect_Ach")));
                        list.add(sb);
                        dbcursor.moveToNext();
                    }
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception get JCP!", e.toString());
            Crashlytics.logException(e);
            return list;
        }


        return list;
    }


    public boolean insertbeatwisereportDat(ReportGetterSetter auditdata) {
        db.delete("BeatWise_Report", null, null);
        ContentValues values = new ContentValues();
        List<BeatWiseReport> data = auditdata.getBeatWiseReport();
        try {
            if (data.size() == 0) {
                return false;
            }

            for (int i = 0; i < data.size(); i++) {
                values.put("Beat_Name", data.get(i).getBeatName());
                values.put("Target_Outlet", data.get(i).getTargetOutlet());
                values.put("Merchandised", data.get(i).getMerchandised());
                values.put("Outlet_Ach", data.get(i).getOutletAch());
                values.put("Outlet_Perfect", data.get(i).getOutletPerfect());
                values.put("Perfect_Ach", data.get(i).getPefectAch());

                long id = db.insert("BeatWise_Report", null, values);
                if (id == -1) {
                    throw new Exception();
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("Database Exception  ", ex.toString());
            Crashlytics.logException(ex);
            return false;
        }
    }

    public boolean insertDSRWiseReportData(ReportGetterSetter auditdata) {
        db.delete("DSRwise_Report", null, null);
        ContentValues values = new ContentValues();
        List<DSRwiseReport> data = auditdata.getDSRwiseReport();
        try {
            if (data.size() == 0) {
                return false;
            }

            for (int i = 0; i < data.size(); i++) {
                values.put("DSR_Code", data.get(i).getDSRCode());
                values.put("Target_Outlet", data.get(i).getTargetOutlet());
                values.put("Merchandised", data.get(i).getMerchandised());
                values.put("Outlet_Ach", data.get(i).getOutletAch());
                values.put("Outlet_Perfect", data.get(i).getOutletPerfect());
                values.put("Perfect_Ach", data.get(i).getPerfectAch());

                long id = db.insert("DSRwise_Report", null, values);
                if (id == -1) {
                    throw new Exception();
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("Database Exception  ", ex.toString());
            Crashlytics.logException(ex);
            return false;
        }
    }


    public ArrayList<JourneyPlan> getfilteredJCPstore(String dentist_name) {
        ArrayList<JourneyPlan> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("select  * from Journey_Plan where Store_Name like'%" + dentist_name + "%' or Store_Type like'%" + dentist_name + "%'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    JourneyPlan sb = new JourneyPlan();
                    sb.setStoreId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Store_Id")));
                    sb.setVisitDate(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Visit_Date")));
                    sb.setDistributor(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Distributor")));
                    sb.setStoreName(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Name")));
                    sb.setAddress((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Address"))));
                    sb.setPincode((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Pincode"))));
                    sb.setContactPerson((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Contact_Person"))));
                    sb.setContactNo((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Contact_No"))));
                    sb.setMobileNo((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Mobile_No"))));
                    sb.setStoreEmail((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Email"))));
                    sb.setVisitingCardPic((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Visiting_Card_Pic"))));

                    sb.setLatitude((dbcursor.getDouble(dbcursor.getColumnIndexOrThrow("Latitude"))));
                    sb.setLongitude((dbcursor.getDouble(dbcursor.getColumnIndexOrThrow("Longitude"))));
                    sb.setGeoTag((dbcursor.getString(dbcursor.getColumnIndexOrThrow("GeoTag"))));
                    sb.setAgeing_Of_Branding((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Ageing_Of_Branding"))));

                    sb.setCity(dbcursor.getString(dbcursor.getColumnIndexOrThrow("City")));
                    sb.setStoreCategory(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Category")));
                    sb.setStoreCategoryId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Store_Category_Id")));
                    sb.setStoreType(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Type")));
                    sb.setStoreTypeId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Store_Type_Id")));
                    sb.setDistributorId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Distributor_Id")));
                    sb.setUploadStatus(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Upload_Status")));
                    sb.setLastVisitDate(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Last_Visit_Date")));
                    sb.setStateId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("State_Id")));
                    sb.setRegionId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Region_Id")));

                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception get JCP!", e.toString());
            return list;
        }


        return list;
    }


    public ArrayList<JourneyPlan> getStoreData(String date) {
        ArrayList<JourneyPlan> list = new ArrayList<JourneyPlan>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * FROM Journey_Plan  " + "WHERE Visit_Date ='" + date + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    JourneyPlan sb = new JourneyPlan();
                    sb.setStoreId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Store_Id")));
                    sb.setVisitDate(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Visit_Date")));
                    sb.setDistributor(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Distributor")));
                    sb.setStoreName(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Name")));
                    sb.setAddress((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Address"))));
                    sb.setPincode((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Pincode"))));
                    sb.setContactPerson((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Contact_Person"))));
                    sb.setContactNo((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Contact_No"))));
                    sb.setMobileNo((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Mobile_No"))));
                    sb.setStoreEmail((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Email"))));
                    sb.setVisitingCardPic((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Visiting_Card_Pic"))));

                    sb.setLatitude((dbcursor.getDouble(dbcursor.getColumnIndexOrThrow("Latitude"))));
                    sb.setLongitude((dbcursor.getDouble(dbcursor.getColumnIndexOrThrow("Longitude"))));
                    sb.setGeoTag((dbcursor.getString(dbcursor.getColumnIndexOrThrow("GeoTag"))));
                    sb.setAgeing_Of_Branding((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Ageing_Of_Branding"))));
                    sb.setCity(dbcursor.getString(dbcursor.getColumnIndexOrThrow("City")));
                    sb.setStoreCategory(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Category")));
                    sb.setStoreCategoryId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Store_Category_Id")));
                    sb.setStoreType(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Type")));
                    sb.setStoreTypeId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Store_Type_Id")));
                    sb.setDistributorId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Distributor_Id")));
                    sb.setUploadStatus(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Upload_Status")));
                    sb.setLastVisitDate(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Last_Visit_Date")));
                    sb.setStateId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("State_Id")));
                    sb.setRegionId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Region_Id")));

                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception get JCP!", e.toString());
            Crashlytics.logException(e);
            return list;
        }


        return list;
    }

    public boolean isCoverageDataFilled(String visit_date) {
        boolean filled = false;
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT * FROM COVERAGE_DATA " + "where " + CommonString.KEY_VISIT_DATE + "<>'" + visit_date + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                int icount = dbcursor.getCount();
                dbcursor.close();
                if (icount > 0) {
                    filled = true;
                } else {
                    filled = false;
                }
            }
        } catch (Exception e) {
            Log.d("Exception when fetching Records!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            Crashlytics.logException(e);
            return filled;
        }

        return filled;
    }


    public long InsertCoverageData(CoverageBean data) {
        db.delete(CommonString.TABLE_COVERAGE_DATA, CommonString.KEY_STORE_CD + "='" + data.getStore_Id() + "' AND VISIT_DATE='" + data.getVisitDate() + "'", null);
        ContentValues values = new ContentValues();
        long l = 0;
        try {
            values.put(CommonString.KEY_STORE_CD, data.getStore_Id());
            values.put(CommonString.KEY_USER_ID, data.getUserId());
            values.put(CommonString.KEY_VISIT_DATE, data.getVisitDate());
            values.put(CommonString.KEY_LATITUDE, data.getLatitude());
            values.put(CommonString.KEY_LONGITUDE, data.getLongitude());
            values.put(CommonString.KEY_IMAGE, data.getImage());
            values.put(CommonString.KEY_COVERAGE_REMARK, data.getRemark());
            values.put(CommonString.KEY_REASON_ID, data.getReasonid());
            values.put(CommonString.KEY_REASON, data.getReason());
            values.put(CommonString.KEY_CITY_NAME, data.getCity_name());
            l = db.insert(CommonString.TABLE_COVERAGE_DATA, null, values);
        } catch (Exception ex) {
            Log.d("Database Exception while Insert Closes Data ", ex.toString());
            Crashlytics.logException(ex);
        }
        return l;
    }

    public ArrayList<CoverageBean> getSpecificCoverageData(String visitdate, String store_Id) {
        ArrayList<CoverageBean> list = new ArrayList<CoverageBean>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * from " + CommonString.TABLE_COVERAGE_DATA + " where " + CommonString.KEY_VISIT_DATE + "='" + visitdate + "' AND " +
                    CommonString.KEY_STORE_CD + "='" + store_Id + "'", null);


            if (dbcursor != null) {

                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CoverageBean sb = new CoverageBean();
                    sb.setStore_Id(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_STORE_CD)));
                    sb.setUserId(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_USER_ID)));
                    sb.setVisitDate(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_VISIT_DATE)));
                    sb.setLatitude(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LATITUDE)));
                    sb.setLongitude(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LONGITUDE)));
                    sb.setImage(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IMAGE)));
                    sb.setReason(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON)));
                    sb.setReasonid(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON_ID)));
                    sb.setMID(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ID))));
                    sb.setRemark(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COVERAGE_REMARK)));
                    sb.setCity_name(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_CITY_NAME)));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Log.d("Exception when fetching Coverage Data!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            Crashlytics.logException(e);

        }

        return list;

    }

    //jeevan   nmjnmn,
    public long updateJaurneyPlanSpecificStoreStatus(String _Id, String visit_date, String status) {
        long l = 0;
        try {
            ContentValues values = new ContentValues();
            values.put("Upload_Status", status);
            l = db.update("Journey_Plan", values, " Store_Id ='" + _Id + "' AND Visit_Date ='" + visit_date + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return l;
    }

    public long updateJaurneyPlanSpecificStoreofLastVisit_date(String _Id, String visit_date) {
        long l = 0;
        try {
            ContentValues values = new ContentValues();
            values.put("Last_Visit_Date", visit_date);
            l = db.update("Journey_Plan", values, " Store_Id ='" + _Id + "' AND Visit_Date ='" + visit_date + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return l;
    }

    public long updateJaurneyPlanPosmEditableStatus(String _Id, String visit_date, String status) {
        long l = 0;
        try {
            ContentValues values = new ContentValues();
            values.put("Record_Edit", status);
            l = db.update("Journey_Plan", values, " Store_Id ='" + _Id + "' AND Visit_Date ='" + visit_date + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return l;
    }


    //jeevan   nmjnmn,
    public ArrayList<JourneyPlan> getSpecificStoreData(String store_id) {
        ArrayList<JourneyPlan> list = new ArrayList<JourneyPlan>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * from Journey_Plan  " + "where Store_Id ='" + store_id + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    JourneyPlan sb = new JourneyPlan();
                    sb.setStoreId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Store_Id")));
                    sb.setVisitDate(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Visit_Date")));
                    sb.setDistributor(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Distributor")));
                    sb.setStoreName(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Name")));
                    sb.setAddress((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Address"))));
                    sb.setPincode((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Pincode"))));
                    sb.setContactPerson((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Contact_Person"))));
                    sb.setContactNo((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Contact_No"))));
                    sb.setMobileNo((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Mobile_No"))));
                    sb.setStoreEmail((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Email"))));
                    sb.setVisitingCardPic((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Visiting_Card_Pic"))));

                    sb.setLatitude((dbcursor.getDouble(dbcursor.getColumnIndexOrThrow("Latitude"))));
                    sb.setLongitude((dbcursor.getDouble(dbcursor.getColumnIndexOrThrow("Longitude"))));
                    sb.setGeoTag((dbcursor.getString(dbcursor.getColumnIndexOrThrow("GeoTag"))));
                    sb.setAgeing_Of_Branding((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Ageing_Of_Branding"))));
                    sb.setCity(dbcursor.getString(dbcursor.getColumnIndexOrThrow("City")));
                    sb.setStoreCategory(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Category")));
                    sb.setStoreCategoryId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Store_Category_Id")));
                    sb.setStoreType(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Type")));
                    sb.setStoreTypeId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Store_Type_Id")));
                    sb.setDistributorId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Distributor_Id")));
                    sb.setUploadStatus(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Upload_Status")));
                    sb.setLastVisitDate(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Last_Visit_Date")));
                    sb.setStateId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("State_Id")));
                    sb.setRegionId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Region_Id")));

                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception get JCP!", e.toString());
            Crashlytics.logException(e);
            return list;
        }


        return list;
    }


    public ArrayList<PosmMaster> getStoreAuditHeaderData(String region_Id, String store_category_Id) {
        Log.d("Fetching", "Storedata--------------->Start<------------");
        ArrayList<PosmMaster> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("select distinct pm.Posm_Id,pm.Posm,pm.Posm_Type_Id,pm.Posm_Type,pm.Ref_Image from Posm_Master pm " +
                    "inner join Mapping_Posm mp on mp.Posm_Id=pm.Posm_Id where mp.Region_Id='" + region_Id + "' and mp.Store_Category_Id='" + store_category_Id + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    PosmMaster sb = new PosmMaster();
                    sb.setPosmId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Posm_Id")));
                    sb.setPosm(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Posm")));
                    sb.setPosmTypeId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Posm_Type_Id")));
                    sb.setPosmType(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Posm_Type")));
                    sb.setRefImage(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Ref_Image")));
                    sb.setPosm_deployment("0");
                    sb.setCurrect_reason_Id("0");
                    sb.setCurrect_reason("");
                    sb.setDeployment_img_one("");
                    sb.setDeployment_img_two("");
                    sb.setEdittext_remarkfor_others("");
                    sb.setRemarkflag(false);
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("Exception ", "when fetching opening stock!!!!!!!!!!!" + e.toString());
            return list;
        }

        Log.d("Fetching ", "opening stock---------------------->Stop<-----------");
        return list;
    }


    public ArrayList<PosmMaster> getstoredeploymentList(String region_Id, String store_category_Id) {
        Log.d("Fetching", "Storedata--------------->Start<------------");
        ArrayList<PosmMaster> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("select distinct pm.Posm_Id,pm.Posm from Posm_Master pm " +
                    "inner join Mapping_Posm mp on mp.Posm_Id=pm.Posm_Id where mp.Region_Id='" + region_Id + "' and mp.Store_Category_Id='" + store_category_Id + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    PosmMaster sb = new PosmMaster();
                    sb.setPosmId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Posm_Id")));
                    sb.setPosm(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Posm")));
                    sb.setPosm_deployment("");
                    sb.setDeployment_img_one("");
                    sb.setDeployment_img_two("");
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("Exception ", "when fetching opening stock!!!!!!!!!!!" + e.toString());
            return list;
        }

        Log.d("Fetching ", "opening stock---------------------->Stop<-----------");
        return list;
    }


    public ArrayList<PosmMaster> getinsertedauditHeaderData(String store_Id, String visit_date) {
        Log.d("Fetching", "Storedata--------------->Start<------------");
        ArrayList<PosmMaster> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("Select distinct PD.POSM_CD,PD.POSM,PD.POSM_TYPE_ID,PD.POSM_TYPE,PD.DEPLOYED," +
                    "PD.POSM_ONE_IMG,PD.POSM_TWO_IMG,PD.REASON_CD,PD.REASON,PM.Ref_Image,PD.ID,PD.REMARK_FLAG,PD.REMARK from POSM_HEADER_DATA PD inner join " +
                    "Posm_Master PM on PM.Posm_Id=PD.POSM_CD where PD.STORE_CD='" + store_Id + "' and PD.VISIT_DATE='" + visit_date + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    PosmMaster sb = new PosmMaster();
                    sb.setPosmId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("POSM_CD")));
                    sb.setPosm(dbcursor.getString(dbcursor.getColumnIndexOrThrow("POSM")));
                    sb.setPosmTypeId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("POSM_TYPE_ID")));
                    sb.setPosmType(dbcursor.getString(dbcursor.getColumnIndexOrThrow("POSM_TYPE")));
                    sb.setRefImage(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Ref_Image")));
                    sb.setPosm_deployment(dbcursor.getString(dbcursor.getColumnIndexOrThrow("DEPLOYED")));
                    sb.setCurrect_reason_Id(dbcursor.getString(dbcursor.getColumnIndexOrThrow("REASON_CD")));
                    sb.setCurrect_reason(dbcursor.getString(dbcursor.getColumnIndexOrThrow("REASON")));
                    sb.setDeployment_img_one(dbcursor.getString(dbcursor.getColumnIndexOrThrow("POSM_ONE_IMG")));
                    sb.setDeployment_img_two(dbcursor.getString(dbcursor.getColumnIndexOrThrow("POSM_TWO_IMG")));
                    sb.setKey_Id(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ID)));
                    if (dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_POSM_REMARK_FLAG)).equals("1")) {
                        sb.setRemarkflag(true);
                    } else {
                        sb.setRemarkflag(false);
                    }
                    sb.setEdittext_remarkfor_others(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_POSM_REMARK)));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("Exception ", "when fetching opening stock!!!!!!!!!!!" + e.toString());
            return list;
        }

        Log.d("Fetching ", "opening stock---------------------->Stop<-----------");
        return list;
    }


    public ArrayList<PosmMaster> getauditchecklist(String store_cd, String visit_date, String common_Id) {
        Log.d("Fetching", "Storedata--------------->Start<------------");
        ArrayList<PosmMaster> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("Select * from " + CommonString.TABLE_STORE_POSM_DATA + " where " + CommonString.KEY_STORE_CD + " ='" + store_cd + "' and VISIT_DATE='" + visit_date + "' and Common_Id='" + common_Id + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    PosmMaster sb = new PosmMaster();
                    sb.setPosmId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow(KEY_POSM_CD)));
                    sb.setChecklist_Id(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_CHECKLIST_ID)));
                    sb.setChecklist(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_CHECKLIST)));
                    sb.setCurrectans_Ic(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ANSWER_Id)));
                    sb.setCurrectans(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ANSWER)));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Log.d("Exception ", "when fetching opening stock!!!!!!!!!!!" + e.toString());
            return list;
        }

        Log.d("Fetching ", "opening stock---------------------->Stop<-----------");
        return list;
    }


    public ArrayList<PosmMaster> getposmchecklistby_posmId(int posm_Id) {
        Log.d("Fetching", "Storedata--------------->Start<------------");
        ArrayList<PosmMaster> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("select distinct PC.Checklist_Id,PC.Checklist from Posm_Checklist PC inner" +
                    " join Mapping_Posm_Checklist MC on PC.Checklist_Id=MC.Checklist_Id where MC.Posm_Id=" + posm_Id + " ", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    PosmMaster sb = new PosmMaster();
                    sb.setChecklist(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Checklist")));
                    sb.setChecklist_Id(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Checklist_Id")));
                    sb.setCurrectans_Ic("0");
                    sb.setCurrectans("");
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Log.d("Exception ", "when fetching opening stock!!!!!!!!!!!" + e.toString());
            Crashlytics.logException(e);
            return list;
        }

        Log.d("Fetching ", "opening stock---------------------->Stop<-----------");
        return list;
    }

    public ArrayList<NonPosmReason> getnonposmreason() {
        ArrayList<NonPosmReason> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("select DISTINCT Preason_Id,Preason,Editable,Remark from Non_Posm_Reason", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    NonPosmReason df = new NonPosmReason();
                    df.setPreasonId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Preason_Id")));
                    df.setPreason(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Preason")));
                    if (dbcursor.getString(dbcursor.getColumnIndexOrThrow("Editable")).equals("1")) {
                        df.setEditable(true);
                    } else {
                        df.setEditable(false);
                    }
                    if (dbcursor.getString(dbcursor.getColumnIndexOrThrow("Remark")).equals("1")) {
                        df.setRemark(true);
                    } else {
                        df.setRemark(false);
                    }
                    list.add(df);

                    dbcursor.moveToNext();
                }

                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
            return list;
        }

        return list;

    }

    public ArrayList<NonPosmReason> getnonposmreason(String checklist_Id) {
        ArrayList<NonPosmReason> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("select distinct Answer_Id,Answer from Posm_Checklist where Checklist_Id='" + checklist_Id + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    NonPosmReason df = new NonPosmReason();
                    df.setPreasonId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Answer_Id")));
                    df.setPreason(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Answer")));
                    list.add(df);

                    dbcursor.moveToNext();
                }

                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
            return list;
        }

        return list;

    }


    public long insertStorePosmListData(String Store_Id, String visit_date, HashMap<PosmMaster, List<PosmMaster>> data, List<PosmMaster> save_listDataHeader) {
        db.delete(CommonString.TABLE_INSERT_POSM_OPENINGHEADER_DATA, CommonString.KEY_STORE_CD + " ='" + Store_Id + "'", null);
        db.delete(CommonString.TABLE_STORE_POSM_DATA, CommonString.KEY_STORE_CD + " ='" + Store_Id + "'", null);
        ContentValues values = new ContentValues();
        ContentValues values1 = new ContentValues();
        long k2 = 0;
        try {
            db.beginTransaction();
            for (int i = 0; i < save_listDataHeader.size(); i++) {
                values.put(CommonString.KEY_STORE_CD, Store_Id);
                values.put(CommonString.KEY_VISIT_DATE, visit_date);
                values.put(KEY_POSM_CD, save_listDataHeader.get(i).getPosmId());
                values.put(CommonString.KEY_POSM, save_listDataHeader.get(i).getPosm());
                values.put(CommonString.KEY_POSM_TYPE_ID, save_listDataHeader.get(i).getPosmTypeId());
                values.put(CommonString.KEY_POSM_TYPE, save_listDataHeader.get(i).getPosmType());
                values.put(CommonString.KEY_POSM_DEPLOYED, save_listDataHeader.get(i).getPosm_deployment());
                values.put(CommonString.KEY_POSM_ONE_IMAGE, save_listDataHeader.get(i).getDeployment_img_one());
                values.put(CommonString.KEY_POSM_TWO_IMAGE, save_listDataHeader.get(i).getDeployment_img_two());
                values.put(CommonString.KEY_POSM_REASON_CD, save_listDataHeader.get(i).getCurrect_reason_Id());
                values.put(CommonString.KEY_POSM_REASON, save_listDataHeader.get(i).getCurrect_reason());
                values.put(CommonString.KEY_POSM_REMARK_FLAG, save_listDataHeader.get(i).isRemarkflag());
                values.put(CommonString.KEY_POSM_REMARK, save_listDataHeader.get(i).getEdittext_remarkfor_others());

                long l = db.insert(CommonString.TABLE_INSERT_POSM_OPENINGHEADER_DATA, null, values);

                for (int j = 0; j < data.get(save_listDataHeader.get(i)).size(); j++) {
                    values1.put("Common_Id", (int) l);
                    values1.put(CommonString.KEY_STORE_CD, Store_Id);
                    values1.put(CommonString.KEY_VISIT_DATE, visit_date);
                    values1.put(KEY_POSM_CD, save_listDataHeader.get(i).getPosmId());
                    values1.put(CommonString.KEY_CHECKLIST_ID, data.get(save_listDataHeader.get(i)).get(j).getChecklist_Id());
                    values1.put(CommonString.KEY_CHECKLIST, data.get(save_listDataHeader.get(i)).get(j).getChecklist());
                    values1.put(CommonString.KEY_ANSWER_Id, data.get(save_listDataHeader.get(i)).get(j).getCurrectans_Ic());
                    values1.put(CommonString.KEY_ANSWER, data.get(save_listDataHeader.get(i)).get(j).getCurrectans());

                    k2 = db.insert(CommonString.TABLE_STORE_POSM_DATA, null, values1);
                }
            }
            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception ex) {
            Crashlytics.logException(ex);
            Log.d("Database Exception", " while Insert Posm Master Data " + ex.toString());
        }
        return k2;
    }

    public ArrayList<PosmMaster> getstoreposminsertedData(String store_Id, String posm_Id) {
        Log.d("Fetching", "Storedata--------------->Start<------------");
        ArrayList<PosmMaster> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT * FROM " + CommonString.TABLE_STORE_POSM_DATA + " WHERE " + CommonString.KEY_STORE_CD + " ='" + store_Id + "' AND " + KEY_POSM_CD + "='" + posm_Id + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    PosmMaster sb = new PosmMaster();
                    sb.setPosmId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow(KEY_POSM_CD)));
                    sb.setChecklist_Id(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_CHECKLIST_ID)));
                    sb.setChecklist(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_CHECKLIST)));
                    sb.setCurrectans_Ic(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ANSWER_Id)));
                    sb.setCurrectans(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ANSWER)));
                    list.add(sb);
                    dbcursor.moveToNext();
                }

                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("Exception ", "when fetching opening stock!!!!!!!!!!!" + e.toString());
            return list;
        }

        Log.d("Fetching ", "opening stock---------------------->Stop<-----------");
        return list;
    }

    public ArrayList<PosmMaster> getstoreposmData(String store_Id) {
        Log.d("Fetching", "Storedata--------------->Start<------------");
        ArrayList<PosmMaster> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT * FROM STORE_POSM_DATA WHERE " + CommonString.KEY_STORE_CD + " ='" + store_Id + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    PosmMaster sb = new PosmMaster();
                    sb.setPosmId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow(KEY_POSM_CD)));
                    sb.setPosm(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_POSM)));
                    sb.setPosmTypeId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow(CommonString.KEY_POSM_TYPE_ID)));
                    sb.setPosmType(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_POSM_TYPE)));
                    sb.setPosm_deployment(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_POSM_DEPLOYED)));
                    sb.setDeployment_img_one(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_POSM_ONE_IMAGE)));
                    sb.setCurrect_reason_Id(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_POSM_REASON_CD)));
                    sb.setCurrect_reason(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_POSM_REASON)));

                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Log.d("Exception ", "when fetching opening stock!!!!!!!!!!!" + e.toString());
            return list;
        }

        Log.d("Fetching ", "opening stock---------------------->Stop<-----------");
        return list;
    }


    public boolean isposm_deployment(String store_Id, String visit_date) {
        boolean filled = false;
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT " + CommonString.KEY_POSM_DEPLOYED + " FROM " + CommonString.TABLE_STORE_DEPLOYMENT_DATA + " WHERE " + CommonString.KEY_STORE_CD + "= '" + store_Id + "' AND " + CommonString.KEY_VISIT_DATE + "='" + visit_date + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    if (dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_POSM_DEPLOYED)).equals("")) {
                        filled = false;
                        break;
                    } else {
                        filled = true;
                    }

                    dbcursor.moveToNext();
                }
                dbcursor.close();
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Records!!!!!!!!!!!!!!!!!!!!!", e.toString());
            return filled;
        }
        return filled;


    }


    public boolean isstore_auditexist(String store_Id) {
        boolean filled = false;
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT POSM_CD FROM " + CommonString.TABLE_STORE_POSM_DATA + " WHERE " + CommonString.KEY_STORE_CD + "= '" + store_Id + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    if (dbcursor.getString(dbcursor.getColumnIndexOrThrow(KEY_POSM_CD)).equals("")) {
                        filled = false;
                        break;
                    } else {
                        filled = true;
                    }
                    dbcursor.moveToNext();
                }
                dbcursor.close();
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Records!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return filled;
        }

        return filled;
    }

    public boolean checknonworkingreasonusingreason_Id(String reason_Id) {
        boolean filled = false;
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("select * from Non_Working_Reason where Reason_Id='" + reason_Id + "'", null);

            if (dbcursor != null) {

                dbcursor.moveToFirst();

                while (!dbcursor.isAfterLast()) {
                    if (dbcursor.getString(dbcursor.getColumnIndexOrThrow("Reason")).equalsIgnoreCase("Present")) {
                        filled = true;
                        break;
                    } else {
                        filled = false;
                    }

                    dbcursor.moveToNext();
                }

                dbcursor.close();

            }

        } catch (Exception e) {
            Log.d("Exception when fetching Records!!!!!!!!!!!!!!!!!!!!!", e.toString());
            return filled;
        }

        return filled;
    }


    public boolean isJCPStoreCheckIn(String date) {
        boolean filled = false;
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * FROM Journey_Plan  " + "WHERE Visit_Date ='" + date + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    if (dbcursor.getString(dbcursor.getColumnIndexOrThrow("Upload_Status")).equalsIgnoreCase(CommonString.KEY_CHECK_IN)
                            || dbcursor.getString(dbcursor.getColumnIndexOrThrow("Upload_Status")).equalsIgnoreCase(CommonString.KEY_P)
                            || dbcursor.getString(dbcursor.getColumnIndexOrThrow("Upload_Status")).equalsIgnoreCase(CommonString.KEY_D)
                            || dbcursor.getString(dbcursor.getColumnIndexOrThrow("Upload_Status")).equalsIgnoreCase(CommonString.KEY_U)
                            || dbcursor.getString(dbcursor.getColumnIndexOrThrow("Upload_Status")).equalsIgnoreCase(CommonString.STORE_STATUS_LEAVE)) {
                        filled = true;
                        break;
                    } else {
                        filled = false;
                    }
                    dbcursor.moveToNext();
                }
                dbcursor.close();
            }
        } catch (Exception e) {
            Log.d("Exception when fetching Records!!!!!!!!!!!!!!!!!!!!!", e.toString());
            return filled;
        }
        return filled;
    }

    public boolean iscurrentdateworkingwithstoreCheckIN(String date) {
        boolean filled = false;
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * FROM Journey_Plan  " + "WHERE Visit_Date ='" + date + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    if (dbcursor.getString(dbcursor.getColumnIndexOrThrow("Last_Visit_Date")).equalsIgnoreCase(date)) {
                        if (dbcursor.getString(dbcursor.getColumnIndexOrThrow("Upload_Status")).equalsIgnoreCase(CommonString.KEY_CHECK_IN)
                                || dbcursor.getString(dbcursor.getColumnIndexOrThrow("Upload_Status")).equalsIgnoreCase(CommonString.KEY_P)
                                || dbcursor.getString(dbcursor.getColumnIndexOrThrow("Upload_Status")).equalsIgnoreCase(CommonString.KEY_D)
                                || dbcursor.getString(dbcursor.getColumnIndexOrThrow("Upload_Status")).equalsIgnoreCase(CommonString.KEY_U)
                                || dbcursor.getString(dbcursor.getColumnIndexOrThrow("Upload_Status")).equalsIgnoreCase(CommonString.STORE_STATUS_LEAVE)) {
                            filled = true;
                            break;
                        }
                    } else {
                        filled = false;
                    }
                    dbcursor.moveToNext();
                }
                dbcursor.close();
            }
        } catch (Exception e) {
            Log.d("Exception when fetching Records!!!!!!!!!!!!!!!!!!!!!", e.toString());
            return filled;
        }
        return filled;
    }


    public boolean iscurrentDSRMoved(String date) {
        boolean filled = false;
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * FROM Journey_Plan  " + "WHERE Visit_Date ='" + date + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    if (dbcursor.getString(dbcursor.getColumnIndexOrThrow("Last_Visit_Date")).equalsIgnoreCase(date)) {
                        if (dbcursor.getString(dbcursor.getColumnIndexOrThrow("Upload_Status")).equalsIgnoreCase(CommonString.KEY_CHECK_IN)) {
                            filled = true;
                            break;
                        }
                    } else {
                        filled = false;
                    }
                    dbcursor.moveToNext();
                }
                dbcursor.close();
            }
        } catch (Exception e) {
            Log.d("Exception when fetching Records!!!!!!!!!!!!!!!!!!!!!", e.toString());
            return filled;
        }
        return filled;
    }


    public boolean isPJPCheckinformainmenu(String date, String store_Id) {
        boolean filled = false;
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * FROM Journey_Plan  " + "WHERE Visit_Date ='" + date + "' AND Store_Id='" + store_Id + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    if (dbcursor.getString(dbcursor.getColumnIndexOrThrow("Upload_Status")).equalsIgnoreCase(CommonString.KEY_CHECK_IN)) {
                        filled = true;
                        break;
                    } else {
                        filled = false;
                    }
                    dbcursor.moveToNext();
                }
                dbcursor.close();
            }
        } catch (Exception e) {
            Log.d("Exception when fetching Records!!!!!!!!!!!!!!!!!!!!!", e.toString());
            return filled;
        }
        return filled;
    }


    public boolean isjcpStoreCheckInwithdentist_Id(String date, int dentist_Id) {
        boolean filled = false;
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * FROM Journey_Plan  " + "WHERE Visit_Date ='" + date + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    if (dbcursor.getString(dbcursor.getColumnIndexOrThrow("Upload_Status")).equalsIgnoreCase(CommonString.KEY_CHECK_IN)) {
                        if (dentist_Id != dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Store_Id"))) {
                            filled = true;
                            break;
                        } else {
                            break;
                        }
                    }
                    dbcursor.moveToNext();
                }
                dbcursor.close();
            }
        } catch (Exception e) {
            Log.d("Exception when fetching Records!!!!!!!!!!!!!!!!!!!!!", e.toString());
            return filled;
        }
        return filled;
    }


    //Deviation
    @SuppressLint("LongLogTag")
    public ArrayList<NonWorkingReason> getNonWorkingDataByFlag(boolean flag, boolean forattendance) {
        Log.d("FetchingAssetdata--------------->Start<------------",
                "------------------");
        ArrayList<NonWorkingReason> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            if (!flag && forattendance) {
                dbcursor = db.rawQuery("SELECT * FROM Non_Working_Reason where For_Attendance='1'", null);
            } else if (!forattendance && flag) {
                dbcursor = db.rawQuery("SELECT * FROM Non_Working_Reason where Entry_Allow='1' and For_Coverage='1'", null);
            } else {
                dbcursor = db.rawQuery("SELECT * FROM Non_Working_Reason where For_Coverage='1'", null);
            }
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    NonWorkingReason sb = new NonWorkingReason();
                    sb.setReasonId(Integer.valueOf(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Reason_Id"))));
                    sb.setReason(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Reason")));
                    String entry_allow = dbcursor.getString(dbcursor.getColumnIndexOrThrow("Entry_Allow"));
                    if (entry_allow.equals("1")) {
                        sb.setEntryAllow(true);
                    } else {
                        sb.setEntryAllow(false);
                    }
                    String image_allow = dbcursor.getString(dbcursor.getColumnIndexOrThrow("Image_Allow"));
                    if (image_allow.equals("1")) {
                        sb.setImageAllow(true);
                    } else {
                        sb.setImageAllow(false);
                    }
                    String gps_mendtry = dbcursor.getString(dbcursor.getColumnIndexOrThrow("GPS_Mandatory"));
                    if (gps_mendtry.equals("1")) {
                        sb.setGPSMandatory(true);
                    } else {
                        sb.setGPSMandatory(false);
                    }
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Non working!!!!!!!!!!!",
                    e.toString());
            return list;
        }

        Log.d("Fetching non working data---------------------->Stop<-----------",
                "-------------------");
        return list;
    }

    public ArrayList<CoverageBean> getCoverageData(String visitdate) {
        ArrayList<CoverageBean> list = new ArrayList<CoverageBean>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT * FROM " + CommonString.TABLE_COVERAGE_DATA + " WHERE " + CommonString.KEY_VISIT_DATE + "='" + visitdate + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CoverageBean sb = new CoverageBean();
                    sb.setStore_Id(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_STORE_CD)));
                    sb.setUserId(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_USER_ID)));
                    sb.setVisitDate(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_VISIT_DATE)));
                    sb.setLatitude(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LATITUDE)));
                    sb.setLongitude(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LONGITUDE)));
                    sb.setImage(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IMAGE)));
                    sb.setReason(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON)));
                    sb.setReasonid(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON_ID)));
                    sb.setMID(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ID))));
                    sb.setRemark(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COVERAGE_REMARK)));
                    sb.setCity_name(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_CITY_NAME)));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Log.d("Exception when fetching Coverage Data!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());

        }

        return list;

    }


    public ArrayList<JourneyPlan> getSpecificStoreDatawithdate(String visit_date, String store_id) {
        ArrayList<JourneyPlan> list = new ArrayList<JourneyPlan>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT * from Journey_Plan  " +
                    "where Visit_Date ='" + visit_date + "' AND Store_Id='" + store_id + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    JourneyPlan sb = new JourneyPlan();
                    sb.setStoreId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Store_Id")));
                    sb.setVisitDate(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Visit_Date")));
                    sb.setDistributor(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Distributor")));
                    sb.setStoreName(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Name")));
                    sb.setAddress((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Address"))));
                    sb.setPincode((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Pincode"))));
                    sb.setContactPerson((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Contact_Person"))));
                    sb.setContactNo((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Contact_No"))));
                    sb.setMobileNo((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Mobile_No"))));
                    sb.setStoreEmail((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Email"))));
                    sb.setVisitingCardPic((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Visiting_Card_Pic"))));

                    sb.setLatitude((dbcursor.getDouble(dbcursor.getColumnIndexOrThrow("Latitude"))));
                    sb.setLongitude((dbcursor.getDouble(dbcursor.getColumnIndexOrThrow("Longitude"))));
                    sb.setGeoTag((dbcursor.getString(dbcursor.getColumnIndexOrThrow("GeoTag"))));
                    sb.setAgeing_Of_Branding((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Ageing_Of_Branding"))));
                    sb.setCity(dbcursor.getString(dbcursor.getColumnIndexOrThrow("City")));
                    sb.setStoreCategory(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Category")));
                    sb.setStoreCategoryId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Store_Category_Id")));
                    sb.setStoreType(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Type")));
                    sb.setStoreTypeId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Store_Type_Id")));
                    sb.setDistributorId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Distributor_Id")));
                    sb.setUploadStatus(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Upload_Status")));
                    sb.setLastVisitDate(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Last_Visit_Date")));
                    sb.setStateId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("State_Id")));
                    sb.setRegionId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Region_Id")));

                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception get JCP!", e.toString());
            return list;
        }


        return list;
    }


    public ArrayList<CoverageBean> getcoverageDataPrevious(String visitdate) {
        ArrayList<CoverageBean> list = new ArrayList<CoverageBean>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * from " + CommonString.TABLE_COVERAGE_DATA + " where " + CommonString.KEY_VISIT_DATE + "<>'" + visitdate + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CoverageBean sb = new CoverageBean();
                    sb.setStore_Id(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_STORE_CD)));
                    sb.setUserId(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_USER_ID)));
                    sb.setVisitDate(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_VISIT_DATE)));
                    sb.setLatitude(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LATITUDE)));
                    sb.setLongitude(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LONGITUDE)));
                    sb.setImage(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IMAGE)));
                    sb.setReason(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON)));
                    sb.setReasonid(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON_ID)));
                    sb.setMID(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ID))));
                    sb.setRemark(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COVERAGE_REMARK)));
                    sb.setCity_name(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_CITY_NAME)));


                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Log.d("Exception when fetching Coverage Data!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            Crashlytics.logException(e);
        }

        return list;

    }

    public JourneyPlan getSpecificStoreDataPrevious(String date, String store_cd) {
        JourneyPlan sb = new JourneyPlan();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT * from Journey_Plan  " + "where Visit_Date <> '" + date + "' AND Store_Id='" + store_cd + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    sb.setStoreId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Store_Id")));
                    sb.setVisitDate(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Visit_Date")));
                    sb.setDistributor(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Distributor")));
                    sb.setStoreName(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Name")));
                    sb.setAddress((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Address"))));
                    sb.setPincode((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Pincode"))));
                    sb.setContactPerson((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Contact_Person"))));
                    sb.setContactNo((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Contact_No"))));
                    sb.setMobileNo((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Mobile_No"))));
                    sb.setStoreEmail((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Email"))));
                    sb.setVisitingCardPic((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Visiting_Card_Pic"))));
                    sb.setLatitude((dbcursor.getDouble(dbcursor.getColumnIndexOrThrow("Latitude"))));
                    sb.setLongitude((dbcursor.getDouble(dbcursor.getColumnIndexOrThrow("Longitude"))));
                    sb.setGeoTag((dbcursor.getString(dbcursor.getColumnIndexOrThrow("GeoTag"))));
                    sb.setCity(dbcursor.getString(dbcursor.getColumnIndexOrThrow("City")));
                    sb.setStoreCategory(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Category")));
                    sb.setStoreCategoryId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Store_Category_Id")));
                    sb.setStoreType(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Type")));
                    sb.setStoreTypeId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Store_Type_Id")));
                    sb.setDistributorId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Distributor_Id")));
                    sb.setUploadStatus(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Upload_Status")));
                    sb.setLastVisitDate(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Last_Visit_Date")));
                    sb.setStateId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("State_Id")));
                    sb.setRegionId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Region_Id")));
                    sb.setAgeing_Of_Branding((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Ageing_Of_Branding"))));

                    dbcursor.moveToNext();

                }
                dbcursor.close();
                return sb;
            }

        } catch (Exception e) {
            Log.d("Exception get JCP!", e.toString());
            Crashlytics.logException(e);
            return sb;

        }

        return sb;
    }


    public long insertAttendenceData(String user_id, String visit_date, String image, String reason,
                                     String reason_cd, String image_allow) {
        db.delete(CommonString.TABLE_INSERT_MERCHANDISER_ATTENDENCE_TABLE, null, null);
        ContentValues values = new ContentValues();
        long l = 0;
        try {
            values.put(CommonString.KEY_USER_ID, user_id);
            values.put(CommonString.KEY_VISIT_DATE, visit_date);
            values.put(CommonString.KEY_IMAGE, image);
            values.put(CommonString.KEY_REASON, reason);
            values.put(CommonString.KEY_REASON_ID, reason_cd);
            values.put(CommonString.KEY_IMAGE_ALLOW, image_allow);
            l = db.insert(CommonString.TABLE_INSERT_MERCHANDISER_ATTENDENCE_TABLE, null, values);

        } catch (Exception e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        }
        return l;
    }

    public long InsertSTOREgeotag(String Dietician_Id, double lat, double longitude, String path, String status) {
        db.delete(CommonString.TABLE_STORE_GEOTAGGING, CommonString.KEY_STORE_CD + "='" + Dietician_Id + "'", null);
        ContentValues values = new ContentValues();
        long l = 0;
        try {
            values.put(CommonString.KEY_STORE_CD, Dietician_Id);
            values.put("LATITUDE", Double.toString(lat));
            values.put("LONGITUDE", Double.toString(longitude));
            values.put("FRONT_IMAGE", path);
            values.put(CommonString.KEY_GEO_TAG_STATUS, status);
            l = db.insert(CommonString.TABLE_STORE_GEOTAGGING, null, values);

        } catch (Exception ex) {
            Log.d("Database Exception ", ex.toString());
            return 0;
        }
        return l;
    }


    public ArrayList<GeotaggingBeans> getinsertGeotaggingData(String Dietician_Id, String status) {
        ArrayList<GeotaggingBeans> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("Select * from " + CommonString.TABLE_STORE_GEOTAGGING + "" +
                    " where " + CommonString.KEY_STORE_CD + " ='" + Dietician_Id + "' and " + CommonString.KEY_GEO_TAG_STATUS + " = '" + status + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    GeotaggingBeans geoTag = new GeotaggingBeans();
                    geoTag.setStore_Id(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_STORE_CD)));
                    geoTag.setLatitude(Double.parseDouble(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LATITUDE))));
                    geoTag.setLongitude(Double.parseDouble(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LONGITUDE))));
                    geoTag.setImage(dbcursor.getString(dbcursor.getColumnIndexOrThrow("FRONT_IMAGE")));
                    list.add(geoTag);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception Brands",
                    e.toString());
            return list;
        }
        return list;

    }

    public long updateInsertedGeoTagStatus(String id, String status) {
        ContentValues values = new ContentValues();
        long l = 0;
        try {
            values.put(CommonString.KEY_GEO_TAG_STATUS, status);
            l = db.update(CommonString.TABLE_STORE_GEOTAGGING, values, CommonString.KEY_STORE_CD + "='" + id + "'", null);
        } catch (Exception ex) {
            return 0;
        }
        return l;
    }

    public long updateStatus(String id, String status) {
        ContentValues values = new ContentValues();
        long l = 0;
        try {
            values.put("GeoTag", status);
            l = db.update("Journey_Plan", values, "Store_Id='" + id + "'", null);
        } catch (Exception ex) {
        }
        return l;
    }

    public long insertStoreProfileData(String Dietician_Id, String visit_date, StoreProfileGetterSetter save_listDataHeader) {
        db.delete(CommonString.TABLE_STORE_PROFILE_DATA, CommonString.KEY_STORE_CD + "='" + Dietician_Id + "' AND VISIT_DATE='" + visit_date + "'", null);
        ContentValues values = new ContentValues();
        long l2 = 0;
        try {
            db.beginTransaction();
            values.put(CommonString.KEY_STORE_CD, Dietician_Id);
            values.put(CommonString.KEY_VISIT_DATE, visit_date);
            values.put(CommonString.KEY_STORE_NAME, save_listDataHeader.getStore_name());
            values.put(CommonString.KEY_STORE_PROFILE_STORE_ADDRESS, save_listDataHeader.getProfileAddress());
            values.put(CommonString.KEY_STORE_PROFILE_CITY, save_listDataHeader.getProfileCity());
            values.put(CommonString.KEY_CLINIC_PIN_CODE, save_listDataHeader.getPin_Code());
            values.put(CommonString.KEY_CLINIC_EMAIL_ID, save_listDataHeader.getEmail_Id());
            values.put(CommonString.KEY_STORE_PROFILE_CONTACT_NO, save_listDataHeader.getProfileContact());
            values.put(CommonString.KEY_RETAILER_NAME, save_listDataHeader.getRetailer_name());
            values.put(CommonString.KEY_ageingof_branding, save_listDataHeader.getAgeingof_branding());
            values.put(CommonString.KEY_CATEGORY, save_listDataHeader.getCategory_name());
            values.put(CommonString.KEY_SEGMENT, save_listDataHeader.getSegment());
            values.put(CommonString.KEY_DISTRIBUTOR, save_listDataHeader.getDistributor());
            values.put(CommonString.KEY_VISITING_IMG, save_listDataHeader.getVisiting_card());
            values.put(CommonString.KEY_REMARK, save_listDataHeader.getProfile_remark());

            l2 = db.insert(CommonString.TABLE_STORE_PROFILE_DATA, null, values);

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception e) {
            Log.d("Database Exception", " while Insert Header Data " + e.toString());
        }
        return l2;
    }

    public StoreProfileGetterSetter getStoreProfileData(String store_Id, String visit_date) {
        StoreProfileGetterSetter sb = new StoreProfileGetterSetter();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT * FROM STORE_PROFILE_DATA WHERE " + CommonString.KEY_STORE_CD + " ='" + store_Id + "' AND VISIT_DATE='" + visit_date + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    sb.setStore_name(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_STORE_NAME)));
                    sb.setProfileAddress((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_STORE_PROFILE_STORE_ADDRESS))));
                    sb.setProfileCity(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_STORE_PROFILE_CITY)));
                    sb.setPin_Code(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_CLINIC_PIN_CODE)));
                    sb.setEmail_Id(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_CLINIC_EMAIL_ID)));
                    sb.setProfileContact(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_STORE_PROFILE_CONTACT_NO)));
                    sb.setRetailer_name(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_RETAILER_NAME)));
                    sb.setAgeingof_branding(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ageingof_branding)));
                    sb.setCategory_name(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_CATEGORY)));
                    sb.setSegment(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_SEGMENT)));
                    sb.setDistributor(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_DISTRIBUTOR)));
                    sb.setVisiting_card(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_VISITING_IMG)));
                    sb.setProfile_remark(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REMARK)));
                    sb.setKey_Id(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ID)));

                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return sb;
            }

        } catch (Exception e) {
            Log.d("Exception get JCP!", e.toString());
            return sb;
        }

        return sb;
    }

    public long insertdeploymentdata(String store_Id, String visit_date, ArrayList<PosmMaster> deploymList) {
        db.delete(CommonString.TABLE_STORE_DEPLOYMENT_DATA, CommonString.KEY_STORE_CD + " ='" + store_Id + "'and " + CommonString.KEY_VISIT_DATE + "='" + visit_date + "'", null);
        ContentValues values1 = new ContentValues();
        long l2 = 0;
        try {
            db.beginTransaction();
            for (int i = 0; i < deploymList.size(); i++) {
                values1.put(CommonString.KEY_STORE_CD, store_Id);
                values1.put(CommonString.KEY_VISIT_DATE, visit_date);
                values1.put(KEY_POSM_CD, deploymList.get(i).getPosmId().toString());
                values1.put(CommonString.KEY_POSM, deploymList.get(i).getPosm());
                values1.put(CommonString.KEY_POSM_DEPLOYED, deploymList.get(i).getPosm_deployment());
                values1.put(CommonString.KEY_POSM_ONE_IMAGE, deploymList.get(i).getDeployment_img_one());
                values1.put(CommonString.KEY_POSM_TWO_IMAGE, deploymList.get(i).getDeployment_img_two());
                l2 = db.insert(CommonString.TABLE_STORE_DEPLOYMENT_DATA, null, values1);
            }

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception e) {
            Log.d("Database Exception", " while Insert Posm Master Data " + e.toString());
        }
        return l2;
    }

    public ArrayList<PosmMaster> getinserteddeployment(String store_cd, String visit_date) {
        Log.d("Fetching", "Storedata--------------->Start<------------");
        ArrayList<PosmMaster> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("select * from " + CommonString.TABLE_STORE_DEPLOYMENT_DATA + " where " + CommonString.KEY_STORE_CD + "='" + store_cd + "' and VISIT_DATE='" + visit_date + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    PosmMaster sb = new PosmMaster();
                    sb.setPosmId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow(CommonString.KEY_POSM_CD)));
                    sb.setPosm(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_POSM)));
                    sb.setPosm_deployment(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_POSM_DEPLOYED)));
                    sb.setDeployment_img_one(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_POSM_ONE_IMAGE)));
                    sb.setDeployment_img_two(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_POSM_TWO_IMAGE)));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("Exception ", "when fetching opening stock!!!!!!!!!!!" + e.toString());
            return list;
        }

        Log.d("Fetching ", "opening stock---------------------->Stop<-----------");
        return list;
    }


}
