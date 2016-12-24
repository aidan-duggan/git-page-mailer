package com.wg.gpm.parser;

import com.wg.gpm.properties.Property;

/**
 * Created by aidan on 24/12/16.
 */
public enum ImageType {
    NEW_IMAGE("[img]"){
        @Override
        public String buildImageLink(ParserContext context, String repoUrl, String imageName) {
            return context.buildImageLink(repoUrl, imageName);
        }
    },
    EXISTING_IMAGE("[ex-img]"){
        @Override
        public String buildImageLink(ParserContext context, String repoUrl, String imageName) {
            return repoUrl + "/" + Property.IMG_DIR + "/" + imageName;
        }
    };

    private final String tag;

    ImageType(String tag){
        this.tag = tag;
    }

    public String getTag(){
        return this.tag;
    }

    public abstract String buildImageLink(ParserContext context, String repoUrl, String imageName);

}
