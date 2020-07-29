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

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import java.io.IOException;
import java.lang.IllegalArgumentException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.security.GeneralSecurityException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns comments stored in database. **/
@WebServlet("/comments")
public class ServeCommentsServlet extends HttpServlet {
  private static final String CLIENT_ID = "810678295196-nls1qkpmf8pju0gu9bjb6j4bdkqkbfdu.apps.googleusercontent.com";
  private GoogleIdTokenVerifier verifier;
  
  @Override
  public void init() {
    verifier = new GoogleIdTokenVerifier.Builder(
        new NetHttpTransport(),
        JacksonFactory.getDefaultInstance())
        .setAudience(Collections.singletonList(CLIENT_ID))
        .build();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    Gson gson = new Gson();

    List<Comment> comments = prepareComments();
    String commentsJSON = gson.toJson(comments);
    response.getWriter().println(commentsJSON);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String content = request.getParameter("content");
    String userTokenId = extractUserTokenId(request);

    try {
      GoogleIdToken idToken = validateUserIdToken(userTokenId);
      System.out.println("Token ID is valid.");
      addCommentToDb(idToken, content);
      response.sendRedirect("/leave-comment/leave-comment.html");
    } catch (GeneralSecurityException e) {
      System.out.println("Token ID was not valid.");
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    }
  }

  private static String extractUserTokenId(HttpServletRequest request) {
    String userTokenId = request.getParameter("userTokenId");
    if (userTokenId == null) {
      throw new IllegalArgumentException("User token ID is empty.");
    }
    return userTokenId;
  }

  private void addCommentToDb(GoogleIdToken idToken, String content) {
    String email = idToken.getPayload().getEmail();
    long timestamp = System.currentTimeMillis();
    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("email", email);
    commentEntity.setProperty("content", content);
    commentEntity.setProperty("timestamp", timestamp);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);
  }

  private GoogleIdToken validateUserIdToken(String idTokenString) throws GeneralSecurityException {
    GoogleIdToken idToken;
    try {
      idToken = verifier.verify(idTokenString);
    } catch (IOException e) {
      throw new GeneralSecurityException("User token ID is not valid");
    }

    if (idToken == null) {
      throw new GeneralSecurityException("User token ID is not valid");
    }

    return idToken;
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
