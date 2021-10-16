package xml;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;

public class StaXWriter {
	// create XMLEventWriter
	private XMLEventWriter eventWriter;
	// create an EventFactory
	private XMLEventFactory eventFactory = XMLEventFactory.newInstance();
	private OutputStream out;
	public StaXWriter(String f) throws FileNotFoundException, XMLStreamException{
		// create an XMLOutputFactory
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		out=new FileOutputStream(f);
		eventWriter=outputFactory.createXMLEventWriter(out);
		StartDocument startDocument = eventFactory.createStartDocument();
		eventWriter.add(startDocument);
	}
	public void start(String s) throws XMLStreamException{
		StartElement StartElement = eventFactory.createStartElement("","",s);
		eventWriter.add(StartElement);
	}
	public void start_attr(String s,String a,String b) throws XMLStreamException{
		Attribute attr=eventFactory.createAttribute(a,b);//b=boolean. inexistent
		StartElement StartElement = eventFactory.createStartElement("","",s,Arrays.asList(attr).iterator(),Arrays.asList().iterator());
		eventWriter.add(StartElement);
	}
	public void data(String s,String value) throws XMLStreamException{
		StartElement StartElement = eventFactory.createStartElement("","",s);
		eventWriter.add(StartElement);
		// create Content
		Characters characters = eventFactory.createCharacters(value);
		eventWriter.add(characters);
		// create End node
		EndElement eElement = eventFactory.createEndElement("", "", s);
		eventWriter.add(eElement);
	}
	public void end(String s) throws XMLStreamException{
		EndElement eElement = eventFactory.createEndElement("", "", s);
		eventWriter.add(eElement);
	}
	public void close() throws XMLStreamException, IOException{
		eventWriter.add(eventFactory.createEndDocument());
		eventWriter.close();out.close();
	}
} 