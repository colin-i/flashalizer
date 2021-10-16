package xml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public class StaXParser {
	private XMLEventReader eventReader;private InputStream in;
	public StaXParser(String f) throws FileNotFoundException, XMLStreamException{
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		// Setup a new eventReader
		in=new FileInputStream(f);
		eventReader=inputFactory.createXMLEventReader(in);
		eventReader.nextEvent();
	}
	private XMLEvent last_event;
	public String advance_start() throws XMLStreamException{
		last_event=eventReader.nextEvent();
		if(last_event.isStartElement()==false)return null;
		return last_event.asStartElement().getName().getLocalPart();
	}
	public String get_attr(String name) throws XMLStreamException{
		return last_event.asStartElement().getAttributeByName(new QName(name)).getValue();
	}
	public void advance() throws XMLStreamException{
		eventReader.nextEvent();
	}
	public String data() throws XMLStreamException{
		XMLEvent start=eventReader.nextEvent();
		if(start.isStartElement()==false)return null;
		XMLEvent e=eventReader.nextEvent();
		if(e.isEndElement()==true)return "";
		String s="";
		do{
			s+=e.asCharacters().getData();//this is required there are cases when (text multiple lines breaks text again lines) is chopped
			e=eventReader.nextEvent();
		}while(e.isEndElement()==false);
		return s;
	}
	public void close() throws IOException, XMLStreamException{
		//advance();
		eventReader.close();in.close();
	}
} 