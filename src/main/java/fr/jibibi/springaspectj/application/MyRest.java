package fr.jibibi.springaspectj.application;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyRest {
    
    @RequestMapping("/test")
    public String startComputation(){
        this.firstMethod();
        return "success";
    }

    @LogMe("firstMethod")
    public void firstMethod(){
        System.out.println("Method 1");
        this.firstFirstMethod();
        this.firstSecondMethod();
    }

    @LogMe("firstFirstMethod")
	private void firstFirstMethod() {
        System.out.println("Method 1.1");
    }
    
    @LogMe("firstSecondMethod")
	private void firstSecondMethod() {
        System.out.println("Method 1.2");
	}
    
}