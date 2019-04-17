package com.example.joonas.harjoitustyo;

import android.content.Context;
import android.provider.DocumentsContract;
import android.text.TextUtils;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Käyttäjätiedot
 */
public class User {
    protected String password, nimi, info, accountName, newPW, oldPW;
    protected int userId;
    protected boolean authCheck;
    static final String xmlFileName = "users.xml";
    Context ctx;
    Document doc;



    public int getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getNimi() {
        return nimi;
    }

    public String getInfo() {
        return info;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setOldPW(String oldPW) {
        this.oldPW = oldPW;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }
    public void setNewPW(String newPW) {
        this.newPW = newPW;
    }

    public void setXML() //Offline tilassa tunnus tallennetaan XML-tiedostoon
    {
        File file = new File(ctx.getFilesDir(), xmlFileName);
        //file.delete(); // Tällä voi resetoida luodut käyttäjät
        if (file.exists()) //onko muistissa jo XML tiedosto?
        {
            try {

                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                Document document = documentBuilder.parse(file);
                Element root = document.getDocumentElement();

                Element newUser = document.createElement("User");
                root.appendChild(newUser);

                Element nimi = document.createElement("nimi");
                nimi.appendChild(document.createTextNode(getNimi()));
                newUser.appendChild(nimi);

                Element tunnus = document.createElement("tunnus");
                tunnus.appendChild(document.createTextNode(getAccountName()));
                newUser.appendChild(tunnus);

                Element pw = document.createElement("salasana");
                pw.appendChild(document.createTextNode(getPassword()));
                newUser.appendChild(pw);

                Element info = document.createElement("info");
                info.appendChild(document.createTextNode(getInfo()));
                newUser.appendChild(info);

                DOMSource source = new DOMSource(document);

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                StreamResult result = new StreamResult(file);
                transformer.transform(source, result);


            }catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TransformerConfigurationException e) {
                e.printStackTrace();
            } catch (TransformerException e) {
                e.printStackTrace();
            }
        }
        else { //Luodaan uusi XML-tiedosto

            try {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document document = dBuilder.newDocument();

                Element parentElement = document.createElement("Users");
                document.appendChild(parentElement);

                Element element = document.createElement("User");
                parentElement.appendChild(element);

                Element eNimi = document.createElement("nimi");
                eNimi.appendChild(document.createTextNode(nimi));
                element.appendChild(eNimi);

                Element eTunnus = document.createElement("tunnus");
                eTunnus.appendChild(document.createTextNode(accountName));
                element.appendChild(eTunnus);

                Element eSalasana = document.createElement("salasana");
                eSalasana.appendChild(document.createTextNode(password));
                element.appendChild(eSalasana);

                Element eInfo = document.createElement("info");
                eInfo.appendChild(document.createTextNode(info));
                element.appendChild(eInfo);

                File newFile = new File(ctx.getFilesDir(), xmlFileName); //Tiedosto tallennetaan sisäiseen muistiin
                try {
                    newFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(document);

                StreamResult streamResult = new StreamResult(newFile);
                transformer.transform(source, streamResult);

            /* Attr attr = document.createAttribute("nimi"); //Tämä luo <user nimi="arvo">
            attr.setValue(nimi);
            element.setAttributeNode(attr); */
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (TransformerConfigurationException e) {
                e.printStackTrace();
            } catch (TransformerException e) {
                e.printStackTrace();
            }
        }
    }

    public void OfflineChangePassword() throws ParserConfigurationException, IOException, SAXException, TransformerException {
        File file = new File(ctx.getFilesDir(), xmlFileName);

        if (file.exists()) {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            doc = docBuilder.parse(file);

            Node users = doc.getElementsByTagName("Users").item(0);
            NodeList users_list = users.getChildNodes();

            for (int y = 0; y < users_list.getLength(); y++) {

                Node user = doc.getElementsByTagName("User").item(y);
                NodeList list = user.getChildNodes();

                    Node tunnusNode = list.item(1); //tunnus
                    Node passuNode = list.item(2); //passu
                    String tunnari = tunnusNode.getTextContent().toString();
                    String passu = passuNode.getTextContent().toString();
                if (accountName.equals(tunnari) && password.equals(passu))
                {
                    passuNode.setTextContent(newPW);
                }
            }
        }
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);

        StreamResult streamResult = new StreamResult(file);
        transformer.transform(source, streamResult);

    }

    public boolean offlineLogin()  {
        File file = new File(ctx.getFilesDir(), xmlFileName);

        if (file.exists()) {
            try {
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                Document document = documentBuilder.parse(file);

                DOMSource domSource = new DOMSource(document);
                StringWriter writer = new StringWriter();
                StreamResult result = new StreamResult(writer);
                TransformerFactory tf = TransformerFactory.newInstance();
                Transformer transformer = null;
                try {
                    transformer = tf.newTransformer();
                } catch (TransformerConfigurationException e) {
                    e.printStackTrace();
                }
                try {
                    transformer.transform(domSource, result);
                } catch (TransformerException e) {
                    e.printStackTrace();
                }
                //String xml = writer.toString(); //kopioi notepadiin niin näet users.xml-tiedoston

                Node users = document.getElementsByTagName("Users").item(0); //Aloitus tagi
                NodeList users_list = users.getChildNodes();

                for (int y = 0; y < users_list.getLength(); y++) { //käydään läpi kaikki 'userit'

                    Node user = document.getElementsByTagName("User").item(y);
                    NodeList list = user.getChildNodes();

                    Node nimiNode = list.item(0); //nimi
                    Node tunnusNode = list.item(1); //tunnus
                    Node passuNode = list.item(2); //passu
                    Node infoNode = list.item(3); //info

                    // etsi tunnus ja salasana
                    if (passuNode.getTextContent().equals(password) && tunnusNode.getTextContent().equals(accountName)) {
                        authCheck = true;
                        setNimi(nimiNode.getTextContent().toString());
                        setInfo(infoNode.getTextContent().toString());
                    }
                }
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return authCheck;
    }





}
