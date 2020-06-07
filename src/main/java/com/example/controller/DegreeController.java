package com.example.controller;

import com.example.domain.Degree;
import com.example.domain.Worker;
import com.example.repos.DegreesRepository;
import com.example.repos.WorkersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class DegreeController {
    @Autowired
    private DegreesRepository degreesRepository;
    @Autowired
    private WorkersRepository workerRepository;
    @GetMapping("/degrees")
    public String showDegrees(Model model)
    {
        model.addAttribute("degrees",degreesRepository.findAll());
        return "degrees";
    }
    @GetMapping("/addDegree")
    public String showAdd(Model model)
    {
        model.addAttribute("degreeForm",new Degree());
        return "addDegree";
    }
    @PostMapping("/addDegree")
    public String addDegree(Model model,@ModelAttribute("degreeForm") Degree newDegree)
    {   List<Degree> degrees=(List<Degree>)degreesRepository.findAll();
        System.out.println(newDegree);
        if(newDegree!=null&&newDegree.getDegree().length()>0) {
            int max=0;
            for(Degree d:degrees)
            {
                if(d.getId()>max)
                    max=d.getId();
            }
            Degree newInstance=new Degree(max+1);
            newInstance.setDegree(newDegree.getDegree());
            degreesRepository.save(newInstance);
            return "redirect:/degrees";
        }
        model.addAttribute("errorMessage", "all fields must be filled in correctly");
        model.addAttribute("degreeForm",new Degree());
        return "addDegree";

    }
    @Transactional
    @GetMapping("/deleteDegree")
    public String delete(Model model,@RequestParam(name="id", required=false) String id)
    {

        int deleteId=Integer.parseInt(id);
        degreesRepository.deleteById(deleteId);
        System.out.println("DEGREE BY ID"+deleteId+" DELETED");
        return "redirect:/degrees";

    }
    @GetMapping("/updateDegree")
    public String showUpdate(Model model,@RequestParam(name="id", required=false) String id)
    {
        Degree degree=degreesRepository.findById(Integer.parseInt(id));
        model.addAttribute("degreeForm",degree);

        return "updateDegree";
    }
    @PostMapping("updateDegree")
    public String update(Model model,@ModelAttribute("degreeForm") Degree degree) {

        if (degree!=null&&degree.getDegree().length()>0) {

            degreesRepository.save(degree);
            System.out.println("DEGREE"+degree.getDegree()+degree.getId()+" UPDATED");
            return "redirect:/degrees";
        }
        model.addAttribute("worker",degree);
        model.addAttribute("errorMessage", "all fields must be filled in correctly");
        return "updateDegree";

    }
}
