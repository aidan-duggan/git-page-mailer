package pog.parser;

import java.util.Deque;
import java.util.Optional;
import java.util.Stack;

/**
 * Created by aidan on 04/11/16.
 */
public interface OptionalLineParser {

    boolean parseLine(ParserContext context, Deque<String> lines, Deque<String> results);

}
