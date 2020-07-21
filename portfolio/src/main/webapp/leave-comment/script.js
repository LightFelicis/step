function getComments() {
  console.log("Downloading comments.");
  const responsePromise = fetch("/get-comments");
  responsePromise.then(handleResponse);
}

function handleResponse(response) {
  console.log('Handling the response.');

  // response.text() returns a Promise, because the response is a stream of
  // content and not a simple variable.
  const textPromise = response.text();
  const JSONPromise = textPromise.then(JSON.parse);
  // When the response is converted to text, pass the result into the
  // addCommentsToDom() function.
  JSONPromise.then(addCommentsToDom);
}

function addCommentToHTML(commentJSON) {
  $('#comment-list')
  .append($('<div class="comment-content"></div>').text(commentJSON.content))
  .append($('<div class="comment-author"></div>').text(commentJSON.author));

  $('#comment-list > .comment-content').each(function(){
    $(this).next('.comment-author').andSelf().wrapAll('<div class="comment"></div>');
  });
}

// Given JSON with multiple comments, modifies HTML code.
function addCommentsToDom(comments) {
  for (let comment of comments.comments) {
    console.log(`${comment.author}`);
    addCommentToHTML(comment);
  }
}

getComments();