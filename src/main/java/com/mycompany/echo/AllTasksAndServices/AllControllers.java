package com.mycompany.echo.AllTasksAndServices;

import com.mycompany.echo.AllModels.BuildInfoModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
public class AllControllers {
    @Autowired
    BuildInfoModel buildInfoModel;

    @PostMapping("/build")
    @ResponseBody
    public void buildInfo(@RequestParam String chart_name,@RequestParam String chart_release_name,@RequestParam String branch,@RequestParam String mode){
        System.out.println(chart_name);
        System.out.println(chart_release_name);

    }
}
