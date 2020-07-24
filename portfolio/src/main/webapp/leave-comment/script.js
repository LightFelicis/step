function getComments() {
  fetch("../comments").
  then(response => response.text()).
  then(JSON.parse).
  then(addCommentsToDom);
}

// Given JSON with multiple comments, modifies HTML code.
function addCommentsToDom(comments) {
  let commentSections = $();
  $.each(comments, ((index, commentJSON) => {
    commentSections = commentSections.add(
        $('<section class="comment">')
            .append($('<section class="comment-author">')
                .text(commentJSON.author))
            .append($('<section class="comment-content">')
                .text(commentJSON.content)));
  }));
  $('#comment-list').append(commentSections);
}

getComments();
