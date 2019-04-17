package com.example.joonas.harjoitustyo;

import android.content.Context;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
 * Created by Joonas on 18/02/2016.
 */
public class XMLCompletedJobs {

String xmlFileName = "Jobs.xml";
Context ctx;
List<Job> list;
Document doc;

    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }

    public void setList(List list) {
        this.list = list;
    }

    public void setXML() //
    {
        File file = new File(ctx.getFilesDir(), xmlFileName);
        if (file.exists() && !list.isEmpty()) {
            try {
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                doc = docBuilder.parse(file);

                for (int y = 0; y < 10; y++) {
                    Node job = doc.getElementsByTagName("job").item(y);

                    NodeList childlist = job.getChildNodes();

                    Node jobID = childlist.item(5); //job_id
                    Node statusNode = childlist.item(1); //status
                    Node userIdNode = childlist.item(3); //userID
                    Node tunnitNode = childlist.item(11); //tunnit
                    Node suoriteNode = childlist.item(13); //suoritteet
                    Node seliteNode = childlist.item(15); //selite

                    for (Job j : list) {
                        String job_id = j.getJob_id().toString();
                        if (job_id.equals(jobID.getTextContent().toString())) {
                            String status = j.getStatus().toString();
                            String tunnit = j.getTunnit().toString();
                            String suoritteet = j.getSuoritteet().toString();
                            String selite = j.getSelite().toString();
                            String userID = j.getUser_id().toString();
                            statusNode.setTextContent(status);
                            userIdNode.setTextContent(userID);
                            tunnitNode.setTextContent(tunnit);
                            suoriteNode.setTextContent(suoritteet);
                            seliteNode.setTextContent(selite);
                        }
                    }
                }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);

            StreamResult streamResult = new StreamResult(file);
            transformer.transform(source, streamResult);

            } catch (ParserConfigurationException e) {
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


    }

}
