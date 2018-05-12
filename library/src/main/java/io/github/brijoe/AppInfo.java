package io.github.brijoe;

/**
 * Created by bridgeliang on 2018/5/4.
 */

class AppInfo {
    private String pkgName;
    private String version;

    private String signMd5;
    private String signSha1;

    private String signSha256;

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }


    public String getSignMd5() {
        return signMd5;
    }

    public void setSignMd5(String signMd5) {
        this.signMd5 = signMd5;
    }

    public String getSignSha1() {
        return signSha1;
    }

    public void setSignSha1(String signSha1) {
        this.signSha1 = signSha1;
    }

    public String getSignSha256() {
        return signSha256;
    }

    public void setSignSha256(String signSha256) {
        this.signSha256 = signSha256;
    }
}
