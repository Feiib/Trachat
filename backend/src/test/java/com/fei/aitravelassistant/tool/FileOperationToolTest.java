package com.fei.aitravelassistant.tool;

import org.junit.jupiter.api.Test;

class FileOperationToolTest {

    @Test
    void readFile() {
        String fileName = "fff";
        FileOperationTool fileOperationTool = new FileOperationTool();
        String string = fileOperationTool.readFile(fileName);
        System.out.println(string);

    }

    @Test
    void writeFile() {
        String fileName = "fff";
        String content = "fffhgeuh";
        FileOperationTool fileOperationTool = new FileOperationTool();
        String string = fileOperationTool.writeFile(content,fileName);
        System.out.println(string);
    }
}