package junit;

import junit.framework.Assert;
import lucene.LuceneWrapper;
import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class LuceneTest {
    @After
    public void tearDown() throws IOException {
        LuceneWrapper.close();
    }

    @Test
    public void simpleSearchTest() throws IOException, ParseException {
        LuceneWrapper.addDocument("Caption", "Description", "This body contains a keyword");
        LuceneWrapper.addDocument("Caption", "Description", "empty body");

        List<String> result = LuceneWrapper.search("keyword");
        Assert.assertTrue(result != null && result.isEmpty());
    }
}
