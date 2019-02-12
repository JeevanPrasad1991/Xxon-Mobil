package jp.cpm.com.xxonmobil.constant;

import android.os.Environment;

/**
 * Created by jeevanp on 14-12-2017.
 */

public class CommonString {
    //preference
    public static final String KEY_USERNAME = "USERNAME";
    public static final String KEY_PASSWORD = "PASSWORD";
    public static final String KEY_STATUS = "STATUS";
    public static final String KEY_DATE = "DATE";
    public static final int CAPTURE_MEDIA = 131;
    public static final String KEY_REPORT_FLAG = "REPORT_FLAG";
    public static final String KEY_YYYYMMDD_DATE = "yyyymmddDate";
    public static final String KEY_STOREVISITED_STATUS = "STOREVISITED_STATUS";
    public static String URL = "http://em.parinaam.in/webservice/ExxonMobil.svc/";
    public static String URLGORIMAG = "http://em.parinaam.in/webservice/Imageupload.asmx/";
    public static final String KEY_ATTENDENCE_STATUS = "ATTENDENCE_STATUS";
    public static final String KEY_PATH = "PATH";
    public static final String KEY_VERSION = "APP_VERSION";
    public static final String KEY_SUCCESS = "Success";
    public static final String KEY_FAILURE = "Failure";
    public static final String MESSAGE_INTERNET_NOT_AVALABLE = "No Internet Connection.Please Check Your Network Connection";
    public static final String MESSAGE_EXCEPTION = "Problem Occured : Report The Problem To Parinaam ";
    public static final String MESSAGE_SOCKETEXCEPTION = "Network Communication Failure. Please Check Your Network Connection";
    public static final String MESSAGE_INVALID_JSON = "Problem Occured while parsing Json : invalid json data";
    public static final String KEY_P = "P";
    public static final String KEY_D = "D";
    public static final String KEY_U = "U";
    public static final String KEY_C = "C";
    public static final String KEY_Y = "Y";
    public static final String STORE_STATUS_LEAVE = "L";
    public static final String KEY_CHECK_IN = "I";
    public static final String KEY_STATUS_N = "N";
    ///all service key
    public static final String KEY_LOGIN_DETAILS = "LoginDetaillatest";
    public static final String KEY_DOWNLOAD_INDEX = "download_Index";
    public static final int TAG_FROM_CURRENT = 1;
    public static final int DOWNLOAD_ALL_SERVICE = 2;
    public static final int COVERAGE_DETAIL = 3;
    public static final int UPLOADJsonDetail = 5;
    //File Path
    public static final String BACKUP_FILE_PATH = Environment.getExternalStorageDirectory() + "/XxonMobil_backup/";
    ////for insert data key
    public static final String FILE_PATH = Environment.getExternalStorageDirectory() + "/.XxonMobil_Images/";
    public static final String ONBACK_ALERT_MESSAGE = "Unsaved data will be lost - Do you want to continue?";
    public static final String KEY_USER_TYPE = "RIGHTNAME";
    public static final String KEY_DEVIATION_NONWORKING = "D_NONWORKING";
    //jeevan
    public static final String DATA_DELETE_ALERT_MESSAGE = "Saved data will be lost - Do you want to continue?";
    public static final String KEY_STORE_NAME = "STORE_NAME";
    public static final String KEY_STORE_CD = "STORE_CD";
    public static final String KEY_STORE_CATEGORY_Id = "Store_Category_Id";
    public static final String KEY_STATE_ID = "STATE_ID";
    public static final String KEY_REGION_ID = "REGION_ID";
    public static final String KEY_VISIT_DATE = "VISIT_DATE";
    public static final String KEY_LATITUDE = "LATITUDE";
    public static final String KEY_LONGITUDE = "LONGITUDE";
    public static final String KEY_REASON_ID = "REASON_ID";
    public static final String KEY_REASON = "REASON";
    public static final String KEY_IMAGE = "STORE_IMAGE";
    public static final String KEY_COVERAGE_REMARK = "REMARK";
    public static final String KEY_USER_ID = "USER_ID";
    public static final String KEY_ID = "ID";
    public static final String KEY_POSM_CD = "POSM_CD";
    public static final String KEY_POSM = "POSM";
    public static final String KEY_POSM_TYPE_ID = "POSM_TYPE_ID";
    public static final String KEY_POSM_TYPE = "POSM_TYPE";
    public static final String KEY_POSM_DEPLOYED = "DEPLOYED";
    public static final String KEY_POSM_ONE_IMAGE = "POSM_ONE_IMG";
    public static final String KEY_POSM_TWO_IMAGE = "POSM_TWO_IMG";
    public static final String KEY_POSM_REASON_CD = "REASON_CD";
    public static final String KEY_POSM_REASON = "REASON";
    public static final String KEY_POSM_REMARK_FLAG = "REMARK_FLAG";
    public static final String KEY_POSM_REMARK = "REMARK";


