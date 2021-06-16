package lucene.test.kuromoji;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashSet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ja.JapaneseAnalyzer;
import org.apache.lucene.analysis.ja.JapaneseTokenizer;
import org.apache.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute;
import org.apache.lucene.analysis.ja.tokenattributes.ReadingAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;

public class HelloKuromoji {
  public static final String[] contents = {
    "太陽は群馬県になりました。つまり太陽系は群馬県系です。"
  };

  private static final String[] stoptags = {
    "記号-一般", "記号-読点", "記号-句点", "記号-空白", "記号-括弧開", "記号-括弧閉", "記号-アルファベット"
  };

  private Analyzer analyzer = new JapaneseAnalyzer(null,
    JapaneseTokenizer.DEFAULT_MODE,
    CharArraySet.EMPTY_SET, new HashSet(Arrays.asList(stoptags)));

  public void displayTokenStream() throws IOException {
    for (String content : contents) {
      System.out.println("\n" + content);
      System.out.println("====================================================================");
      StringReader reader = new StringReader(content);
      TokenStream stream = analyzer.tokenStream("", reader);
      stream.reset();  // must call TokenStream#reset()
      displayTokens(stream);
      stream.close();
    }
  }

  private void displayTokens(TokenStream stream) throws IOException {
    System.out.println("|テキスト\t|開始\t|終了\t|読み\t\t|品詞");
    System.out.println("--------------------------------------------------------------------");
    while(stream.incrementToken()) {
      CharTermAttribute termAtt = stream.getAttribute(CharTermAttribute.class);
      ReadingAttribute rAtt = stream.getAttribute(ReadingAttribute.class);
      OffsetAttribute oAtt = stream.getAttribute(OffsetAttribute.class);
      PartOfSpeechAttribute psAtt = stream.getAttribute(PartOfSpeechAttribute.class);

      String text = termAtt.toString();
      String yomi = rAtt.getReading();
      int sOffset = oAtt.startOffset();
      int eOffset = oAtt.endOffset();
      String pos = psAtt.getPartOfSpeech();

      System.out.println(
        "|" + text + "\t\t" +
          "|" + Integer.toString(sOffset) + "\t" +
          "|" + Integer.toString(eOffset) + "\t" +
          "|" + yomi + "\t\t" +
          "|" + pos + "\t"
      );
    }
  }

  public static void main(String[] args) throws IOException {
    HelloKuromoji test = new HelloKuromoji();
    test.displayTokenStream();
  }
}