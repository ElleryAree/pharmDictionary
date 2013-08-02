package lucene;

import models.Article;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LuceneWrapper {

    // Store the index in memory:
    private static Directory directory = new RAMDirectory();
//    private static Analyzer analyzer = new RussianAnalyzer(Version.LUCENE_43);
    private static Analyzer analyzer = new EnglishAnalyzer(Version.LUCENE_43);

    private static String CAPTION_FIELD = "caption";
    private static String DESCRIPTION_FIELD = "description";
    private static String BODY_FIELD = "body";
    private static String ID_FIELD = "id";


    public static void addAll(List<Article> articles) throws IOException {
        for (Article article: articles){
            addDocument(article.id().toString(), article.caption(), article.short_description(), article.body());
        }
    }

    public static void addDocument(String id, String caption, String description, String body) throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_43, analyzer);
        IndexWriter writer = new IndexWriter(directory, config);

        Document doc = new Document();
        doc.add(new Field(ID_FIELD, id, TextField.TYPE_STORED));
        doc.add(new Field(CAPTION_FIELD, caption, TextField.TYPE_STORED));
        doc.add(new Field(DESCRIPTION_FIELD, description, TextField.TYPE_STORED));
        doc.add(new Field(BODY_FIELD, body, TextField.TYPE_STORED));

        writer.addDocument(doc);
        writer.close();
    }

    public static List<Article> search(String queryString) throws IOException, ParseException, InvalidTokenOffsetsException {
        // Now search the index:
        DirectoryReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        // Parse a simple query that searches for "text":
        QueryParser parser = new MultiFieldQueryParser(Version.LUCENE_43,
                            new String[]{CAPTION_FIELD, DESCRIPTION_FIELD, BODY_FIELD},
                            analyzer);
        Query query = parser.parse(queryString);
        ScoreDoc[] hits = searcher.search(query, null, 1000).scoreDocs;
        // Iterate through the results:
        List<Article> docs = new ArrayList<Article>(hits.length);

        SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter();
        Highlighter highlighter = new Highlighter(htmlFormatter, new QueryScorer(query));

        for (ScoreDoc hit : hits) {
            Document document = searcher.doc(hit.doc);
            TokenStream tokenStream = TokenSources.getAnyTokenStream(searcher.getIndexReader(), hit.doc, BODY_FIELD, analyzer);
            TextFragment[] frag = highlighter.getBestTextFragments(tokenStream, document.get(BODY_FIELD), false, 10);//highlighter.getBestFragments(tokenStream, text, 3, "...");

            String description = "";
            for (TextFragment aFrag : frag) {
                if ((aFrag != null) && (aFrag.getScore() > 0)) {
                    description +=  aFrag.toString() + "<br>";
                }
            }
            docs.add(new Article(new ObjectId(document.get(ID_FIELD)), document.get(CAPTION_FIELD), description, "", ""));
        }
        reader.close();

        return docs;
    }

    public static void close() throws IOException {
        directory.close();
    }
}
