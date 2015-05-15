
package com.javapapers.android.model;

public class Guid {

    private String isPermaLink;
    private String content;

    /**
     * 
     * @return
     *     The isPermaLink
     */
    public String getIsPermaLink() {
        return isPermaLink;
    }

    /**
     * 
     * @param isPermaLink
     *     The isPermaLink
     */
    public void setIsPermaLink(String isPermaLink) {
        this.isPermaLink = isPermaLink;
    }

    /**
     * 
     * @return
     *     The content
     */
    public String getContent() {
        return content;
    }

    /**
     * 
     * @param content
     *     The content
     */
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Guid{" +
            "isPermaLink='" + isPermaLink + '\'' +
            ", content='" + content + '\'' +
            '}';
    }
}
