package lucene.test.lucene;

/**
 * Lucene索引操作异常，此处继承spring的dao异常
 * 
 * @author zhou
 * 
 */
public class LuceneException extends RuntimeException{

  private static final long serialVersionUID = 1L;

  public LuceneException(String msg){
    super(msg);
  }

  public LuceneException(String msg, Throwable t){
    super(msg, t);
  }

}