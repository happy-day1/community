package com.nowcoder.community.service;

import com.nowcoder.community.dao.es.DiscussPostRepository;
import com.nowcoder.community.entity.DiscussPost;
import org.apache.commons.lang3.time.DateUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestHandler;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

@Service
public class ElasticsearchService {

    @Autowired
    private DiscussPostRepository discussPostRepository;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    public void saveDiscussPost(DiscussPost post) {
        discussPostRepository.save(post);
    }

    public void deleteDiscussPost(int id) {
        discussPostRepository.deleteById(id);
    }

    public List<DiscussPost> searchDiscussPost(String keyWord, int current, int limit) throws ParseException {
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.multiMatchQuery(keyWord, "title", "content"))
                .from(current).size(limit)
                .sort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .highlighter(new HighlightBuilder().field("title").requireFieldMatch(false).
                        preTags("<em>").postTags("</em>"))
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
            List<DiscussPost> list = new ArrayList<DiscussPost>();
            for (SearchHit hit : searchResponse.getHits().getHits()) {
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
                createTime = createTime.substring(0, 10) + " " + createTime.substring(11, 19);
                Date date = DateUtils.parseDate(createTime, "yyyy-MM-dd HH:mm:ss");
                post.setCreateTime(date);
                String commentCount = hit.getSourceAsMap().get("commentCount").toString();
                post.setCommentCount(Integer.valueOf(commentCount));
                // 处理高亮显示
                HighlightField titleField = hit.getHighlightFields().get("title");
                if (titleField != null) {
                    post.setTitle(titleField.getFragments()[0].toString());
                }
                HighlightField contentField = hit.getHighlightFields().get("content");
                if (contentField != null) {
                    post.setContent(contentField.getFragments()[0].toString());
                }
                list.add(post);
            }
            return list;
        }
        return null;
    }

    public int searchDiscussPostCount(String keyWord) {
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.multiMatchQuery(keyWord, "title", "content"))
                .from(0).size(1000);

        // 构建搜索请求
        SearchRequest request = new SearchRequest("discusspost");
        request.source(builder);
        SearchResponse searchResponse = null;
        try {
            searchResponse = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            return searchResponse.getHits().getHits().length;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
