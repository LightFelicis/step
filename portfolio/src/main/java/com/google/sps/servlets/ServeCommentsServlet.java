// Copyright 2020 Google LLC
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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns comments stored in database. **/
@WebServlet("/get-comments")
public class ServeCommentsServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    Gson gson = new Gson();

    CommentList comments = new CommentList(prepareComments());
    String commentsJSON = gson.toJson(comments);
    response.getWriter().println(commentsJSON);
  }

  private List<Comment> prepareComments() {
    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<Comment> comments = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      String authorEmail = (String) entity.getProperty("email");
      String content = (String) entity.getProperty("content");
      Comment comment = new Comment(authorEmail, content);
      comments.add(comment);
    }

    return comments;
  }
}

class Comment {
  private String author;
  private String content;

  public Comment(String author, String content) {
    this.author = author;
    this.content = content;
  }

  public String getAuthor() {
    return author;
  };

  public String getContent() {
    return content;
  };
}

class CommentList {
  private List<Comment> comments;

  public CommentList(List<Comment> comments) {
    this.comments = new ArrayList(comments);
  }

  public List<Comment> getComments() {
    return new ArrayList(comments);
  }
}
