package com.example.android.navigatour;

/**
 * Created by setia on 11/20/2017.
 */

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class ReadXMLUTF8FileSAX
{
    public static void main( String[] args )
    {
        try {

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            DefaultHandler handler = new DefaultHandler() {

                boolean brname = false;
                boolean baddress = false;
                boolean bopeningtimes = false;
                boolean bmealprice = false;

                public void startElement(String uri, String localName,
                                         String qName, Attributes attributes)
                        throws SAXException {

//                    System.out.println("Start Element :" + qName);

                    if (qName.equalsIgnoreCase("NAME")) { //if tag equal name, it is the restaurant name
                        brname = true;
                    }

                    if (qName.equalsIgnoreCase("ADDRESS")) {
                        baddress = true;
                    }

                    if (qName.equalsIgnoreCase("OPENING_TIMES_LABEL")) {
                        bopeningtimes = true;
                    }

                    if (qName.equalsIgnoreCase("MEAL_PRICE")) {
                        bmealprice = true;
                    }

                }

                public void endElement(String uri, String localName,
                                       String qName)
                        throws SAXException {

//                    System.out.println("End Element :" + qName);

                }

                public void characters(char ch[], int start, int length)
                        throws SAXException {

//                    System.out.println(new String(ch, start, length));


                    if (brname) {
                        System.out.println("Restaurant Name : "
                                + new String(ch, start, length));
                        brname = false;
                    }

                    if (baddress) {
                        System.out.println("Address : "
                                + new String(ch, start, length));
                        baddress = false;
                    }

                    if (bopeningtimes) {
                        System.out.println("Opening Times : "
                                + new String(ch, start, length));
                        bopeningtimes = false;
                    }

                    if (bmealprice) {
                        System.out.println("Meal Price : "
                                + new String(ch, start, length));
                        bmealprice = false;
                    }

                }

            };

//            InputStream inputStream= new FileInputStream(file);
//            Reader reader = new InputStreamReader(inputStream,"UTF-8");

            InputSource is = new InputSource(new URL("http://apir.viamichelin.com/apir/2/findPoi.xml/RESTAURANT/eng?center=103.85:1.29&nb=10&dist=1000&source=RESGR&filter=AGG.provider%20eq%20RESGR&charset=UTF-8&ie=UTF-8&authKey=RESTGP20171120074056040173531595").openStream());
            is.setEncoding("UTF-8");

            saxParser.parse(is, handler);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
