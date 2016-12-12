package pog.parser;

import java.util.Deque;
import java.util.Optional;
import java.util.Stack;

/**
 * Created by aidan on 07/11/16.
 */
public class TitleImageLineParser implements OptionalLineParser {

    private static final String TITLE_IMAGE = "img:";
    private static final int BEGIN_INDEX = TITLE_IMAGE.length() + 1;
    private static final String IMAGE = "image: ";

    private final String repoUrl;

    public TitleImageLineParser(String repoUrl){
        super();
        this.repoUrl = repoUrl;
    }

    @Override
    public boolean parseLine(ParserContext context, Deque<String> lines, Deque<String> results) {
        String line = lines.peekLast();
        if(line.startsWith(TITLE_IMAGE)){
            String imageName = line.substring(BEGIN_INDEX).trim();
            String imageLink = context.buildImageLink(repoUrl, imageName);
            results.addLast(IMAGE + imageLink);
            lines.removeLast();
            return true;
        }
        return false;
    }
}
