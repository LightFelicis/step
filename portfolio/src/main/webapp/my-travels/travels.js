const placesCoords = {
    'berlin' : {lat: 52.521999, lng: 13.413016},
    'default' : {lat: 52.048947, lng: 20.442758},
    'disneyland' : {lat: 48.869486, lng: 2.780874},
    'krakow' : {lat: 50.061683, lng: 19.937111},
    'kolobrzeg' : {lat: 54.186629, lng: 15.570729},
    'matterhorn' : {lat: 45.938319, lng: 7.731824},
    'zurich' : {lat: 47.349534, lng: 8.491872}
    }

const placesDescriptions = {
    'berlin' : 'My last trip was to Berlin. The photo was taken just after ' +
        'going shopping, I\'m a huge fan of it, as you can tell from my face.',
    'default' : 'This is where I live -- ' +
        'a small town in the centre of Poland.',
    'disneyland' : 'For my 19th birthday I visited France. ' +
        'The picture was taken at Disneyland.',
    'krakow' : 'Kraków is previous capital of Poland and ' +
        'one of the most beautiful cities I have ever seen.',
    'kolobrzeg' : 'This is a photo of me and Marek at the beach, ' +
        'watching sunset.',
    'zurich' : 'This is a photo from my first trip to Zuerich, ' +
        'taken at the top of Uetliberg mountain',
    'matterhorn' : 'This is me in front of Matterhorn mountain, ' +
        'known from Toblerone.'
    }

function createDescription(content) {
  return '<div id="content">' +
      '<div id="siteNotice">' +
      '</div>' +
      '<div id="bodyContent">' +
      content +
      '</div>' +
      '</div>';
}

function setLocation(location) {
  const coords = placesCoords[location];
  const map = new google.maps.Map(
      document.getElementById('map'), {zoom: 10, center: coords});
  const description = createDescription(placesDescriptions[location]);

  let infowindow = new google.maps.InfoWindow({
    content: description
  });
  const marker = new google.maps.Marker(
      {position: coords,
       map: map,
       title: location});
  infowindow.open(map, marker);
}

// Initialize and add the map to page.
function initMap() {
  setLocation('default');
}

Object.keys(placesCoords).forEach((place, _) => {
  const element = document.getElementById(place);
  if (element != null) {
    element.addEventListener('click',
    () => { setLocation(place); });
  }
});
