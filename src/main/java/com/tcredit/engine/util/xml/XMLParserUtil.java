package com.tcredit.engine.util.xml;

import com.tcredit.engine.exception.XMLParserException;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import java.io.File;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-11-27 10:58
 * @updatedUser: zl.T
 * @updatedDate: 2017-11-27 10:58
 * @updatedRemark:
 * @version:
 */
public class XMLParserUtil {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory
            .getLogger(XMLParserUtil.class);

    /**
     *
     * @param fileName
     * @return
     */
    public static Document paserXmlDocument(String fileName) {
        File inputXml = new File(fileName);
        return getDocument(inputXml);
    }

    public static Document paserXmlDocument(File file) {
        return getDocument(file);
    }

    private static Document getDocument(File file) {
        Document document = null;
        if (null == file || !file.exists()) {
            LOGGER.error("XML parser error:can't found specified file , wrong file " + file.getName());
            throw new XMLParserException(String.format("XML parser error:can't found specified file,wrong file %s", file.getName()));
        }
        try {
            SAXReader saxReader = new SAXReader();
            document = saxReader.read(file);
        } catch (Exception e) {
            LOGGER.error("document parser error " + e.getMessage());
            throw new RuntimeException(e);
        }
        return document;
    }


    public <T> T get(Document document,Class<T> tClass) throws IllegalAccessException, InstantiationException {
        T t = tClass.newInstance();


        return t;
    }
}