var auth2; // The Sign-In object.
var googleUser; // The current user.

function init() {
  gapi.load('auth2', initSignin);
}

var initSignin = function() {
  auth2 = gapi.auth2.getAuthInstance();
  auth2.attachClickHandler($('.g-signin2')[0], {prompt: "select_account"});
  auth2.currentUser.listen(userChanged);

  if (auth2.isSignedIn.get() == true) {
    auth2.signIn();
  }
};

function signOut() {
  auth2.signOut();
}

function userChanged(user) {
  googleUser = user;
  if (googleUser.isSignedIn()) {
    $('#userTokenId').val(googleUser.getAuthResponse().id_token);
    $('#show-on-sign-in').css('visibility', 'visible');
    $('#greeting').text('Welcome, ' + googleUser.getBasicProfile().getName() + '!');
  } else {
    $("#userTokenId").val('');
    $('#show-on-sign-in').css('visibility', 'hidden');
    $('#greeting').text('Please log in to share your feedback!');
  }
};

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

