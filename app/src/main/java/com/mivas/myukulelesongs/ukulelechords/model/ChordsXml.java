package com.mivas.myukulelesongs.ukulelechords.model;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Java class representing a response from ukulele-chords.com
 */
@Root(name = "uc", strict = false)
public class ChordsXml {

    @ElementList(inline = true)
    private List<ChordXml> chords;

    public List<ChordXml> getChords() {
        return chords;
    }
}