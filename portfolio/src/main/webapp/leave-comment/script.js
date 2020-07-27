function onSignIn(googleUser) {
  let loggedUser = googleUser.getAuthResponse().id_token;
  $('<input />').attr('type', 'hidden')
    .attr('name', 'userTokenId')
    .attr('value', loggedUser)
    .appendTo('#leave-comment-form');

  $('#leave-comment')
      .append($('<a></a>')
          .attr('id', 'sign-out')
          .attr('href', '#')
          .attr('onclick', 'signOut();')
          .addClass('button')
          .text("Sign out")
      );
  document.getElementById("leave-comment-form").style.visibility = 'visible';
}

function signOut() {
  var auth2 = gapi.auth2.getAuthInstance();
  auth2.signOut().then(function () {
    console.log('User signed out.');
  });
  $("[name=userTokenId]").remove();
  $("#sign-out").remove();
  document.getElementById("leave-comment-form").style.visibility = 'hidden';
}

function getComments() {
  fetch("../comments")
    .then(response => response.text())
    .then(JSON.parse)
    .then(addCommentsToDom);
}

// Given JSON with multiple comments, modifies HTML code.
function addCommentsToDom(comments) {
  let commentSections = $();
  $.map(comments, ((commentJSON, index) => {
    commentSections = commentSections.add(
        $('<section class="comment">')
            .append($('<section class="comment-author">')
                .text(commentJSON.author))
            .append($('<section class="comment-content">')
                .text(commentJSON.content))
            .append($('<section class="comment-end">')));
  }));
  $('#comment-list').append(commentSections);
}

getComments();

