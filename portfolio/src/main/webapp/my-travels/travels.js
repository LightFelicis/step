const placesCoords = {
                    "berlin" : {lat: 52.521999, lng: 13.413016},
                    "default" : {lat: 52.048947, lng: 20.442758},
                    "disneyland" : {lat: 48.869486, lng: 2.780874},
                    "krakow" : {lat: 50.061683, lng: 19.937111},
                    "kolobrzeg" : {lat: 54.186629, lng: 15.570729},
                    "matterhorn" : {lat: 45.938319, lng: 7.731824},
                    "zurich" : {lat: 47.349534, lng: 8.491872}
                   }

function setLocation(location) {
  let coords = placesCoords[location];
  let map = new google.maps.Map(
      document.getElementById('map'), {zoom: 10, center: coords});
  let marker = new google.maps.Marker({position: coords, map: map});
}

// Initialize and add the map to page.
function initMap() {
  setLocation("default");
}

Object.keys(placesCoords).forEach((place, _) => {
  let element = document.getElementById(place);
  if (element != null) {
    element.addEventListener("click",
    () => { setLocation(place); });
  }
});
