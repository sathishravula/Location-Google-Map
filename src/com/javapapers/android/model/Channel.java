
package com.javapapers.android.model;

public class Channel {

    private Item item;

    /**
     * 
     * @return
     *     The item
     */
    public Item getItem() {
        return item;
    }

    /**
     * 
     * @param item
     *     The item
     */
    public void setItem(Item item) {
        this.item = item;
    }

    @Override
    public String toString() {
        return "Channel{" +
            "item=" + item +
            '}';
    }
}
