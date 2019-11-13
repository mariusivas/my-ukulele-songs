package com.mivas.myukulelesongs.rest.model;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "uc", strict = false)
public class ChordsXml {

    @ElementList(inline = true)
    private List<ChordXml> chords;

    public List<ChordXml> getChords() {
        return chords;
    }
}