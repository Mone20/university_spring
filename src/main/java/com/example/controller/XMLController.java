package com.example.controller;

import com.example.CheckboxAttribute;
import com.example.XMLStatic;
import com.example.domain.EntityInterface;
import com.example.domain.Filter;
import com.example.domain.Worker;
import com.example.repos.DegreesRepository;
import com.example.repos.PositionRepository;
import com.example.repos.WorkersRepository;
import com.example.specifitations.WorkerSpecifitation;
import com.example.storage.StorageService;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.w3c.dom.NodeList;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.springframework.data.jpa.domain.Specification.where;

@Controller
public class XMLController {
    public static HashMap<Worker,Worker> hm;
    @Autowired
    WorkersRepository workersRepository;
    @Autowired
    DegreesRepository degreesRepository;
    @Autowired
    PositionRepository positionRepository;
    private final StorageService storageService;

    @Autowired
    public XMLController(StorageService storageS) {
        this.storageService = storageS;
    }
    @GetMapping("/xmlWorkers")
    public String listUploadedFiles(Model model,RedirectAttributes redirectAttributes) throws IOException {
        model.addAttribute("positions",positionRepository.findAll());
        model.addAttribute("degrees",degreesRepository.findAll());
        model.addAttribute("loadMessage",redirectAttributes.getAttribute("loadMessage"));
        model.addAttribute("filter",new Filter());
        return "xmlMainWorkers";
    }
    @GetMapping("/xmlWorkersOutput")
    public String out(Model model)
    {
        return "outputWorkers";
    }
    @PostMapping("xmlWorkers/create")
    public String createXML(Model model, @ModelAttribute("filter") Filter filter) throws IOException, TransformerException {
        List<Worker> workers=workersRepository.findAll(where(WorkerSpecifitation.workersWith(new Pair("pId",filter.getFirstFilter()))
                .and(WorkerSpecifitation.workersWith(new Pair("dId",filter.getSecondFilter())))));;
        if(!workers.isEmpty()) {
            XMLStatic.createXML((List) workers,"docXSDWorkers.xsd","Workers","fileXMLWorkers.xml");
            TransformerFactory factory = TransformerFactory.newInstance();
            Source xslt = new StreamSource(new File("xslTableWorkers.xsl"));
            Transformer transformer = factory.newTransformer(xslt);
            Source xml = new StreamSource(new File("fileXMLWorkers.xml"));
            if (!Files.exists(Paths.get("src\\main\\resources\\templates\\outputWorkers.html")))
                Files.createFile(Paths.get("src\\main\\resources\\templates\\outputWorkers.html"));
            else
            {
                Files.delete(Paths.get("src\\main\\resources\\templates\\outputWorkers.html"));
                Files.createFile(Paths.get("src\\main\\resources\\templates\\outputWorkers.html"));
            }
            transformer.transform(xml, new StreamResult(new File("src\\main\\resources\\templates\\outputWorkers.html")));
            return "redirect:/xmlWorkersOutput";
        }
       return "redirect:/xmlWorkers";
    }
    @PostMapping("/xmlWorkers")
    @Transactional
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   Model model,RedirectAttributes redirectAttributes) throws IOException {
        storageService.store(file);
        if(hm!=null&&!hm.isEmpty())
        hm.clear();
       NodeList nodeList=XMLStatic.readXML(file.getInputStream(),file.getOriginalFilename(),"Worker",(List)workersRepository.findAll());
       ArrayList<Integer> conflictList=new ArrayList<>();
       for(Map.Entry<Worker,Worker> entry: hm.entrySet())
           conflictList.add(entry.getKey().getId());


        for (int i = 0; i < nodeList.getLength(); i++)
        {
            Worker w=XMLStatic.getObjectWorker(nodeList.item(i));
            if(!conflictList.contains(w.getId())) {
                if(positionRepository.existsById(Integer.parseInt(w.getpId())))
                w.setPosition(positionRepository.findById(Integer.parseInt(w.getpId())));
                if(degreesRepository.existsById(Integer.parseInt(w.getdId())))
                w.setDegree(degreesRepository.findById(Integer.parseInt(w.getdId())));
                workersRepository.save(w);
            }

        }

            if(hm.isEmpty()) {
            redirectAttributes.addAttribute("loadMessage", "upload complete!");
            return "redirect:/xmlWorkers";
        }else{
            model.addAttribute("listCheckBoxes",new CheckboxAttribute());
            model.addAttribute("conflicts",hm);
            return "conflictsWorkers";

        }
    }
    @PostMapping("/conflictsWorkers")
    public String conflict(Model model,@ModelAttribute(value="listCheckBoxes") CheckboxAttribute box,
                           RedirectAttributes redirectAttributes)
    {
        System.out.println(box.getCheckedItems().size());
        if(box.getCheckedItems()!=null&&box.getCheckedItems().size()>0) {
            for (String id : box.getCheckedItems()) {
                for (Worker w :hm.values()) {
                    System.out.println(w.getId());
                    System.out.println(id);
                    if (w.getId() == Integer.parseInt(id)) {
                        w.setPosition(positionRepository.findById(Integer.parseInt(w.getpId())));
                        w.setDegree(degreesRepository.findById(Integer.parseInt(w.getdId())));
                        workersRepository.save(w);
                        System.out.println("SAVE IMPORT OBJECT"+w.getInf());
                    }
                }
            }

        }
        redirectAttributes.addAttribute("loadMessage", "upload complete!");
        return "redirect:/xmlWorkers";
    }


}
