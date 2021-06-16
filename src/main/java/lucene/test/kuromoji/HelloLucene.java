package lucene.test.kuromoji;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ja.JapaneseAnalyzer;
import org.apache.lucene.analysis.ja.JapaneseTokenizer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;

import lucene.test.lucene.LuceneUtils;

public class HelloLucene {
    private static final String FIELD_CONTENT = "content";
    private static Directory directory;
    private static final Analyzer analyzer = new JapaneseAnalyzer(null, JapaneseTokenizer.Mode.NORMAL, null,
            new HashSet<String>());
    private static final QueryParser qp = new QueryParser(FIELD_CONTENT, analyzer);

    private static final String[] contents = {
            "カツオはサザエの弟", "サザエはワカメの姉", "ワカメはカツオの妹",
            "カツオは長男", "サザエは長女", "ワカメは次女",
            "マスオはサザエの夫", "波平は舟の夫", "タラちゃんのパパはマスオ",
            "サザエとマスオは夫婦", "波平はタラちゃんの祖父", "舟はカツオの母",
            "マスオはカツオの義兄", "カツオはタラちゃんの叔父", "舟はワカメの母"
    };

    public static void main(String[] args) throws Exception {
        directory = new MMapDirectory(new File("HelloLucene").toPath());

        makeIndex();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String q = null;
        while (q == null || !q.equals("q")) {
            System.out.println("\n検索質問 (qで終了) > ");
            System.out.flush();
            q = reader.readLine();
            if (!q.equals("q")) {
                searchIndex(q);
            }
        }
        reader.close();
        if (directory != null) {
            directory.close();
        }
    }

    // インデックス作成メソッド
    private static void makeIndex() throws IOException {
        IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(analyzer));
        for (String s : contents) {
            Document doc = new Document();
            doc.add(new Field(FIELD_CONTENT, s, LuceneUtils.searchType()));
            writer.addDocument(doc);
        }
        writer.close();
    }

    // 検索メソッド
    private static void searchIndex(final String q) throws IOException, ParseException {
        IndexReader r = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(r);
        Query query = qp.parse(q);
        TopScoreDocCollector results = TopScoreDocCollector.create(10, 10);
        searcher.search(query, results);
        TopDocs docs = results.topDocs();
        System.out.println(docs.totalHits.value + "件ヒットしました。");
        ScoreDoc[] hits = docs.scoreDocs;
        for (ScoreDoc hit : hits) {
            Document doc = searcher.doc(hit.doc);
            System.out.println(doc.get(FIELD_CONTENT));
        }
    }
}