package pog.parser;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import pog.message.PostDetails;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Created by aidan on 09/11/16.
 */
public class LinkLineParserTest {

    private final LinkLineParser parser = new LinkLineParser();
    private ParserContext context;

    @Before
    public void setUp(){
        PostDetails postDetails = new PostDetails("title", Lists.newArrayList("/img/img1.jpg", "/img/img2.png", "/img/img3.gif"));
        context = new DefaultParserContext(postDetails);
    }

    @Test
    public void testNormalLine(){
        Deque stack = new ArrayDeque();
        boolean result = parser.parseLine(context, buildStack("normal line"), stack);
        assertFalse(result);
    }

    @Test
    public void testContainsLink(){
        Deque stack = new ArrayDeque();
        boolean result = parser.parseLine(context, buildStack("line [link]{1}"), stack);
        assertFalse(result);
    }

    @Test
    public void testStartingLink(){
        Deque stack = new ArrayDeque();
        boolean result = parser.parseLine(context, buildStack("{1}=(http://link.com)"), stack);
        assertTrue(result);
        result = parser.parseLine(context, buildStack("{2}=(http://link2.com)"), stack);
        assertTrue(result);
        assertEquals("(http://link.com)", context.getLink("{1}"));
        assertEquals("(http://link2.com)", context.getLink("{2}"));
    }

    @Test
    public void testLinkAndTextWrongOrder(){
        Deque stack = new ArrayDeque();
        boolean result = parser.parseLine(context, buildStack("{1}=(http://link.com)"), stack);
        assertTrue(result);
        result = parser.parseLine(context, buildStack("[link]{1}"), stack);
        assertFalse(result);
    }

    @Test
    public void testStartThenNormal(){
        Deque stack = new ArrayDeque();
        boolean result = parser.parseLine(context, buildStack("---"), stack);
        assertFalse(result);
        result = parser.parseLine(context, buildStack("normal line"), stack);
        assertFalse(result);
    }

    @Test
    public void testLinkThenStartThenLink(){
        Deque stack = new ArrayDeque();
        boolean result = parser.parseLine(context, buildStack("---"), stack);
        result = parser.parseLine(context, buildStack("{1}=(http://link.com)"), stack);
        result = parser.parseLine(context, buildStack("---"), stack);
        result = parser.parseLine(context, buildStack("normal line"), stack);
        assertFalse(result);
        result = parser.parseLine(context, buildStack("link [line]{1}"), stack);
        assertTrue(result);
        assertEquals("link [line](http://link.com)", stack.pop());
    }

    @Test
    public void testMultiLinksInLine(){
        Deque stack = new ArrayDeque();
        boolean result = parser.parseLine(context, buildStack("---"), stack);
        result = parser.parseLine(context, buildStack("{1}=(http://link.com)"), stack);
        result = parser.parseLine(context, buildStack("{2}=(http://link2.com)"), stack);
        result = parser.parseLine(context, buildStack("---"), stack);
        result = parser.parseLine(context, buildStack("normal line"), stack);
        assertFalse(result);
        result = parser.parseLine(context, buildStack("link [line]{1} line [line2]{2}"), stack);
        assertTrue(result);
        assertEquals("link [line](http://link.com) line [line2](http://link2.com)", stack.pop());
    }

    @Test
    public void testSameLinkTwiceInLine(){
        Deque stack = new ArrayDeque();
        boolean result = parser.parseLine(context, buildStack("---"), stack);
        result = parser.parseLine(context, buildStack("{1}=(http://link.com)"), stack);
        result = parser.parseLine(context, buildStack("---"), stack);
        result = parser.parseLine(context, buildStack("normal line"), stack);
        assertFalse(result);
        result = parser.parseLine(context, buildStack("link [line]{1} line [line2]{1}"), stack);
        assertTrue(result);
        assertEquals("link [line](http://link.com) line [line2](http://link.com)", stack.pop());
    }

    private Deque<String> buildStack(String line) {
        Deque<String> stack = new ArrayDeque<>();
        stack.push(line);
        return stack;
    }
}
