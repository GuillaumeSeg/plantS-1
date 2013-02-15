package xml;
import java.io.File;

import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class JDOMHierarchy {

	private Element m_Root;
	private org.jdom2.Document m_Document;
	
	public JDOMHierarchy(File XMLFile){
		SAXBuilder sxb = new SAXBuilder();
		try{
			m_Document = sxb.build(XMLFile);
		}
		catch(Exception e){
			System.out.println("error when building Document !");
		}
		m_Root = m_Document.getRootElement();
	}
	
	public void printXML(){
		try{
			XMLOutputter exit = new XMLOutputter(Format.getPrettyFormat());
			exit.output(m_Document, System.out);
		}catch(java.io.IOException e){}
	}
	
	public Element getRoot(){
		return m_Root;
	}
}
