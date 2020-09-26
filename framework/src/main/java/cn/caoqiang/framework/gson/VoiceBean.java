package cn.caoqiang.framework.gson;

import java.util.List;

/**
 * FileName: VoiceBean
 * Founder: LiuGuiLin
 * Profile:
 */
public class VoiceBean {

    /**
     * sn : 1
     * ls : true
     * bg : 0
     * ed : 0
     * ws : [{"bg":73,"cw":[{"sc":0,"w":"这个"}]},{"bg":105,"cw":[{"sc":0,"w":"时候"}]},{"bg":121,"cw":[{"sc":0,"w":"呢"}]},{"bg":137,"cw":[{"sc":0,"w":"，"}]},{"bg":137,"cw":[{"sc":0,"w":"就"}]},{"bg":149,"cw":[{"sc":0,"w":"会"}]},{"bg":161,"cw":[{"sc":0,"w":"出现"}]},{"bg":193,"cw":[{"sc":0,"w":"什么"}]},{"bg":213,"cw":[{"sc":0,"w":"呢"}]},{"bg":221,"cw":[{"sc":0,"w":"，"}]},{"bg":221,"cw":[{"sc":0,"w":"我们"}]},{"bg":241,"cw":[{"sc":0,"w":"来"}]},{"bg":281,"cw":[{"sc":0,"w":"复制"}]},{"bg":313,"cw":[{"sc":0,"w":"一下"}]},{"bg":0,"cw":[{"sc":0,"w":"。"}]}]
     */

    private int sn;
    private boolean ls;
    private int bg;
    private int ed;
    private List<WsBean> ws;

    public int getSn() {
        return sn;
    }

    public void setSn(int sn) {
        this.sn = sn;
    }

    public boolean isLs() {
        return ls;
    }

    public void setLs(boolean ls) {
        this.ls = ls;
    }

    public int getBg() {
        return bg;
    }

    public void setBg(int bg) {
        this.bg = bg;
    }

    public int getEd() {
        return ed;
    }

    public void setEd(int ed) {
        this.ed = ed;
    }

    public List<WsBean> getWs() {
        return ws;
    }

    public void setWs(List<WsBean> ws) {
        this.ws = ws;
    }

    public static class WsBean {
        /**
         * bg : 73
         * cw : [{"sc":0,"w":"这个"}]
         */

        private int bg;
        private List<CwBean> cw;

        public int getBg() {
            return bg;
        }

        public void setBg(int bg) {
            this.bg = bg;
        }

        public List<CwBean> getCw() {
            return cw;
        }

        public void setCw(List<CwBean> cw) {
            this.cw = cw;
        }

        public static class CwBean {
            /**
             * sc : 0.0
             * w : 这个
             */

            private double sc;
            private String w;

            public double getSc() {
                return sc;
            }

            public void setSc(double sc) {
                this.sc = sc;
            }

            public String getW() {
                return w;
            }

            public void setW(String w) {
                this.w = w;
            }
        }
    }
}
