package com.ebaykorea.escrow.replymybox.model;

import com.strongloop.android.loopback.Model;

/**
 * Created by cacao on 2016. 2. 18..
 */
public class LocationModel extends Model {
    private String memberid;
    private String date;
    private String longitude;

    public String getMemberid() {
        return memberid;
    }

    public void setMemberid(String memberid) {
        this.memberid = memberid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    private String latitude;

}
