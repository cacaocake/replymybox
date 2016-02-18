package com.ebaykorea.escrow.replymybox.model;

import com.strongloop.android.loopback.ModelRepository;

public class BoxRepository extends ModelRepository<BoxModel> {
    public BoxRepository() {
        super("box", "box", BoxModel.class);
    }
}