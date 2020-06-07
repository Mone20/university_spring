package com.example.controller;

import com.example.domain.Degree;
import com.example.domain.Filter;
import com.example.domain.Position;
import com.example.domain.Worker;
import com.example.repos.DegreesRepository;
import com.example.repos.PositionRepository;
import com.example.repos.WorkersRepository;
import com.example.specifitations.WorkerSpecifitation;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.data.jpa.domain.Specification.where;

@Controller
public class WorkersController {
    @Autowired
    private WorkersRepository workerRepository;
    @Autowired
    private PositionRepository positionRepository;
    @Autowired
    private DegreesRepository degreesRepository;
    @GetMapping
    public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {

        return "main";
    }
    @RequestMapping(value = { "/workers/search" }, method = RequestMethod.POST,name="searchByName")
    public String searchByName(Model model,@ModelAttribute("name") String lastName)
    {
        if(lastName!=null&&lastName.length()>0)
        {
            model.addAttribute("workers",workerRepository.findByLastName(lastName));
        }
        else
            model.addAttribute("workers",workerRepository.findAll());

        model.addAttribute("positions",positionRepository.findAll());
        model.addAttribute("degrees",degreesRepository.findAll());
        model.addAttribute("filter",new Filter());
        return "workers";

    }
    @GetMapping("/workers")
    public String show(Model model)
    {
        model.addAttribute("positions",positionRepository.findAll());
        model.addAttribute("degrees",degreesRepository.findAll());
        model.addAttribute("workers",workerRepository.findAll());
        model.addAttribute("filter",new Filter());
        return "workers";
    }
    @PostMapping("/workers")
    public String filter(Model model, @ModelAttribute("filter") Filter filter)
    {
        Iterable<Worker> workers=workerRepository.findAll(where(WorkerSpecifitation.workersWith(new Pair("pId",filter.getFirstFilter()))
                .and(WorkerSpecifitation.workersWith(new Pair("dId",filter.getSecondFilter())))));
        model.addAttribute("positions",positionRepository.findAll());
        model.addAttribute("degrees",degreesRepository.findAll());
        model.addAttribute("workers", workers);
        model.addAttribute("filter",new Filter());
        return "workers";
    }
    @GetMapping("/updateWorker")
    public String showUpdate(Model model,@RequestParam(name="id", required=false) String id)
    {
        List<Position> positions=(List)positionRepository.findAll();
        List<Degree> degrees=(List)degreesRepository.findAll();
        List<Worker> workers=(List)workerRepository.findAll();
        Worker worker=workerRepository.findById(Integer.parseInt(id));
        positions.remove(worker.getPosition());
        degrees.remove(worker.getDegree());
        workers.remove(worker);
        for(Worker w:workerRepository.findByParentId(worker.getId()))
            workers.remove(w);
        model.addAttribute("positions",positions);
        model.addAttribute("degrees",degrees);
        model.addAttribute("workers",workers);
        model.addAttribute("worker",worker);

        return "updateWorker";
    }
    @PostMapping("updateWorker")
    public String update(Model model,@ModelAttribute("worker") Worker newWorker) {
        Iterable<Worker> workers = workerRepository.findAll();
        model.addAttribute("workers", workers);
        model.addAttribute("positions",positionRepository.findAll());
        model.addAttribute("degrees",degreesRepository.findAll());
        if (!newWorker.isEmpty()) {
            if(newWorker.getParentId()<0)
                newWorker.deleteBoss();
            if(Integer.parseInt(newWorker.getpId())!=-1)
            newWorker.setPosition(positionRepository.findById(Integer.parseInt(newWorker.getpId())));
            if(Integer.parseInt(newWorker.getdId())!=-1)
            newWorker.setDegree(degreesRepository.findById(Integer.parseInt(newWorker.getdId())));
            workerRepository.save(newWorker);
            System.out.println("OBJECT"+newWorker.getInf()+" UPDATED");
            return "redirect:/workers";
        }
        model.addAttribute("workers",workers);
        model.addAttribute("worker",newWorker);
        model.addAttribute("errorMessage", "all fields must be filled in correctly");
        return "updateWorker";


    }
    @GetMapping("/addWorker")
    public String showAdd(Model model)
    {
        Iterable<Position> positions=positionRepository.findAll();
        Iterable<Degree> degrees= degreesRepository.findAll();
        Iterable<Worker> workers=workerRepository.findAll();
        Worker workerForm=new Worker();
        model.addAttribute("positions",positions);
        model.addAttribute("degrees",degrees);
        model.addAttribute("workers",workers);
        model.addAttribute("workerForm",workerForm);
        return "addWorker";
    }
    @PostMapping("/addWorker")
    public String add(Model model, @ModelAttribute("workerForm") Worker newInstance)
    {

        Iterable<Worker> workers=workerRepository.findAll();
        int max=0;
        for(Worker w:workers)
        {
            if(w.getId()>max)
                max=w.getId();
        }
        if (!newInstance.isEmpty()) {
            if(newInstance.getParentId()<0)
                newInstance.deleteBoss();
            newInstance.setId(max+1);
            if(Integer.parseInt(newInstance.getpId())>0)
            newInstance.setPosition(positionRepository.findById(Integer.parseInt(newInstance.getpId())));

            if(Integer.parseInt(newInstance.getdId())>0)
            newInstance.setDegree(degreesRepository.findById(Integer.parseInt(newInstance.getdId())));
            workerRepository.save(newInstance);
            model.addAttribute("workers",workers);
            model.addAttribute("positions",positionRepository.findAll());
            model.addAttribute("degrees",degreesRepository.findAll());
            System.out.println("OBJECT "+newInstance.getInf()+" ADDED");
            return "redirect:/workers";
        }

        model.addAttribute("errorMessage", "all fields must be filled in correctly");
        model.addAttribute("workerForm",new Worker(max+1));
        return "addWorker";
    }
@GetMapping("/info")
    public String showInfo(Model model,@RequestParam(name="id", required=false) String id)
{
    if(workerRepository.existsById(Integer.parseInt(id))) {
        Worker worker = workerRepository.findById(Integer.parseInt(id));
        model.addAttribute("worker", worker);
        if (worker.getParentId() != null) {
            int parentId = worker.getParentId();
            model.addAttribute("boss", workerRepository.findById(parentId));
        }
        return "info";
    }
    return "redirect:/workers";

}
@GetMapping("/delete")
    public String delete(Model model,@RequestParam(name="id", required=false) String id)
{

    int deleteId=Integer.parseInt(id);
    workerRepository.deleteById(deleteId);
    System.out.println("OBJECT BY ID"+deleteId+" DELETED");
    Iterable<Worker> workers=workerRepository.findAll();
    for(Worker w:workers)
    {
        if(w.getParentId()!=null) {
            if (w.getParentId() == deleteId) {
                w.deleteBoss();
                if (w.getParentId() == null)
                    System.out.println("boss deleted");

            }
        }
    }
    workerRepository.saveAll(workers);
    return "redirect:/workers";

}
@GetMapping("groupFilter")
    public String showGroupFilter(Model model)
{
    model.addAttribute("positions",positionRepository.findAll());
    model.addAttribute("degrees",degreesRepository.findAll());
    model.addAttribute("groupFilter",new Filter());
    return "groupFilter";

}
@PostMapping("groupFilter")
    public String groupFilter(Model model,@ModelAttribute("groupFilter") Filter groupFilter,@ModelAttribute("salery") String salery)
{
    Iterable<Worker> changeWorkers;
    int firstFilter=Integer.parseInt(groupFilter.getFirstFilter());
    int secondFilter=Integer.parseInt(groupFilter.getSecondFilter());
    changeWorkers=workerRepository.findAll(where(WorkerSpecifitation.workersWith(new Pair("pId",groupFilter.getFirstFilter()))
            .and(WorkerSpecifitation.workersWith(new Pair("dId",groupFilter.getSecondFilter())))));
    Integer changedWorker=Integer.parseInt(salery);
    for(Worker w:changeWorkers)
        w.setSalery(changedWorker);

    workerRepository.saveAll(changeWorkers);
    String first=firstFilter>0?positionRepository.findById(firstFilter).getPosition():"EMPTY";
    String second=secondFilter>0?degreesRepository.findById(secondFilter).getDegree():"EMPTY";
    System.out.println("GROUP"+" POSITION:"+first+" DEGREE:"
    +second+" CHANGED SALERY ON "+changedWorker);
    return "redirect:/workers";
}
}