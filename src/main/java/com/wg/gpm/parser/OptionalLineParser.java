package com.wg.gpm.parser;

import java.util.Deque;

/**
 * Created by aidan on 04/11/16.
 */
public interface OptionalLineParser {

    boolean parseLine(ParserContext context, Deque<String> lines, Deque<String> results);

}
