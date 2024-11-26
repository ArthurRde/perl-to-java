package com.sippy.wrapper.parent.response;

import com.sippy.wrapper.parent.database.dao.TnbDao;

import java.util.List;

public class TnbListResponse {
    private List<TnbDao> tnb;

    public List<TnbDao> getTnb() {
        return tnb;
    }

    public void setTnb(List<TnbDao> tnb) {
        this.tnb = tnb;
    }
}
