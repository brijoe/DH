package io.github.brijoe;

import java.io.Serializable;

/**
 * 页面信息
 *
 * @Brjoe
 */

class PageInfo implements Serializable {


    private String pageName;

    private String intentStr;

    private String caller;



    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getIntentStr() {
        return intentStr;
    }

    public void setIntentStr(String intentStr) {
        this.intentStr = intentStr;
    }

    public String getCaller() {
        return caller;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }
}
