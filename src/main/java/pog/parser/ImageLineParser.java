package pog.parser;

import java.util.Deque;
import java.util.Optional;
import java.util.Stack;

/**
 * Created by aidan on 04/11/16.
 */
public class ImageLineParser implements OptionalLineParser{

    private static final String IMG = "[img]";
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
        if(line.contains(IMG)){
            int imageNameStart = line.indexOf(IMG);
            int imageTextStart = line.indexOf(TEXT_SEPERATOR);
            String imageText = line.substring(imageTextStart + 1);
            String imageName = getString(line, imageNameStart, imageTextStart);
            String imageLink = context.buildImageLink(repoUrl, imageName);

            StringBuilder builder = new StringBuilder();
            builder.append(line.substring(0, imageNameStart));
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
            lines.removeLast();
            results.addLast(builder.toString());
            return true;
        }
        return false;
    }

    private void buildEndElementWithQuote(StringBuilder builder) {
        builder.append("\">");
    }

    private void appendQuote(StringBuilder builder) {
        builder.append("\"");
    }

    private String getString(String line, int imageNameStart, int imageTextStart) {
        return line.substring(imageNameStart + IMG.length(), imageTextStart).trim();
    }
}
