package com.fei.aitravelassistant.tool;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.IOException;

public class WebScrapeTool {
    @Tool(description = "useful for when you want to scrape a website")
    public String scrape(@ToolParam(description = "the url to scrape") String url) {
        try {
            Document elements = Jsoup.connect(url).get();
            return elements.html();
        } catch (IOException e) {
            return "error " + e.getMessage();
        }
    }
}
