package com.killersite.roo.addon.ang;

import org.springframework.roo.metadata.MetadataIdentificationUtils;
import org.springframework.roo.metadata.MetadataItem;
import org.springframework.roo.metadata.MetadataNotificationListener;
import org.springframework.roo.metadata.MetadataProvider;

/**
 * Created by Ben on 5/9/2014.
 */
public class AngMetadataListener implements MetadataProvider, MetadataNotificationListener {
    @Override
    public void notify(String upstreamDependency, String downstreamDependency) {
        if (MetadataIdentificationUtils.isIdentifyingClass(downstreamDependency)) {
            // A physical Java type has changed, and determine what the
            // corresponding local metadata identification string would have been
        }
    }

    @Override
    public MetadataItem get(String metadataIdentificationString) {
        return null;
    }

    @Override
    public String getProvidesType() {
        return null;
    }

}
