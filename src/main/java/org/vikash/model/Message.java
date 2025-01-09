package org.vikash.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Message {

    private String source;
    private String instrument;
    private List<LPData> lpDataList = new ArrayList<>();

    public Message(String source, String instrument, List<LPData> lpDataList) {
        this.source = source;
        this.instrument = instrument;
        this.lpDataList = lpDataList;
    }
}

