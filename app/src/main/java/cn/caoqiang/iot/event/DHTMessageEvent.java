package cn.caoqiang.iot.event;

import cn.caoqiang.framework.gson.DHTBean;

public class DHTMessageEvent {
    DHTBean dhtBean;

    public void setDhtBean(DHTBean dhtBean) {
        this.dhtBean = dhtBean;
    }

    public DHTBean getDhtBean() {
        return dhtBean;
    }
}
