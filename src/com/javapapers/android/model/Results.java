
package com.javapapers.android.model;

public class Results {

    private Channel channel;

    /**
     * 
     * @return
     *     The channel
     */
    public Channel getChannel() {
        return channel;
    }

    /**
     * 
     * @param channel
     *     The channel
     */
    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public String toString() {
        return "Results{" +
            "channel=" + channel +
            '}';
    }
}
