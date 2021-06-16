package lucene.test.core.dal;

public class PostConstants{
  private PostConstants(){
  }

  /* 文章评论状态 */
  public static final String COMMENT_OPEN = "open";
  public static final String COMMENT_CLOSE = "close";
  /* 文章状态,发布、回收站 */
  public static final String POST_PUBLISH = "publish";
  public static final String POST_SECRET = "secret";
  public static final String POST_TRASH = "trash";
  
  /* 所有创建者 */
  public static final String POST_CREATOR_ALL = "all";

  /**
   * 默认文章的parent
   */
  public static final String DEFAULT_PARENT = "Root";

  /**
   * 初始化postid
   */
  public static final int INIT_POST_ID = 5;

}
