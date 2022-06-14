package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.es.DiscussPostRepository;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.service.ElasticsearchService;
import org.apache.commons.lang3.time.DateUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ElasticsearchTest {

    @Autowired
    private DiscussPostMapper discussMapper;

    @Autowired
    private DiscussPostRepository discussRepository;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Test
    public void testInsert() {
        discussRepository.save(discussMapper.selectDiscussPostById(241));
        discussRepository.save(discussMapper.selectDiscussPostById(242));
        discussRepository.save(discussMapper.selectDiscussPostById(243));
    }

    @Test
    public void testInsertList() {
        discussRepository.saveAll(discussMapper.selectDiscussPosts(101, 0, 100));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(102, 0, 100));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(103, 0, 100));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(111, 0, 100));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(112, 0, 100));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(131, 0, 100));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(132, 0, 100));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(133, 0, 100));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(134, 0, 100));
    }

    @Test
    public void testUpdate() {
        DiscussPost discussPost = discussMapper.selectDiscussPostById(231);
        discussPost.setContent("新人使劲灌水。");
        discussRepository.save(discussPost);
    }

    @Test
    public void testSearch() throws ParseException {
        // 构造搜索条件：高亮显示、分页方式、排序方式
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                .from(0).size(10)
                .sort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .highlighter(new HighlightBuilder().field("title").requireFieldMatch(false).
                        preTags("\"<span style='color:red'>\"").postTags("\"</span>\""))
                .highlighter(new HighlightBuilder().field("content").requireFieldMatch(false).
                        preTags("\"<span style='color:red'>\"").postTags("\"</span>\""));

        // 构建搜索请求
        SearchRequest request = new SearchRequest("discusspost");
        request.source(builder);
        SearchResponse searchResponse = null;
        try {
            searchResponse = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (searchResponse != null) {
            System.out.println("检出总数目:" + searchResponse.getHits().getTotalHits());
            List<DiscussPost> list = new ArrayList<DiscussPost>();
            for (SearchHit hit : searchResponse.getHits().getHits()) {
                Map<String, Object> map = hit.getSourceAsMap();

                // 将Document转化为Bean
                DiscussPost post = new DiscussPost();
                String id = hit.getSourceAsMap().get("id").toString();
                post.setId(Integer.valueOf(id));
                String userId = hit.getSourceAsMap().get("userId").toString();
                post.setUserId(Integer.valueOf(userId));
                String title = hit.getSourceAsMap().get("title").toString();
                post.setTitle(title);
                String content = hit.getSourceAsMap().get("content").toString();
                post.setContent(content);
                String status = hit.getSourceAsMap().get("status").toString();
                post.setStatus(Integer.valueOf(status));
                // es在存日期时会把日期转换为long类型
                String createTime = hit.getSourceAsMap().get("createTime").toString();
                System.out.println(createTime);
                createTime = createTime.substring(0,10)+" "+createTime.substring(11, 19);
                Date date = DateUtils.parseDate(createTime, "yyyy-MM-dd HH:mm:ss");
                post.setCreateTime(date);
                String commentCount = hit.getSourceAsMap().get("commentCount").toString();
                post.setCommentCount(Integer.valueOf(commentCount));
                // 处理高亮显示
                HighlightField titleField =  hit.getHighlightFields().get("title");
                if (titleField != null) {
                    post.setTitle(titleField.getFragments()[0].toString());
                }
                HighlightField contentField = hit.getHighlightFields().get("content");
                if (contentField != null) {
                    post.setContent(contentField.getFragments()[0].toString());
                }
                list.add(post);
            }
            list.forEach(System.out::println);
        }
    }

    @Test
    public void testElasticsearchService() throws ParseException {
        System.out.println(elasticsearchService.searchDiscussPostCount("互联网寒冬"));
    }
}
