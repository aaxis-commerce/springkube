package com.aaxis.sample;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {

    @RequestMapping(value = "/spikeCPU", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<String> spikeCPU(@RequestParam("loopSize") int loopSize) {

        System.out.println("spikeCPU called with loopSize: " + loopSize);

        ResponseEntity<String> lReturn;
        float x = 0.0001f;
        for (long i = 0; i <= loopSize; i++) {
            x += Math.sqrt(x);

        }
        return new ResponseEntity<String>(x + "", HttpStatus.OK);
    }

    @RequestMapping(value = "/spikeMemory", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<String> spikeMemory(@RequestParam("words") int words,
            @RequestParam("length") int length) {
        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < words; i++) {
            sb.append(RandomStringUtils.randomAlphanumeric(length)+"\n");
        }
        return new ResponseEntity<String>(sb.toString(), HttpStatus.OK);
    }

    @RequestMapping(value = "/sleep", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<String> sleep(@RequestParam("time") int time) {
        //ResponseEntity<String> lReturn;
        try{
            Thread.sleep(time*1000);
        }catch(Exception e){}
        
        return new ResponseEntity<String>("Feeling refreshed after " + (time*1000) + " secs", HttpStatus.OK);
    }
}