package com.example.controller;

import com.example.CheckboxAttribute;
import com.example.XMLStatic;
import com.example.domain.Degree;
import com.example.domain.Filter;
import com.example.domain.Worker;
import com.example.repos.DegreesRepository;
import com.example.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.w3c.dom.NodeList;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Controller
public class XMLControllerDegrees {
    private final StorageService storageService;
    public static HashMap<Degree,Degree> hm;
    @Autowired
    public XMLControllerDegrees(StorageService storageS) {
        this.storageService = storageS;
    }
    @Autowired
    DegreesRepository degreesRepository;
    @GetMapping("/xmlDegrees")
    public String listUploadedFilesDegree(Model model, RedirectAttributes redirectAttributes) throws IOException {
        model.addAttribute("loadMessage",redirectAttributes.getAttribute("loadMessage"));
        return "xmlMainDegrees";
    }

    @PostMapping("xmlDegrees/create")
    public String createXMLDegree(Model model) throws IOException, TransformerException {
        List<Degree> degrees=(List<Degree>)degreesRepository.findAll();
        if(!degrees.isEmpty()) {
            XMLStatic.createXML((List) degrees,"docXSDDegrees.xsd","Degrees","fileXMLDegrees.xml");
            TransformerFactory factory = TransformerFactory.newInstance();
            Source xslt = new StreamSource(new File("xslTableDegrees.xsl"));
            Transformer transformer = factory.newTransformer(xslt);
            Source xml = new StreamSource(new File("fileXMLDegrees.xml"));
            if (!Files.exists(Paths.get("src\\main\\resources\\templates\\outputDegrees.html")))
                Files.createFile(Paths.get("src\\main\\resources\\templates\\outputDegrees.html"));
            else
            {
                Files.delete(Paths.get("src\\main\\resources\\templates\\outputDegrees.html"));
                Files.createFile(Paths.get("src\\main\\resources\\templates\\outputDegrees.html"));
            }
            transformer.transform(xml, new StreamResult(new File("src\\main\\resources\\templates\\outputDegrees.html")));
            return "outputDegrees";
        }
        return "redirect:/xmlDegrees";
    }
    @PostMapping("/xmlDegrees")
    @Transactional
    public String handleFileUploadDegree(@RequestParam("file") MultipartFile file,
                                   Model model,RedirectAttributes redirectAttributes) throws IOException {
        hm.clear();
        storageService.store(file);
        NodeList nodeList=XMLStatic.readXML(file.getInputStream(),file.getOriginalFilename(),"Degree",(List)degreesRepository.findAll());
        ArrayList<Integer> conflictList=new ArrayList<>();
        for(Map.Entry<Degree,Degree> entry: hm.entrySet())
            conflictList.add(entry.getKey().getId());
        for (int i = 0; i < nodeList.getLength(); i++)
        {
            Degree d=XMLStatic.getObjectDegree(nodeList.item(i));
            if(!conflictList.contains(d.getId()))
                degreesRepository.save(d);
        }
        if(hm.isEmpty()) {
            redirectAttributes.addAttribute("loadMessage", "upload complete!");
            return "redirect:/xmlDegrees";
        }else{
            model.addAttribute("listCheckBoxes",new CheckboxAttribute());

            model.addAttribute("conflicts",hm);
            return "conflictsDegrees";

        }
    }
    @PostMapping("/conflictsDegrees")
    public String conflictDegree(Model model,@ModelAttribute(value="listCheckBoxes") CheckboxAttribute box,
                           RedirectAttributes redirectAttributes)
    {
        System.out.println(box.getCheckedItems().size());
        if(box.getCheckedItems()!=null&&box.getCheckedItems().size()>0) {
            for (String id : box.getCheckedItems()) {
                for (Degree d :hm.values()) {
                    System.out.println(d.getId());
                    System.out.println(id);
                    if (d.getId() == Integer.parseInt(id)) {
                        degreesRepository.save(d);
                        System.out.println("SAVE IMPORT OBJECT"+d.toString());
                    }
                }
            }
        }
        redirectAttributes.addAttribute("loadMessage", "upload complete!");
        return "redirect:/xmlDegrees";
    }
}