    public static final String KEY_CHECKLIST_ID = "CHECKLIST_Id";
    public static final String KEY_CHECKLIST = "CHECKLIST";
    public static final String KEY_ANSWER_Id = "ANSWER_ID";
    public static final String KEY_ANSWER = "ANSWER";

    public static final String KEY_CITY_NAME = "CITY_NAME";

    public static final String KEY_DSR_NAME = "DSR_NAME";
    public static final String KEY_JOURNEY_PLAN = "Journey_Plan";
    public static final String KEY_JOURNEY_PLAN_AUDIT = "Journey_Plan_Audit";
    public static final String KEY_MAPPING_POSM = "Mapping_Posm";
    public static final String KEY_UPLOADED_POSM_DATA = "Last_Visit_Posm_Execution";

    public static final String TABLE_COVERAGE_DATA = "COVERAGE_DATA";
    public static final String CREATE_TABLE_COVERAGE_DATA = "CREATE TABLE  IF NOT EXISTS "
            + TABLE_COVERAGE_DATA + " (" + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT ," + KEY_STORE_CD
            + " INTEGER,USER_ID VARCHAR, "
            + KEY_VISIT_DATE + " VARCHAR,"
            + KEY_LATITUDE + " VARCHAR," + KEY_LONGITUDE + " VARCHAR,"
            + KEY_IMAGE + " VARCHAR,"
            + KEY_REASON_ID + " INTEGER,"
            + KEY_COVERAGE_REMARK + " VARCHAR,"
            + KEY_CITY_NAME + " VARCHAR,"
            + KEY_REASON + " VARCHAR)";


    public static final String TABLE_INSERT_POSM_OPENINGHEADER_DATA = "POSM_HEADER_DATA";

    public static final String CREATE_TABLE_POSM_OPENINGHEADER_DATA = "CREATE TABLE  IF NOT EXISTS "
            + TABLE_INSERT_POSM_OPENINGHEADER_DATA + " (" + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + KEY_VISIT_DATE + " VARCHAR,"
            + KEY_STORE_CD + " INTEGER, "
            + KEY_POSM_CD + " INTEGER,"
            + KEY_POSM + " VARCHAR,"
            + KEY_POSM_TYPE_ID + " INTEGER,"
            + KEY_POSM_TYPE + " VARCHAR,"
            + KEY_POSM_DEPLOYED + " INTEGER,"
            + KEY_POSM_ONE_IMAGE + " VARCHAR,"
            + KEY_POSM_TWO_IMAGE + " VARCHAR,"
            + KEY_POSM_REASON_CD + " INTEGER, "
            + KEY_POSM_REMARK_FLAG + " INTEGER,"
            + KEY_POSM_REMARK + " VARCHAR,"
            + KEY_POSM_REASON + " VARCHAR)";



    public static final String TABLE_STORE_POSM_DATA = "POSM_DATA";

    public static final String CREATE_TABLE_STORE_POSM_DATA = "CREATE TABLE  IF NOT EXISTS "
            + TABLE_STORE_POSM_DATA + " (" + "Common_Id" + " INTEGER  ,"
            + KEY_VISIT_DATE + " VARCHAR,"
            + KEY_STORE_CD + " INTEGER, "
            + KEY_POSM_CD + " INTEGER,"
            + KEY_CHECKLIST_ID + " INTEGER, "
            + KEY_CHECKLIST + " VARCHAR, "
            + KEY_ANSWER_Id + " INTEGER, "
            + KEY_ANSWER + " VARCHAR)";

    public static final String MESSAGE_CHANGED = "Invalid UserId Or Password / Password Has Been Changed.";
    public static final String MESSAGE_LOGIN_NO_DATA = "Data mapping error.";
    public static final String KEY_NOTICE_BOARD_LINK = "NOTICE_BOARD_LINK";

    ///FOR VISIBILITY.....

    public static final String KEY_IMAGE_ALLOW = "IMAGE_ALLOW";

    public static final String TABLE_INSERT_MERCHANDISER_ATTENDENCE_TABLE = "MERCHANDISER_ATTENDENCE_TABLE";

    public static final String CREATE_TABLE_INSERT_MERCHANDISER_ATTENDENCE_TABLE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_INSERT_MERCHANDISER_ATTENDENCE_TABLE
            + " ("
            + "KEY_ID"
            + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + KEY_USER_ID
            + " VARCHAR,"
            + KEY_VISIT_DATE
            + " VARCHAR,"
            + KEY_IMAGE
            + " VARCHAR,"
            + KEY_REASON
            + " VARCHAR,"
            + KEY_REASON_ID
            + " INTEGER,"
            + KEY_IMAGE_ALLOW + " INTEGER)";


