package it.schmid.android.mofa.model;


import android.util.Log;

import com.j256.ormlite.field.DatabaseField;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.schmid.android.mofa.MofaApplication;
import it.schmid.android.mofa.NotificationService;
import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.interfaces.ProductInterface;

public class Pesticide extends ImportBehavior implements ProductInterface {
    private static final String TAG = "PesticideClass";
    private static final int SHOWINFO = 1;
    @DatabaseField(id = true)

    private Integer id;
    @DatabaseField(index = true)
    private Integer regNumber;
    @DatabaseField(index = true)
    private String productName;
    @DatabaseField
    private Double defaultDose;
    @DatabaseField
    private String code;
    @DatabaseField
    private String constraints;
    @DatabaseField
    private String data;
    @DatabaseField
    private String status;
    private Boolean importError = false;

    public Integer getId() {
        return id;
    }


    public void setId(Integer id) {
        this.id = id;
    }


    public Integer getRegNumber() {
        return regNumber;
    }


    public void setRegNumber(Integer regNumber) {
        this.regNumber = regNumber;
    }


    public String getProductName() {
        return productName;
    }


    public void setProductName(String productName) {
        this.productName = productName;
    }


    public Double getDefaultDose() {
        return defaultDose;
    }


    public int showInfo() {
        return SHOWINFO;
    }


    public void setDefaultDose(Double defaultDose) {
        this.defaultDose = defaultDose;
    }

    public String getCode() {
        return code;
    }


    public void setCode(String code) {
        this.code = code;
    }

    public String getConstraints() {
        return constraints;
    }

    public void setConstraints(String constraints) {
        this.constraints = constraints;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return productName;
    }


    @Override
    public Boolean importMasterData(String xmlString, NotificationService notification) {
        return importError;
    }
}
