package com.elmoselhy.ahmed.agp;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by ahmedelmoselhy on 9/14/2017.
 */

public class Post {
    String detail;
    String doctorName;
    String subject;

    public String getImage() {
        return doctorimage;
    }

    String doctorimage;
    HashMap<String, Object> timestampCreated = new HashMap<>();

    public Post() {
    }

    public Post(String detail, String doctorName, String subject, HashMap<String, Object> timestampCreated) {
        this.detail = detail;
        this.doctorName = doctorName;
        this.subject = subject;
        this.timestampCreated = timestampCreated;
        HashMap<String, Object> timestampNowObject = new HashMap<String, Object>();
        timestampNowObject.put(ConstantsAGP.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
    }

    public Post(String doctorimage, String detail, String doctorName, String subject, HashMap<String, Object> timestampCreated) {
        this.detail = detail;
        this.doctorName = doctorName;
        this.subject = subject;
        this.doctorimage = doctorimage;
        this.timestampCreated = timestampCreated;
        HashMap<String, Object> timestampNowObject = new HashMap<String, Object>();
        timestampNowObject.put(ConstantsAGP.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
    }

    public void setDoctorimage(String doctorimage) {
        this.doctorimage = doctorimage;
    }

    public HashMap<String, Object> getTimestampCreated() {
        return timestampCreated;
    }

    @Exclude
    public String getTimestampCreatedLong() {
        return  getDate((long) timestampCreated.get("timestamp"), "dd/MM/yyyy hh:mm");
    }

    public void setTimestampCreated(HashMap<String, Object> timestampCreated) {
        this.timestampCreated = timestampCreated;
    }


    public String getDetail() {
        return detail;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getSubject() {
        return subject;
    }

    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
}
