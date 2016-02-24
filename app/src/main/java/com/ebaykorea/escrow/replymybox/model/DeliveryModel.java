package com.ebaykorea.escrow.replymybox.model;

/**
 * Created by cacao on 2016. 2. 24..
 */
public class DeliveryModel {
    private String buyerid;
    private String boxid;
    private String id;

    public String getBuyerid() {
        return buyerid;
    }

    public void setBuyerid(String buyerid) {
        this.buyerid = buyerid;
    }

    public String getBoxid() {
        return boxid;
    }

    public void setBoxid(String boxid) {
        this.boxid = boxid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
