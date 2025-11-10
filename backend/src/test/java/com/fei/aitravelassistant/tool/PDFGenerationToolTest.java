package com.fei.aitravelassistant.tool;

import org.junit.jupiter.api.Test;

class PDFGenerationToolTest {

    @Test
    void generatePDF() {
        PDFGenerationTool pdfGenerationTool = new PDFGenerationTool();
        String string = pdfGenerationTool.generatePDF("test", "hhhhhh");
        System.out.println(string);
    }
}