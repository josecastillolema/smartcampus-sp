package br.lumesi;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;  


public class TesteJersey {
	
	public static void main(String args[]){ 
		//Test the Orion Broker, query for Room
		String urlString = "http://orion.lab.fi-ware.eu:1026/NGSI10/queryContext";
		String XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + 
				"<queryContextRequest>" + 
				"<entityIdList>" + 
				"<entityId type=\"Room\" isPattern=\"true\">" + 
				"<id>Room.*</id>" + 
				"</entityId>" +
				"</entityIdList>" + 
				"<attributeList>" + 
				"<attribute>temperature</attribute>" + 
				"</attributeList>" + 
				"</queryContextRequest>";      
		try { 	  
		   Client client = Client.create();
		   WebResource webResource = client.resource(urlString);
		   ClientResponse response = (ClientResponse) webResource.type("application/xml").header("X-Auth-Token", "your_auth_token").post(ClientResponse.class, XML);

		   if (response.getStatus() != 200) {
		      System.err.println(response.getEntity(String.class));
			 throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
		   }

		   System.out.println("Output from Server .... \n");
		   String output = (String) response.getEntity(String.class);
		   System.out.println(output);

		} 
		catch (Exception e) {
		   System.err.println("Failed. Reason is " + e.getMessage());
		}

		
		
	}
	
	
}
