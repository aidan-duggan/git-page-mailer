package com.wg.gpm.message;

/**
 * Created by aidan on 02/11/16.
 */
public class Attachment{

    private final String title;
    private final byte[] content;

    public Attachment(String title, byte[] content){
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public byte[] getContents() {
        return this.content;
    }

    @Override
    public String toString(){
        return "Attachment " + this.title;
    }
}
