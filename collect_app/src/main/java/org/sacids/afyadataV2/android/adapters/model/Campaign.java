package org.sacids.afyadataV2.android.adapters.model;

import org.parceler.Parcel;

/**
 * Created by administrator on 13/09/2017.
 */
@Parcel
public class Campaign {
    long id;
    String title;
    String type;
    String jrFormId;
    String description;
    String icon;

    public Campaign() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getJrFormId() {
        return jrFormId;
    }

    public void setJrFormId(String jrFormId) {
        this.jrFormId = jrFormId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
