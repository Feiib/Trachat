package com.fei.aitravelassistant.tool;

import org.junit.jupiter.api.Test;

class WebSearchToolTest {

    @Test
    void search() {
        WebSearchTool webSearchTool = new WebSearchTool("qsPs3HzYWZgstA1VtCKLdjmk");
        String search = webSearchTool.search("兰州");
        System.out.println(search);
    }
}