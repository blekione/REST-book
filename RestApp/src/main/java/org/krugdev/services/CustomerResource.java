package org.krugdev.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.krugdev.domain.Customer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class CustomerResource implements CustomerResourceI {
	
	private Map<Integer, Customer> customerDB = new ConcurrentHashMap<>();
	private AtomicInteger idCounter = new AtomicInteger();
	
	public Response createCustomer(InputStream in) {
		Customer customer = readCustomer(in);
		customer.setId(idCounter.incrementAndGet());
		customerDB.put(customer.getId(), customer);
		System.out.println("Created customer " + customer.getId());
		return Response.created(URI.create("/customers/" + customer.getId()))
				.build();
	}
	
	public StreamingOutput getCustomer(int id) {
		final Customer customer = customerDB.get(id);
		if (customer == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return outputStream -> outputCustomer(outputStream, customer);
	}
	
	public void updateCustomer(int id, InputStream in) {
		Customer update = readCustomer(in);
		Customer current = customerDB.get(id);
		if (current == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		current.setFirstName(update.getFirstName());
		current.setLastName(update.getLastName());
		current.setStreet(update.getStreet());
		current.setCity(update.getCity());
		current.setState(update.getState());
		current.setZip(update.getZip());
		current.setCountry(update.getCountry());
	}

	protected Customer readCustomer(InputStream in) {
		try {
			DocumentBuilder builder = 
					DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(in);
			Element root = doc.getDocumentElement();
			
			Customer cust = new Customer();
			if (root.getAttribute("id") != null
					&& !root.getAttribute("id").trim().equals("")) {
				cust.setId(Integer.valueOf(root.getAttribute("id")));
			}
			NodeList nodes = root.getChildNodes();
			for (int i =0; i < nodes.getLength(); i++) {
				Element el = (Element) nodes.item(i);
				if (el.getTagName().equals("first-name")) {
					cust.setFirstName(el.getTextContent());
				} else if (el.getTagName().equals("last-name")) {
					cust.setLastName(el.getTextContent());
				} else if (el.getTagName().equals("street")) {
					cust.setStreet(el.getTextContent());
				} else if (el.getTagName().equals("city")) {
					cust.setCity(el.getTextContent());
				} else if (el.getTagName().equals("state")) {
					cust.setState(el.getTextContent()); 
				} else if (el.getTagName().equals("zip")) {
					cust.setZip(el.getTextContent());
				} else if (el.getTagName().equals("country")) {
					cust.setCountry(el.getTextContent());
				}
			}
			return cust;
		} catch (Exception e) {
			throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
		}
	}
	
	protected void outputCustomer(OutputStream out, Customer cust) 
			throws IOException {
		PrintStream writer = new PrintStream(out);
		writer.println("<customer id=\"" + cust.getId() + "\">");
		writer.println("  <first-name>" + cust.getFirstName() + "</first-name>");
		writer.println("  <last-name>" + cust.getLastName() + "</last-name>");
		writer.println("  <street>" + cust.getStreet() + "</street>");
		writer.println("  <city>" + cust.getCity() + "</city>");
		writer.println("  <state>" + cust.getState() + "</state>");
		writer.println("  <zip>" + cust.getZip() + "</zip>");
		writer.println("  <country>" + cust.getCountry() + "</country>");
		writer.println("</customer>");
	}
	
}
