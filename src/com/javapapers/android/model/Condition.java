
package com.javapapers.android.model;


public class Condition {

    private String code;
    private String date;
    private String temp;
    private String text;

    /**
     * 
     * @return
     *     The code
     */
    public String getCode() {
        return code;
    }

    /**
     * 
     * @param code
     *     The code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 
     * @return
     *     The date
     */
    public String getDate() {
        return date;
    }

    /**
     * 
     * @param date
     *     The date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * 
     * @return
     *     The temp
     */
    public String getTemp() {
        return temp;
    }

    /**
     * 
     * @param temp
     *     The temp
     */
    public void setTemp(String temp) {
        this.temp = temp;
    }

    /**
     * 
     * @return
     *     The text
     */
    public String getText() {
        return text;
    }

    /**
     * 
     * @param text
     *     The text
     */
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Condition{" +
            "code='" + code + '\'' +
            ", date='" + date + '\'' +
            ", temp='" + temp + '\'' +
            ", text='" + text + '\'' +
            '}';
    }
}
