package com.wg.gpm.parser;

import java.util.Deque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by aidan on 07/11/16.
 */
public class LinkLineParser implements OptionalLineParser{

    private static final String MATCH_SQUARE_BRACKETS = "^\\{[0-9]+\\}";
    private static final Pattern SQUARE_BRACKETS_COMPILE = Pattern.compile(MATCH_SQUARE_BRACKETS);

    private static final String GLOBAL_MATCH_SQUARE_BRACKETS = "\\{[0-9]+\\}+";
    private static final Pattern GLOBAL_SQUARE_BRACKETS_COMPILE = Pattern.compile(GLOBAL_MATCH_SQUARE_BRACKETS);

    private static final String POST_INFO_BAR = "---";
    private static final String LINK_START_BRACKET = "{";

    private Boolean hasPostedStarted;

    public LinkLineParser(){
        super();
        this.hasPostedStarted = null;
    }

    @Override
    public boolean parseLine(ParserContext context, Deque<String> lines, Deque<String> results) {
        if(hasPostedStarted == Boolean.TRUE){
            return replaceLinkNumbers(context, lines, results);
        }
        return parserPreStartLines(context, lines, results);
    }

    private boolean parserPreStartLines(ParserContext context, Deque<String> lines, Deque<String> results) {
        String line = lines.peekLast();
        if(line.startsWith(POST_INFO_BAR)){
            setHasStarted();
            return false;
        }
        if(line.startsWith(LINK_START_BRACKET)){
            parseLink(context, line, lines);
            return true;
        }
        return false;
    }

    private void setHasStarted() {
        if(this.hasPostedStarted == null){
            this.hasPostedStarted = Boolean.FALSE;
        }else if(this.hasPostedStarted == Boolean.FALSE){
            this.hasPostedStarted = Boolean.TRUE;
        }
    }

    private boolean replaceLinkNumbers(ParserContext context, Deque<String> lines, Deque<String> results) {
        String line = lines.peekLast();
        Matcher matcher = GLOBAL_SQUARE_BRACKETS_COMPILE.matcher(line);
        boolean found = false;
        String newLine = line;
        while(matcher.find()){
            String number = matcher.group();
            String link = context.getLink(number);
            newLine = newLine.replace(number, link);
            found = true;
        }
        if(found){
            results.addLast(newLine);
            lines.removeLast();
        }
        return found;
    }

    private void parseLink(ParserContext context, String line, Deque<String> lines) {
        Matcher matcher = SQUARE_BRACKETS_COMPILE.matcher(line);
        if(matcher.find()){
            lines.removeLast();
            String number = matcher.group();
            String link;
            if(line.endsWith("(")){
                link = lines.removeLast();
                if(link.endsWith(")")){
                    link = link.substring(0, link.length() -2);
                }else{
                    lines.removeLast();
                }
            }else {
                link = line.substring(number.length() + 1).trim();
            }
            context.pushLink(number, link);
        }
    }
}
