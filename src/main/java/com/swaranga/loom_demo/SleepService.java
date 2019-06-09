package com.swaranga.loom_demo;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("sleep")
public class SleepService {    
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String sleep(@QueryParam("millis") long millis) throws Exception {
		if(millis == 0) {
			millis = 100;
		}
		
		Thread.sleep(millis);
		
		return "done";
	}
}
