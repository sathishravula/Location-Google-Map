
package com.javapapers.android.model;

public class Example {

    private Query query;

    /**
     * 
     * @return
     *     The query
     */
    public Query getQuery() {
        return query;
    }

    /**
     * 
     * @param query
     *     The query
     */
    public void setQuery(Query query) {
        this.query = query;
    }

    @Override
    public String toString() {
        return "Example{" +
            "query=" + query +
            '}';
    }
}
