package com.example.joonas.harjoitustyo;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Tämä hakee assets/jobs.xml tiedot ja luo näistä objekteja
 */
public class XMLPullJobs {
    List<Job> jobs;

    private Job job;
    private String text;
    private String user_id, targetStatus;

    public XMLPullJobs() {
        jobs = new ArrayList<Job>();
    }

    public List<Job> getjobs() {
        return jobs;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTargetStatus() {
        return targetStatus;
    }

    public void setTargetStatus(String targetStatus) {
        this.targetStatus = targetStatus;
    }

    public List<Job> parse(InputStream is) {
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

                        if(tagname.equalsIgnoreCase("job")) {
                            job = new Job();
                        }
                        break;

                    case XmlPullParser.TEXT:

                        text = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:

                        if (tagname.equals("job")) //tämä on työn vika tagi
                        {
                            if (targetStatus.equals("Uusi"))
                            {
                                if (job.getStatus().equals("Uusi")) {
                                    jobs.add(job);
                                }
                                else if (job.getStatus().equals("Aloi-\ntettu")) {
                                    if (job.getUser_id().equals(getUser_id())) {
                                        jobs.add(job);
                                    }
                                }
                            }
                            else if (targetStatus.equals("Valmis") && job.getStatus().equals("Valmis"))
                            {
                                if (job.getUser_id().equals(user_id)) {
                                    jobs.add(job);
                                }
                            }

                        }
                        else if(tagname.equals("status")) {
                        job.setStatus(text);
                        }else if(tagname.equals("user_id")) {
                        job.setUser_id(text);
                        }else if (tagname.equalsIgnoreCase("job_id")) {
                        job.setJob_id(text);
                        }else if(tagname.equalsIgnoreCase("desc")) {
                        job.setDesc(text);
                        }else if(tagname.equalsIgnoreCase("deadline")) {
                        job.setDeadline(text);
                        }else if(tagname.equalsIgnoreCase("tunnit")) {
                        job.setTunnit((text));
                        }else if(tagname.equalsIgnoreCase("suoritteet")) {
                        job.setSuoritteet(text);
                        }else if(tagname.equalsIgnoreCase("selite")) {
                        job.setSelite(text);
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
        return jobs;
    }
}
