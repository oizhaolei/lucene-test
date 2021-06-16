package lucene.test.lucene;

import java.io.IOException;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;

public class LuceneUtils{

  private LuceneUtils(){
  }

  public static FieldType directType(){
    FieldType directType = new FieldType();
    directType.setIndexOptions(IndexOptions.DOCS);
    directType.setStored(true);

    return directType;
  }

  public static FieldType searchType(){
    FieldType searchType = new FieldType();
    searchType.setStored(true);
    /* 设置分词 */
    searchType.setTokenized(true);

    return searchType;
  }


  public static void closeQuietly(TokenStream stream){
    if(stream == null)
      return;

    try{
      stream.close();
    }catch(IOException e){
      e.printStackTrace();
    }
  }

}
