function createDescription(content) {
  return `<div id="content">${content}</div>`;
}

function setLocation(location) {
  if (typeof setLocation.locationsInfo === 'undefined') {
    setLocation.locationsInfo = getMap();
  }

  setLocation.locationsInfo.then(locationsInfo => {
        const coords = {
          'lng': locationsInfo['places'][location]['lng'],
          'lat': locationsInfo['places'][location]['lat']
        };
        const map = new google.maps.Map(
            document.getElementById('map'), {zoom: 10, center: coords});
        const description =
            createDescription(locationsInfo['places'][location]['description']);

        const infoWindow = new google.maps.InfoWindow({
          content: description
        });

        const marker = new google.maps.Marker({
          position: coords,
          map: map,
          title: location
        });
        infoWindow.open(map, marker);
      }
  )
}

// Initialize and add the map to page.
function initMap() {
  setLocation('default');
}

function getMap() {
  return fetch("../static/travels-data.json")
      .then(response => response.text())
      .then(JSON.parse)
      .then(addListeners);
}

function addListeners(placesInfo) {
  Object.keys(placesInfo['places']).forEach((place, _) => {
    const element = document.getElementById(place);
    if (element != null) {
      element.addEventListener('click',
          () => {
            setLocation(place);
          });
    }
  });
  return placesInfo;
}
