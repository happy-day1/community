package com.nowcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    // 替换符
    private static final String REPLACE = "***";

    // 根节点
    private TrieNode root = new TrieNode();

    @PostConstruct
    public void init() {
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ) {
            String keyword;
            while ((keyword = reader.readLine())!=null) {
                // 添加前缀树
                this.addKeyWord(keyword);
            }
        } catch (IOException e) {
            logger.error("加载敏感词文件");
        }
    }

    /**
     * 过滤敏感词
     * @param text:待过滤的文本
     * @return 过滤后的文本
     */
    public String filter(String text) {
        if (StringUtils.isBlank(text)) return null;
        TrieNode tempNode = root;
        int i=0, j=0, length = text.length();
        StringBuilder builder = new StringBuilder();
        while (j<length) {
            char c = text.charAt(j);
            // 跳过符号
            if (isSpecialSymbol(c)) {
                // 如此时为根节点，将此符号记入结果
                if (tempNode==root) {
                   builder.append(c);
                   i++;
                }
                j++;
                continue;
            }
            // 检查下级节点
            tempNode = tempNode.getSubNode(c);
            if (tempNode==null) {
                // 以i开头的字符串不是敏感词
                builder.append(text.charAt(i));
                ++i;
                j = i;
                tempNode = root;
            } else if (tempNode.isKeyWordEnd()) {
                // 发现敏感词，将i-j的字符串替换
                builder.append(REPLACE);
                ++j;
                i = j;
            } else {
                j++;
            }
        }
        // 将最后一批字符计入结果
        builder.append(text.substring(i));
        return builder.toString();
    }

    // 判断是否为符号
    private boolean isSpecialSymbol(Character c) {
        return !CharUtils.isAsciiAlphanumeric(c) && (c<0x2E80 || c>0x9FFF);
    }

    private void addKeyWord(String keyword) {
        TrieNode tempNode = this.root;
        int length = keyword.length();
        for (int i=0; i<length; i++) {
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);
            if (subNode == null) {
                // 初始化子节点
                subNode = new TrieNode();
                tempNode.addSubNode(c, subNode);
            }
            tempNode = subNode;
        }
        tempNode.setKeyWordEnd(true);
    }

    private class TrieNode {
        // 关键词结束标识
        private boolean isKeyWordEnd = false;
        // 子节点(key是下级节点字符，value是下级节点)
        private Map<Character, TrieNode> subNode = new HashMap<Character, TrieNode>();

        public boolean isKeyWordEnd() {
            return isKeyWordEnd;
        }

        public void setKeyWordEnd(boolean keyWordEnd) {
            isKeyWordEnd = keyWordEnd;
        }

        public void addSubNode(Character c, TrieNode node) {
            subNode.put(c, node);
        }

        public TrieNode getSubNode(Character c) {
            return subNode.get(c);
        }
    }

}
