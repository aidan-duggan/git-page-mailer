package com.wg.gpm.parser;

import java.util.*;

/**
 * Created by aidan on 04/11/16.
 */
public class DefaultLineParser implements LineParser {

    private final List<OptionalLineParser> parsers;

    public DefaultLineParser(List<OptionalLineParser> parsers){
        super();
        this.parsers = parsers;
    }

    @Override
    public Collection<String> parse(ParserContext context, Deque<String> lines) {
        Deque<String> results = new ArrayDeque<>();
        while(!lines.isEmpty()) {
            parseLine(context, lines, results);
        }
        return results;
    }

    private void parseLine(ParserContext context, Deque<String> lines, Deque<String> results) {
        boolean found = false;
        for(OptionalLineParser parser : parsers){
            found = parser.parseLine(context, lines, results);
            if(found){
                break;
            }
        }
        if(!found) {
            results.addLast(lines.removeLast());
        }
    }

}
