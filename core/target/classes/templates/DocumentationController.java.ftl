package ${packageName}.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DocumentationController {
    
    @GetMapping({"${redirectFrom}", "${redirectFrom}/"})
    public String redirectToIndex() {
        return "redirect:${redirectTo}";
    }
} 