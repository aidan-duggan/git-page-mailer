package com.wg.gpm.parser;

import java.util.Collection;
import java.util.Deque;

/**
 * Created by aidan on 04/11/16.
 */
public interface LineParser {

    public Collection<String> parse(ParserContext context, Deque<String> lines);
}
