function onSignIn(googleUser) {
  $('#userTokenId').val(googleUser.getAuthResponse().id_token);
  $('#show-on-sign-in')
      .css('visibility', 'visible');
  $('#greeting').text('Welcome, ' + googleUser.getBasicProfile().getName() + '!');
}

function signOut() {
  var auth2 = gapi.auth2.getAuthInstance();
  auth2.signOut().then(function () {
    console.log('User signed out.');
  });
  $('#show-on-sign-in')
      .css('visibility', 'hidden');
  $("#userTokenId").val('');
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

