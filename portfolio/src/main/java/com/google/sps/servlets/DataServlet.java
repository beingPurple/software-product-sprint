// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

//   @Override
//   public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
//     response.setContentType("text/html;");
//     response.getWriter().println("Hello Noelle!");
//   }
        private final List<String> commStream = new ArrayList<>();

		@Override
		public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
			Query query = new Query("Task").addSort("timestamp", SortDirection.DESCENDING);

            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            PreparedQuery results = datastore.prepare(query);

            List<String> tasks = new ArrayList<>();
            for (Entity entity : results.asIterable()) {
             long id = entity.getKey().getId();
                String comm = (String) entity.getProperty("comment");
                // long timestamp = (long) entity.getProperty("timestamp");

                // Task task = new Task(comment, timestamp);
                
                tasks.add(comm);
                

            }
            //so commstream is the most recent refresh. Upon 
            // tasks.add(commStream);

			response.setContentType("application/json;");
			//   response.getWriter().println("Hello Noelle!");
			String json = new Gson().toJson(tasks);
			// String text = getParameter(request, "text-input", "");
			System.out.println("get: " + tasks);
			response.getWriter().println(tasks);
		}
        
        @Override
		public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
			String text = getParameter(request, "textarea_field", "");//returns what is in text firld
			commStream.add(text);
            // System.out.println(text);
			System.out.println("post: " + commStream);
            long timestamp = System.currentTimeMillis();

            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            Entity taskEntity = new Entity("Task");
            taskEntity.setProperty("comment", text);
            taskEntity.setProperty("timestamp", timestamp); //adds a timestamp to the comment

            // DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            datastore.put(taskEntity);

			response.sendRedirect("/index.html");
		}

        private String getParameter(HttpServletRequest request, String name, String defaultValue) {
        String value = request.getParameter(name);
        if (value == null) {
        return defaultValue;
        }
        return value;
  }
}