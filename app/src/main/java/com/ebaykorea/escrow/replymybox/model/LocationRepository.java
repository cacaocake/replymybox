package com.ebaykorea.escrow.replymybox.model;

import com.strongloop.android.loopback.ModelRepository;

/**
 * Created by cacao on 2016. 2. 18..
 */
public class LocationRepository extends ModelRepository<LocationModel> {
    public LocationRepository() {
        super("location", "location", LocationModel.class);
    }
}