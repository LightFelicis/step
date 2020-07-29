function onSignIn(googleUser) {
  if ($('#sign-out').length != 0) {
    return;
  }

  let loggedUser = googleUser.getAuthResponse().id_token;
  addUserTokenIdToForm(loggedUser);

  $('#leave-comment')
      .append(
          $('<a></a>')
              .attr('id', 'sign-out')
              .attr('href', '#')
              .click(signOut)
              .addClass('button')
              .text("Sign out")
      );

  $('#leave-comment-form')
      .css('visibility', 'visible');
}


function addUserTokenIdToForm(userTokenId) {
  if ($('#userTokenId').length != 0) {
    $('#userTokenId').val(userTokenId);
    return;
  }

  $('<input />')
      .attr('type', 'hidden')
      .attr('name', 'userTokenId')
      .val(userTokenId)
      .attr('id', 'userTokenId')
      .appendTo('#leave-comment-form');
}

function signOut() {
  var auth2 = gapi.auth2.getAuthInstance();
  auth2.signOut().then(function () {
    console.log('User signed out.');
  });
  $("#sign-out").remove();
  $('#leave-comment-form')
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

