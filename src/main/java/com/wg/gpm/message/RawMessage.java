package com.wg.gpm.message;

import java.util.List;

/**
 * Created by aidan on 31/10/16.
 */
public class RawMessage {

    private final String title;
    private final String content;
    private final List<Attachment> attachments;

    public RawMessage(String title, String content, List<Attachment> attachments){
        super();
        this.title = title;
        this.content = content;
        this.attachments = attachments;
    }

    public String getTitle(){
        return this.title;
    }

    public String getBody() {
        return this.content;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }
}
