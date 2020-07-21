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

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/** Servlet that returns some example content. **/
@WebServlet("/get-comments")
public class DataServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    Comment comment1 = new Comment("author 1", "comment placeholder 1");
    Comment comment2 = new Comment("author 2", "<script>alert(\"malicious code check!\");</script>");
    List<Comment> comments = new ArrayList<>();
    comments.add(comment1);
    comments.add(comment2);
    CommentList commentList = new CommentList(comments);
    Gson gson = new Gson();    
    String commentsJSON = gson.toJson(commentList);

    response.getWriter().println(commentsJSON);
  }
}

class Comment {
  private String author;
  private String content;
  public Comment(String author, String content) {
    this.author = author;
    this.content = content;
  }

  public String getAuthor() { return author; };
  public String getContent() { return content; };
}

class CommentList {
  private List<Comment> comments;
  public CommentList(List<Comment> comments) {
    this.comments = new ArrayList(comments);
  }

  public List<Comment> getComments() { return new ArrayList(comments); }
}