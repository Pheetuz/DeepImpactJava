package DeepImpactJava;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.swing.*;
import javax.xml.ws.Endpoint;
import java.io.Console;


/**
 * Created by Pete on 4/11/2015.
 */
@WebService()
public class HelloWorld {
  @WebMethod
  public String sayHelloWorldFrom(String from) {
    String result = "Hello, world, from " + from;
    System.out.println(result);
    return result;
  }
  public static void main(String[] argv) {
    //Object implementor = new HelloWorld ();
    //String address = "http://localhost:9000/HelloWorld";
    //Endpoint.publish(address, implementor);
    //System.out.println("Hello");

    System.out.println("hisad");

    // scraper
    Scraper scraper = new Scraper();

    scraper.Parse();

  }
}
