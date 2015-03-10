package xml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

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
	public String advance_start() throws XMLStreamException{
		XMLEvent e=eventReader.nextEvent();
		if(e.isStartElement()==false)return null;
		return e.asStartElement().getName().getLocalPart();
	}
	public void advance() throws XMLStreamException{
		eventReader.nextEvent();
	}
	public String data() throws XMLStreamException{
		XMLEvent start=eventReader.nextEvent();
		if(start.isStartElement()==false)return null;
		XMLEvent e=eventReader.nextEvent();
		if(e.isEndElement()==true)return "";
		String s=e.asCharacters().getData();
		eventReader.nextEvent();
		return s;
	}
	public void close() throws IOException, XMLStreamException{
		//advance();
		eventReader.close();in.close();
	}
} 