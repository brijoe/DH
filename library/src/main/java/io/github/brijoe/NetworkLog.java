package io.github.brijoe;

import java.io.Serializable;
import java.util.Locale;


 class NetworkLog  implements Serializable{

    private static final long serialVersionUID = 666L;

    private Long id;
    private String url;
    private Long date;
    private String requestType;
    private String requestHeaders;
    private String responseHeaders;
    private String responseCode;
    private String responseData;
    private Double duration;
    private String errorClientDesc;
    private String postData;



    public NetworkLog() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(String requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(String headers) {
        this.responseHeaders = headers;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseData() {
        return responseData;
    }

    public void setResponseData(String responseData) {
        this.responseData = responseData;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public String getErrorClientDesc() {
        return errorClientDesc;
    }

    public void setErrorClientDesc(String errorClientDesc) {
        this.errorClientDesc = errorClientDesc;
    }

    public String getPostData() {
        return postData;
    }

    public void setPostData(String postData) {
        this.postData = postData;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(),
                "Request Type : %s \n" +
                        "Request Url : %s\n" +
                        "Request Date : %d\n" +
                        "Request Headers :%s\n"+
                        "Response Headers : %s\n" +
                        "Response Code : %s\n" +
                        "Response Data : %s\n" +
                        "Duration : %d\n" +
                        "Error Client Desc : %s\n" +
                        "Post Data : %s",requestType,url,date, requestHeaders,responseHeaders,responseCode,responseData,
                (long)duration.doubleValue(),errorClientDesc,postData);
    }
}
