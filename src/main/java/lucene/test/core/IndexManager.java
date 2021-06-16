package lucene.test.core;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.Term;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lucene.test.core.dal.JMap;
import lucene.test.core.dal.PageModel;
import lucene.test.core.dal.Post;
import lucene.test.lucene.QueryBuilder;
import lucene.test.lucene.SearchEnginer;

/**
 * 文章Lucene索引管理器
 * 
 * @author zhou
 * 
 */

public class IndexManager{
  private static final Logger logger = LoggerFactory.getLogger(IndexManager.class);

  /**
   * 只有添加文章才插入Lucene索引
   * 
   * @param post
   */
  public void insert(Post post){
  logger.debug("add post index -->" + post.getTitle());
  SearchEnginer.postEnginer().insert(convert(post));
  }

  /**
   * 只有更新文章才更新Lucene索引
   * 
   * @param post
   * @param affect
   */
  public void update(Post post, boolean affect){
      SearchEnginer.postEnginer().update(new Term("id", post.getId()), convert(post));
  }

  public void remove(String postid, String postType){
      SearchEnginer.postEnginer().delete(new Term("id", postid));
  }

  public PageModel<JMap> search(String word, int pageIndex){
    PageModel<JMap> result = new PageModel<>(pageIndex, 15);
    QueryBuilder builder = new QueryBuilder(SearchEnginer.postEnginer().getAnalyzer());
    builder.addShould("title", word).addShould("content", word);
    builder.addLighter("title","content");
    SearchEnginer.postEnginer().searchHighlight(builder, result);

    return result;
  }

  private Document convert(Post post){
    Document doc = new Document();
    doc.add(new StringField("id", post.getId() + "", Field.Store.YES));
    doc.add(new StringField("title", post.getTitle(), Field.Store.YES));
    /* 用jsoup剔除html标签 */
    doc.add(new TextField("content", post.getContent(), Field.Store.YES));
    // doc.add(new LongField("createTime", post.getCreateTime().getTime(),
    // LuceneUtils.storeType()));
    return doc;
  }

}
