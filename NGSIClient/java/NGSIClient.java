import ex.jclient;

public class NGSIClient {

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
   public static void main(String[] args) {
try { 	  
   Client client = Client.create();
   WebResource webResource = client.resource(urlString);
   ClientResponse response = webResource.type("application/xml").header("X-Auth-Token", "your_auth_token").post(ClientResponse.class, XML);
   
   if (response.getStatus() != 200) {
      System.err.println(response.getEntity(String.class));
  	   throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
   }
   
   System.out.println("Output from Server .... \n");
   String output = response.getEntity(String.class);
   System.out.println(output);
   
} catch (Exception e) {
   System.err.println("Failed. Reason is " + e.getMessage());
}
   }
}
