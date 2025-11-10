package com.fei.aitravelassistant.tool;

import org.junit.jupiter.api.Test;

class WebScrapeToolTest {

    @Test
    void scrape() {
        WebScrapeTool webScrapeTool = new WebScrapeTool();
        String content = webScrapeTool.scrape("https://www.thepaper.cn/newsDetail_forward_27514081");
        System.out.println(content);
    }
}