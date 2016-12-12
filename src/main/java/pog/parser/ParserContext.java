package pog.parser;

/**
 * Created by aidan on 04/11/16.
 */
public interface ParserContext {

    public String buildImageLink(String repoUrl, String imageName);

    void pushLink(String number, String link);

    String getLink(String number);
}
