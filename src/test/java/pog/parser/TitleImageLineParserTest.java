package pog.parser;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import pog.message.PostDetails;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;
import java.util.Stack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by aidan on 09/11/16.
 */
public class TitleImageLineParserTest {

    private final TitleImageLineParser parser = new TitleImageLineParser("repourl");
    private ParserContext context;

    @Before
    public void setUp(){
        PostDetails postDetails = new PostDetails("title", Lists.newArrayList("/img/img1.jpg", "/img/img2.png", "/img/img3.gif"));
        context = new DefaultParserContext(postDetails);
    }

    @Test
    public void testNormalLine(){
        Deque stack = new ArrayDeque();
        boolean result = parser.parseLine(context, buildStack("normal line image: img1"), stack);
        assertFalse(result);
    }

    @Test
    public void testImageLine(){
        Deque stack = new ArrayDeque();
        boolean result = parser.parseLine(context, buildStack("img: img2"), stack);
        assertTrue(result);
        assertEquals("image: repourl/img/img2.png", stack.pop());
    }

    private Deque buildStack(String line) {
        Deque stack = new ArrayDeque();
        stack.push(line);
        return stack;
    }
}
