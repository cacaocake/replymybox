package com.ebaykorea.escrow.replymybox.model;

import com.strongloop.android.loopback.Model;
import com.strongloop.android.loopback.ModelRepository;

public class BoxModel extends Model {
    private String boxid;
    private String memberid;
    private String insdate;

    public String getBoxid() {
        return boxid;
    }

    public void setBoxid(String boxid) {
        this.boxid = boxid;
    }

    public String getMemberid() {
        return memberid;
    }

    public void setMemberid(String memberid) {
        this.memberid = memberid;
    }

    public String getInsdate() {
        return insdate;
    }

    public void setInsdate(String insdate) {
        this.insdate = insdate;
    }


}


