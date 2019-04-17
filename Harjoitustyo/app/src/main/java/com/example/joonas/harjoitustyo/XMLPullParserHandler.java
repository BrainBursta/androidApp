package com.example.joonas.harjoitustyo;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Tämä hakee assets/users.xml tiedot ja luo näistä objekteja
 * Asset/users.xml ei käytetä vaan tiedot ovat sisäisessä muistissa. Tämä siis turha,
 * mutta tässä koodi jos joskus tarvitsee
 */
public class XMLPullParserHandler {
    List<User> users;

    private User user;
    private String text;

    public XMLPullParserHandler() {
        users = new ArrayList<User>();
    }

    public List<User> getusers() {
        return users;
    }

    public List<User> parse(InputStream is) {

        XmlPullParserFactory factory = null;
        XmlPullParser parser = null;

        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);

            parser = factory.newPullParser();
            parser.setInput(is, null);

            int eventType = parser.getEventType();
            while(eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:

                        if(tagname.equalsIgnoreCase("user")) {
                            user = new User();
                        }
                        break;

                    case XmlPullParser.TEXT:

                        text = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:

                        if(tagname.equalsIgnoreCase("user")) {
                            users.add(user);
                        }else if(tagname.equalsIgnoreCase("id")) {
                            user.setUserId(Integer.parseInt(text));
                        }else if(tagname.equalsIgnoreCase("nimi")) {
                            user.setNimi(text);
                        }else if(tagname.equalsIgnoreCase("tunnus")) {
                            user.setAccountName(text);
                        }else if(tagname.equalsIgnoreCase("salasana")) {
                            user.setPassword(text);
                        }else if(tagname.equalsIgnoreCase("info")) {
                            user.setInfo(text);
                        }
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }
}