    public static final String KEY_GEO_TAG_STATUS = "GEO_TAG";

    public static final String TAG_OBJECT = "OBJECT";
    public static final String TABLE_STORE_GEOTAGGING = "STORE_GEOTAGGING";
    public static final String CREATE_TABLE_STORE_GEOTAGGING = "CREATE TABLE IF NOT EXISTS "
            + TABLE_STORE_GEOTAGGING
            + " ("
            + "KEY_ID"
            + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + KEY_STORE_CD
            + " INTEGER,"
            + "LATITUDE"
            + " VARCHAR,"
            + "LONGITUDE"
            + " VARCHAR,"
            + KEY_GEO_TAG_STATUS
            + " VARCHAR,"
            + "FRONT_IMAGE" + " VARCHAR)";


    //for store profile
    public static final String for_clinic_address = "Please fill Dentist Clinic Address";
    public static final String forclinic_name = "Please fill Store Name.";
    public static final String for_retailer_name = "Please fill Retailer Name.";
    public static final String for_ageingof_branding = "Please fill Ageing of branding.";
    public static final String foremail_Id = "Please fill Email Id";
    public static final String forValidEmail = "Please fill Valid Email Id";
    public static final String stpcontactnolenght = "Please fill atleast 10 digit contact number";
    public static final String formobilenumber = "Please fill Dentist Mobile Number";
    public static final String forpincode = " Please fill Area Pin Code";
    public static final String forcity = " Please fill Store City Name";
    public static final String forvalidPinCode = "Please fill Valid Pin Code";
    public static final String for_visiting_card = "Please Capture Visiting Card /Bills Image.";
    public static final String for_remark = "Please fill remark";


    //key for user profile
    public static final String KEY_STORE_PROFILE_STORE_ADDRESS = "ADDRESS";
    public static final String KEY_STORE_PROFILE_CITY = "CITY";
    public static final String KEY_STORE_PROFILE_CONTACT_NO = "CONTACT_NO";
    public static final String KEY_CLINIC_PIN_CODE = "PIN_CODE";
    public static final String KEY_CLINIC_EMAIL_ID = "EMAIL_ID";
    public static final String KEY_RETAILER_NAME = "RETAILER";
    public static final String KEY_ageingof_branding= "AGEINGOF_BRANDING";
    public static final String KEY_VISITING_IMG = "VISITING_IMG";
    public static final String KEY_SEGMENT = "SEGMENT";
    public static final String KEY_CATEGORY = "CATEGORY";
    public static final String KEY_DISTRIBUTOR = "DISTRIBUTOR";
    public static final String KEY_REMARK = "REMARK";


    public static final String TABLE_STORE_PROFILE_DATA = "STORE_PROFILE_DATA";
    public static final String CREATE_TABLE_STORE_PROFILE_DATA = "CREATE TABLE  IF NOT EXISTS "
            + TABLE_STORE_PROFILE_DATA + " (" + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + KEY_STORE_CD + " INTEGER, "
            + KEY_VISIT_DATE + " VARCHAR,"
            + KEY_STORE_NAME + " VARCHAR,"
            + KEY_STORE_PROFILE_STORE_ADDRESS + " VARCHAR,"
            + KEY_STORE_PROFILE_CITY + " VARCHAR,"
            + KEY_CLINIC_PIN_CODE + " INTEGER,"
            + KEY_CLINIC_EMAIL_ID + " VARCHAR,"
            + KEY_STORE_PROFILE_CONTACT_NO + " VARCHAR,"
            + KEY_RETAILER_NAME + " VARCHAR,"
            + KEY_ageingof_branding + " VARCHAR,"
            + KEY_CATEGORY + " VARCHAR,"
            + KEY_SEGMENT + " VARCHAR,"
            + KEY_DISTRIBUTOR + " VARCHAR,"
            + KEY_VISITING_IMG + " VARCHAR,"
            + KEY_REMARK + " VARCHAR)";


    public static final String TABLE_STORE_DEPLOYMENT_DATA = "STORE_DEPLOYMENT_TABLE";
    public static final String CREATE_TABLE_STORE_DEPLOYMENT_DATA = "CREATE TABLE  IF NOT EXISTS "
            + TABLE_STORE_DEPLOYMENT_DATA + " (" + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + KEY_STORE_CD + " INTEGER,"
            + KEY_VISIT_DATE + " VARCHAR,"
            + KEY_POSM_CD + " INTEGER,"
            + KEY_POSM + " VARCHAR,"
            + KEY_POSM_DEPLOYED + " INTEGER,"
            + KEY_POSM_ONE_IMAGE + " VARCHAR,"
            + KEY_POSM_TWO_IMAGE + " VARCHAR)";
}
