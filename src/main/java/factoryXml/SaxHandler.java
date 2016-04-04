package factoryXml;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SaxHandler extends DefaultHandler {
	static final Logger logger = LogManager.getLogger(SaxHandler.class);
	private static String CLASSNAME = "class"; 	
	private String element = null; 
	private Object object = null;
	
	public void startDocument() throws SAXException {
		logger.info("Start  xml doc ");
	}
 
	public void endDocument() throws SAXException {
		logger.info("End  xml doc ");
	}
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if(!qName.equals(CLASSNAME)){
			element = qName;
		}
		else{
			String className = attributes.getValue(0);
			object = helpers.ReflectionHelper.createInstance(className);
			logger.info("Class created " + className);
		}	
	}
 
	public void endElement(String uri, String localName, String qName) throws SAXException {
		element = null;
	}
 
	public void characters(char ch[], int start, int length) throws SAXException {
		if(element != null){
			String value = new String(ch, start, length);
			helpers.ReflectionHelper.setFieldValue(object, element, value);
			logger.info(element + " = " + value);
		}
	}
	
	public Object getObject(){
		return object;
	}
}
