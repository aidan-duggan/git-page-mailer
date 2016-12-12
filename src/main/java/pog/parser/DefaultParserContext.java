package pog.parser;

import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import pog.message.PostDetails;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by aidan on 04/11/16.
 */
public class DefaultParserContext implements ParserContext{

    private final Map<String, String> imageNameToImagePattern;
    private final Map<String, String> links;

    public DefaultParserContext(PostDetails postDetails){
        this.imageNameToImagePattern = new HashMap<>();
        for(String attachment: postDetails.getAttachments()){
            String name = extractImageName(attachment);
            this.imageNameToImagePattern.put(name, attachment);
        }
        this.links = new HashMap<>();
    }

    private String extractImageName(String attachment) {
        int slashIndex = attachment.lastIndexOf('/');
        int dotIndex = attachment.indexOf('.');
        return attachment.substring(slashIndex+1, dotIndex);
    }

    @Override
    public String buildImageLink(String repoUrl, String imageName){
        String image = imageNameToImagePattern.get(imageName);
        Preconditions.checkNotNull(image, "image");
        return repoUrl + image;
    }

    @Override
    public void pushLink(String number, String link) {
        this.links.put(number, link);
    }

    @Override
    public String getLink(String number) {
        return this.links.get(number);
    }

}
