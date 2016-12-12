package com.wg.gpm.parser;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import com.wg.gpm.GitMailer;
import com.wg.gpm.message.PostDetails;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * Created by aidan on 10/11/16.
 */
public class EndToEndParserTest {

    private final LineParser parser = GitMailer.buildOptionalLineParser("repourl");

    private ParserContext context;

    @Before
    public void setUp(){
        PostDetails postDetails = new PostDetails("title", Lists.newArrayList("/img/apoxiefront.jpg", "/img/firstpart.png"));
        context = new DefaultParserContext(postDetails);
    }

    @Test
    public void simpleEndToEndTest() throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(text));

        Deque<String> stack = new ArrayDeque<String>();
        reader.lines().forEach(stack::push);
        String result =    parser.parse(context, stack).stream()
                .collect(Collectors.joining("\n"));
        assertEquals(EXPECTED, result);
    }

    private static final String text =
            "---\n"+
            "{0}=(http://whilegaming.github.io/2016-11-20-BirdPlane)\n" +
            "{1}=(https://en.m.wikipedia.org/wiki/Vehicle_armour)\n" +
            "{2}=(\n" +
            "https://www.dremel.com/en_US/products/-/show-product/tools/3000-variable-speed-rotary-tool\n" +
            ")\n" +
            "{3}=(https://www.avesstudio.com/apoxie/apoxie-sculpt)\n" +
            "layout: post\n" +
    "title: Part 2: Putting it all together\n" +
    "subtitle: From concept to practice\n"+
    "type: xcom\n"+
    "img: apoxiefront\n"+
    "imageText: Sanded and Epoxied\n" +
    "---\n"+
    "\n"+
    "With the [mock complete]{0} now it's time to put the real thing together. I\n"+
    "sprayed the sprue black so that when the parts aware all put together the\n" +
    "insides would be black already. I built the basic frame of the plane. The\n"+
    "main body sides are curved when attached to the top part so I had to cello\n"+
    "tape them together to allow it to keep its shape while working on it. I\n"+
    "separated and shaped the helicopter rear propellers to slot just behind the\n"+
    "wings. There was just enough room for the pieces to fit, though it cut very\n"+
    "close on one side of the propeller.\n"+
    "\n"+
    "[img]firstpart: Putting the first pieces together.";

    private static final String EXPECTED =
                    "---\n" +
                    "layout: post\n" +
                    "title: Part 2: Putting it all together\n" +
                    "subtitle: From concept to practice\n" +
                    "type: xcom\n" +
                    "image: repourl/img/apoxiefront.jpg\n" +
                    "imageText: Sanded and Epoxied\n" +
                    "---\n" +
                    "\n"  +
    "With the [mock complete](http://whilegaming.github.io/2016-11-20-BirdPlane) now it's time to put the real thing together. I\n"+
    "sprayed the sprue black so that when the parts aware all put together the\n"+
    "insides would be black already. I built the basic frame of the plane. The\n"+
    "main body sides are curved when attached to the top part so I had to cello\n"+
    "tape them together to allow it to keep its shape while working on it. I\n"+
    "separated and shaped the helicopter rear propellers to slot just behind the\n"+
    "wings. There was just enough room for the pieces to fit, though it cut very\n"+
    "close on one side of the propeller.\n" +
"\n" +
"<p align=\"center\">\n" +
                    "<a href=\"repourl/img/firstpart.png\"><img src=\"repourl/img/firstpart.png\" alt=\" Putting the first pieces together.\" width=\"500\"></a>\n" +
                    "</p>\n" +
                    "<p align=\"center\"><i> Putting the first pieces together.</i></p>";
}
