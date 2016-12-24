package com.wg.gpm.parser;

import java.util.Deque;
import java.util.Optional;

/**
 * Created by aidan on 04/11/16.
 */
public class ImageLineParser implements OptionalLineParser{

    private static final char TEXT_SEPERATOR = ':';
    private static final String CENTER_P_TAG = "<p align=\"center\">";
    private static final String END_P_TAG = "</p>";
    private final String repoUrl;
    private final Optional<String> width;
    private final Optional<String> height;

    public ImageLineParser(String repoUrl, Optional<String> width, Optional<String> height){
        super();
        this.repoUrl = repoUrl;
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean parseLine(ParserContext context, Deque<String> lines, Deque<String> results) {
        String line = lines.peekLast();
        Optional<ImageType> imageType = buildImageType(line);
        if(imageType.isPresent()){
            ImageType type = imageType.get();
            String tag = type.getTag();
            int imageNameStart = line.indexOf(tag);
            int imageTextStart = line.indexOf(TEXT_SEPERATOR);
            String imageName = getName(line, imageNameStart + tag.length(), imageTextStart);
            String imageText = line.substring(imageTextStart + 1);
            String imageLink = type.buildImageLink(context, this.repoUrl, imageName);

            String newLine = buildImageHtml(imageText, imageLink);
            lines.removeLast();
            results.addLast(newLine);
            return true;
        }
        return false;
    }

    private Optional<ImageType> buildImageType(String line) {
        for(ImageType type : ImageType.values()) {
            if (line.startsWith(type.getTag())) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }

    private String buildImageHtml(String imageText, String imageLink) {
        StringBuilder builder = new StringBuilder();
        builder.append(CENTER_P_TAG);
        builder.append(System.lineSeparator());

        builder.append("<a href=\"");
        builder.append(imageLink);
        buildEndElementWithQuote(builder);

        builder.append("<img src=");
        appendQuote(builder);
        builder.append(imageLink);
        appendQuote(builder);
        builder.append(" alt=");
        appendQuote(builder);
        builder.append(imageText);
        appendQuote(builder);
        if(this.width.isPresent()) {
            builder.append(" width=\"");
            builder.append(this.width.get());
            builder.append("\"");
        }
        if(this.height.isPresent()){
            builder.append(" height=\"");
            builder.append(this.height.get());
            builder.append("\"");
        }
        builder.append(">");

        builder.append("</a>");
        builder.append(System.lineSeparator());
        builder.append(END_P_TAG);
        builder.append("\n");
        builder.append(CENTER_P_TAG);
        builder.append("<i>");
        builder.append(imageText);
        builder.append("</i>");
        builder.append(END_P_TAG);
        return builder.toString();
    }

    private void buildEndElementWithQuote(StringBuilder builder) {
        builder.append("\">");
    }

    private void appendQuote(StringBuilder builder) {
        builder.append("\"");
    }

    private String getName(String line, int imageNameEnd, int imageTextStart) {
        return line.substring(imageNameEnd, imageTextStart).trim();
    }
}
