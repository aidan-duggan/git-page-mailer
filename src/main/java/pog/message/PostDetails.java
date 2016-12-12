package pog.message;

import java.util.List;

/**
 * Created by aidan on 31/10/16.
 */
public class PostDetails {

    private final String postPattern;
    private final List<String> attachments;

    public PostDetails(String postPattern, List<String> attachments) {
        super();
        this.postPattern = postPattern;
        this.attachments = attachments;

    }

    public String getPostFilePattern(){
        return this.postPattern;
    }

    public List<String> getAttachments() {
        return attachments;
    }

    @Override
    public String toString(){
        return "Post " + this.postPattern;
    }
}
