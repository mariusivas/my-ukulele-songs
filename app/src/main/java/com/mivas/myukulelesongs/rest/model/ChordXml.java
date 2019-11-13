package com.mivas.myukulelesongs.rest.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name="chord", strict = false)
public class ChordXml {

    @Element(name = "chord_name")
    private String name;

    @Element(name = "chord_diff")
    private String difficulty;

    @Element(name = "chord_diag")
    private String diagram;

    @Element(name = "chord_diag_mini")
    private String miniDiagram;

    @Element(name = "chord_photo")
    private String photo;

    @Element(name = "chord_url")
    private String url;

    public String getName() {
        return name;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getDiagram() {
        return diagram;
    }

    public String getMiniDiagram() {
        return miniDiagram;
    }

    public String getPhoto() {
        return photo;
    }

    public String getUrl() {
        return url;
    }
}