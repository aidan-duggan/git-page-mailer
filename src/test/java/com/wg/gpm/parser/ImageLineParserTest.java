package com.wg.gpm.parser;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import com.wg.gpm.message.PostDetails;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by aidan on 05/11/16.
 */
public class ImageLineParserTest {

    private OptionalLineParser parser = new ImageLineParser("http://url.github.io", Optional.of("500"), Optional.empty());
    private ParserContext context;

    @Before
    public void setUp(){
        PostDetails postDetails = new PostDetails("title", Lists.newArrayList("/img/img1.jpg", "/img/img2.png", "/img/img3.gif"));
        context = new DefaultParserContext(postDetails);
    }

    @Test
    public void testNoImage(){
        Deque stack = new ArrayDeque();
        boolean result = parser.parseLine(context, buildStack("Normal line."), stack);
        assertFalse(result);
    }

    @Test
    public void testTextWithImageNameButNoTag(){
        Deque stack = new ArrayDeque();
        boolean result = parser.parseLine(context, buildStack("Normal line. img1 and img2.png"), stack);
        assertFalse(result);
    }

    @Test
    public void testWithImgTag(){
        Deque stack = new ArrayDeque();
        boolean result = parser.parseLine(context, buildStack("[img]img1:alt image text"), stack);
        assertTrue(result);
        assertEquals("<p align=\"center\">\n" +
                "<a href=\"http://url.github.io/img/img1.jpg\"><img src=\"http://url.github.io/img/img1.jpg\" alt=\"alt image text\" width=\"500\"></a>" +
                "\n</p>" + "\n"+
                "<p align=\"center\"><i>alt image text</i></p>", stack.pop());
    }

    private Deque buildStack(String line) {
        Deque stack = new ArrayDeque();
        stack.push(line);
        return stack;
    }
}
