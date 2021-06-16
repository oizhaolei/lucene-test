package lucene.test;

import static org.junit.Assert.*;

import org.junit.Test;

import lucene.test.core.IndexManager;
import lucene.test.core.dal.JMap;
import lucene.test.core.dal.PageModel;
import lucene.test.core.dal.Post;

public class IndexManagerTests {
    private static final String[] contents = {
            "カツオはサザエの弟", "サザエはワカメの姉", "ワカメはカツオの妹",
            "カツオは長男", "サザエは長女", "ワカメは次女",
            "マスオはサザエの夫", "波平は舟の夫", "タラちゃんのパパはマスオ",
            "サザエとマスオは夫婦", "波平はタラちゃんの祖父", "舟はカツオの母",
            "マスオはカツオの義兄", "カツオはタラちゃんの叔父", "舟はワカメの母"
    };

    @Test
    public void test() {
        IndexManager postIndexManager = new IndexManager();
        for (String s : contents) {
            Post post = new Post();
            post.setId("d273d63619c9aeaf15cdaf76422c4f87");
            post.setTitle(s);
            post.setContent(s);
            postIndexManager.insert(post);
        }
        
        
        PageModel<JMap> search = postIndexManager.search("カツオ", 1);
        System.out.println(search);
        assertEquals(1, search.getTotalCount());
    }

}
