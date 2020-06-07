package com.example;

import com.example.controller.XMLController;
import com.example.controller.XMLControllerDegrees;
import com.example.domain.Degree;
import com.example.domain.EntityInterface;
import com.example.domain.Position;
import com.example.domain.Worker;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLStatic {
    public static Worker getObjectWorker(Node node) {
        Worker worker=null;
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            worker = new Worker(getTagValue("firstName", element),getTagValue("birthDate", element),Integer.parseInt(element.getAttribute("id")),
                    getTagValue("lastName", element),getTagValue("middleName", element)
                    ,Integer.parseInt(getTagValue("parentId", element)),Integer.parseInt(getTagValue("salery", element)));
            if(worker.getParentId()<0)
                worker.setParentId(null);
            worker.setpId(getTagValue("positionId", element));
            worker.setdId(getTagValue("degreeId", element));
        }

        return worker;
    }
    public static Degree getObjectDegree(Node node) {
        Degree degree=null;
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            degree = new Degree(Integer.parseInt(element.getAttribute("id")),getTagValue("degree", element));
            System.out.println(Integer.parseInt(element.getAttribute("id")));
            System.out.println(degree.getId());
        }

        return degree;
    }
    public static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodeList.item(0);
        return node.getNodeValue();
    }

    public static Node getObjectWorker(Document doc, Worker w) {
        Element worker = doc.createElement("Worker");
        worker.setAttribute("id",Integer.toString( w.getId()));
        worker.appendChild(getElements(doc, worker, "lastName", w.getLastName()));
        worker.appendChild(getElements(doc, worker, "firstName", w.getFirstName()));
        worker.appendChild(getElements(doc, worker, "middleName", w.getMiddleName()));
        worker.appendChild(getElements(doc, worker, "birthDate", w.getBirthDate()));
        worker.appendChild(getElements(doc, worker, "degreeId",w.getdId()));
        worker.appendChild(getElements(doc, worker, "positionId",w.getpId()));
        if(w.getParentId()!=null)
        worker.appendChild(getElements(doc, worker, "parentId",Integer.toString( w.getParentId())));
        else
            worker.appendChild(getElements(doc, worker, "parentId","-2"));

        worker.appendChild(getElements(doc, worker, "salery",Integer.toString( w.getSalery())));
        return worker;
    }
    public static Node getObjectDegree(Document doc, Degree d) {
        Element degree = doc.createElement("Degree");
        degree.setAttribute("id",Integer.toString( d.getId()));
        degree.appendChild(getElements(doc, degree, "degree",d.getDegree()));
        return degree;
    }
    public static Node getElements(Document doc, Element element, String name, String value) {
        Element node = doc.createElement(name);
        node.appendChild(doc.createTextNode(value));
        return node;
    }
    public static boolean validationXML(DOMSource docSource, String fileXMLName,String xsdPath) throws SAXException {

        SchemaFactory factory =
                SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");


        File schemaLocation = new File(xsdPath);
        Schema schema = factory.newSchema(schemaLocation);
        Validator validator = schema.newValidator();
        try {
            validator.validate(docSource);
            System.out.println(fileXMLName + " is valid.");

        }
        catch (SAXException ex) {
            System.out.println(fileXMLName + " is not valid because ");
            System.out.println(ex.getMessage());
            return false;
        } catch (IOException e) {

            e.printStackTrace();
            return false;
        }

        return true;
    }
    public static boolean contains(int id,List<EntityInterface> list) {
        for(EntityInterface w:list)
        {
            if(w.getId()==id)
                return true;
        }
        return false;
    }
    public static EntityInterface getObj(List<EntityInterface> list,Integer id)
    {
        for(EntityInterface e:list)
        {
            if(id==e.getId())
                return e;

        }
        return null;

    }
    public static void createXML(List list, String xsdPath, String type,String xmlPath)  {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            Element rootElement =
                    doc.createElement(type);
            doc.appendChild(rootElement);
            switch(type)
            {
                case"Workers":
                    for(int i=0;i<list.size();i++)
                    {
                        Worker w=(Worker)list.get(i);
                        rootElement.appendChild(getObjectWorker(doc,w));

                    }
                    break;
                case"Degrees":
                    for(int i=0;i<list.size();i++)
                    {
                        Degree d=(Degree)list.get(i);
                        rootElement.appendChild(getObjectDegree(doc,d));

                    }
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            File f=new File(xmlPath);
            if(f.exists())
            {
                f.delete();
                f.createNewFile();
            }
            else
                f.createNewFile();
            if(validationXML(source,f.getName(),xsdPath)) {
                StreamResult file = new StreamResult(f);
                transformer.transform(source, file);
                StreamResult console = new StreamResult(System.out);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
public static NodeList readXML(InputStream inputStream, String xmlName, String type,List<EntityInterface> list)
{

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder;
    HashMap<EntityInterface,EntityInterface> conflictMap=new HashMap<>();
    NodeList nodeList=null;
    try {

        builder = factory.newDocumentBuilder();
        Document doc = builder.parse(inputStream);
        doc.getDocumentElement().normalize();
        DOMSource source = new DOMSource(doc);
        if(validationXML(source,xmlName,"docXSD"+type+"s.xsd")) {
          nodeList = doc.getElementsByTagName(type);
            int nodeId=0;
            EntityInterface newInstance=null;
            for (int i = 0; i < nodeList.getLength(); i++) {
                switch(type)
                {
                    case"Degree":newInstance= getObjectDegree(nodeList.item(i));
                        nodeId= newInstance.getId();
                    break;
                    case"Worker":newInstance= getObjectWorker(nodeList.item(i));
                        nodeId= newInstance.getId();
                    break;
                }

                if(contains(nodeId,list))
                {
                    if(!newInstance.equals((Worker)getObj(list,nodeId))) {
                        System.out.println("NOT EQUAL "+newInstance.getId());
                        conflictMap.put(getObj(list,nodeId),newInstance);
                    }
                }
                else {
                    System.out.println("NEW IMPORT OBJECT"+newInstance.toString());
                }

            }
            switch(type)
            {
                case"Degree":
                    HashMap<Degree,Degree> hmDegree=new HashMap();
                    for(Map.Entry entry:conflictMap.entrySet())
                    {
                        hmDegree.put((Degree)entry.getKey(),(Degree)entry.getValue());

                    }
                    XMLControllerDegrees.hm=hmDegree;
                    break;
                case"Worker":HashMap<Worker,Worker> hmWorker=new HashMap();
                    for(Map.Entry entry:conflictMap.entrySet())
                    {
                        hmWorker.put((Worker)entry.getKey(),(Worker)entry.getValue());

                    }
                    XMLController.hm=hmWorker;
                    break;
            }

        }

    } catch (Exception exc) {
        exc.printStackTrace();
    }
    return nodeList;

}


}
