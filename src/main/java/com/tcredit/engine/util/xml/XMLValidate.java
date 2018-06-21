package com.tcredit.engine.util.xml;

import com.tcredit.engine.exception.XMLValidateException;
import com.tcredit.engine.util.FileUtil;
import com.tcredit.engine.util.PathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-11-27 15:01
 * @updatedUser: zl.T
 * @updatedDate: 2017-11-27 15:01
 * @updatedRemark:
 * @version:
 */
public class XMLValidate {

    private static Logger logger = LoggerFactory.getLogger(XMLValidate.class);

    /**
     *
     * @param xsdPath
     * @param xmlPath
     */
    public static void validateXMLSchema(String xsdPath, String xmlPath) {
        try {
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(new File(xsdPath));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new File(xmlPath)));
        } catch (Exception e) {
            logger.error(String.format("xml validate error,file:%s,exception:%s", xmlPath, e.getMessage()));
            throw new XMLValidateException(String.format("xml validate error,file:%s,exception:%s", xmlPath, e.getMessage()));
        }
    }

    public static void validate() {
        String projectDir = PathUtil.fetchParent(PathUtil.fetchProjectRootDir(), 2);
        String dpxsdPath = projectDir + "/config/xsd/data_module.xsd";
        String stepXsdPath = projectDir+"/config/xsd/step.xsd";
        String dpXmlDir = projectDir + "/config/dp/";
        String stepXmlDir = projectDir +"/config/step/";

        /**
         * 校验dataModule
         */
        File[] dpfiles = FileUtil.listFiles(dpXmlDir);
        if (null != dpfiles && dpfiles.length != 0) {
            for (File f : dpfiles) {
                validateXMLSchema(dpxsdPath, f.getAbsolutePath());
            }
        }

        /**
         * 校验step
         */
        File[] stepFiles = FileUtil.listFiles(stepXmlDir);
        if (null != stepFiles && stepFiles.length != 0) {
            for (File f : stepFiles) {
                validateXMLSchema(stepXsdPath, f.getAbsolutePath());
            }
        }
    }

    public static void main(String[] args) {

        validate();
    }

}
