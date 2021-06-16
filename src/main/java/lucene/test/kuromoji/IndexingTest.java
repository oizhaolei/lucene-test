package lucene.test.kuromoji;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.ja.JapaneseAnalyzer;
import org.apache.lucene.analysis.ja.JapaneseTokenizer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.util.BytesRef;

import lucene.test.lucene.LuceneUtils;

public class IndexingTest {
    private static final String[] contents = {
            "カツオはサザエの弟", "サザエはワカメの姉", "ワカメはカツオの妹",
            "カツオは長男", "サザエは長女", "ワカメは次女",
            "マスオはサザエの夫", "波平は舟の夫", "タラちゃんのパパはマスオ",
            "サザエとマスオは夫婦", "波平はタラちゃんの祖父", "舟はカツオの母",
            "マスオはカツオの義兄", "カツオはタラちゃんの叔父", "舟はワカメの母"
    };
    private static final String[] stoptags = {
            "記号-一般", "記号-読点", "記号-句点", "記号-空白", "記号-括弧開", "記号-括弧閉", "記号-アルファベット"
    };

    private static final String F_ID = "id";
    private static final String F_CONTENT = "contents";
    private Analyzer analyzer;
    private Directory directory;

    IndexingTest() throws IOException {
        analyzer = new JapaneseAnalyzer(null,
                JapaneseTokenizer.DEFAULT_MODE,
                CharArraySet.EMPTY_SET, new HashSet(Arrays.asList(stoptags)));
        directory = new MMapDirectory(new File("IndexingTest").toPath());
        makeIndex();
    }

    private LeafReader getFirstLeafReader() throws IOException {
        // XXX サンプルの簡略化のため、最初に見つかった LeafReader を返している
        // XXX 本来は、すべての leaf を辿る or SlowCompositeReaderWrapper で DirectoryReader をラップする
        return DirectoryReader.open(directory).leaves().get(0).reader();
    }

    private IndexWriter getIndexWriter() throws IOException {
        return new IndexWriter(directory, new IndexWriterConfig(analyzer));
    }

    private void makeIndex() throws IOException {
        IndexWriter writer = null;
        try {
            writer = getIndexWriter();
            int i = 1;
            for (String content : contents) {
                Document doc = new Document();
                doc.add(new Field(F_ID, Integer.toString(i++), LuceneUtils.searchType()));
                doc.add(new Field(F_CONTENT, content, LuceneUtils.searchType()));
                writer.addDocument(doc);
            }
        } finally {
            if (writer != null)
                writer.close();
        }
    }

    /** 指定された語を含むドキュメントをインデックスから削除する */
    private void deleteDocuments(String term) throws IOException {
        IndexWriter writer = null;
        try {
            writer = getIndexWriter();
            writer.deleteDocuments(new Term(F_CONTENT, term));
            writer.commit();
        } finally {
            if (writer != null)
                writer.close();
        }
    }

    public static void main(String[] args) throws Exception {
        IndexingTest irt = new IndexingTest();
        // "ワカメ"を含むドキュメントを削除しておく
        irt.deleteDocuments("ワカメ");

        // IndexReader(LeafReader)取得
        LeafReader reader = irt.getFirstLeafReader();
        // 検索可能なドキュメントの数
        System.out.println("Docs : " + Integer.toString(reader.numDocs()));
        // インデックスから削除されたドキュメントの数
        System.out.println("Deleted Docs: " + Integer.toString(reader.numDeletedDocs()));
        // 削除されたドキュメントを含む、全ドキュメント数
        System.out.println("Max Docs : " + Integer.toString(reader.maxDoc()));
        System.out.println("");

        System.out.println("---- All Documents ----");
        for (int i = 0; i < reader.maxDoc(); i++) {
            Document doc = reader.document(i);
            System.out.println(doc.get(F_ID) + " : " + doc.get(F_CONTENT));
        }
        System.out.println("");

        System.out.println("---- Documents contain 'カツオ' ----");
        // "contents" フィールドの Terms 取得
        Terms terms = reader.terms(F_CONTENT);
        // TermsEnum 取得 & "カツオ" という単語(term)が見つかるまで seek
        TermsEnum te = terms.iterator();
        boolean found = te.seekExact(new BytesRef("カツオ"));
        System.out.println("Found:" + found);
//        if (!found) {
//            // TermsEnum#seekExact() は、見つからなかったら false を返す
//        } else {
//            // 単語にひもづく DocsEnum 取得
//            DocsEnum de = te.docs(reader.getLiveDocs(), null);
//            // DocsEnum をたどり、ドキュメントIDとドキュメント本体を取得
//            while (de.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
//                Document doc = reader.document(de.docID());
//                System.out.println(doc.get(F_ID) + " : " + doc.get(F_CONTENT));
//            }
//        }
        reader.close();
    }
}