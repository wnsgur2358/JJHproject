let mapContainer = document.getElementById('map');
let address = /*[[${event.eventAddress}]]*/ '';
let mapOption = {
    center: new kakao.maps.LatLng(33.450701, 126.570667),
    level: 3
};
let map = new kakao.maps.Map(mapContainer, mapOption);
let geocoder = new kakao.maps.services.Geocoder();
geocoder.addressSearch(address, function(result, status) {
    if (status === kakao.maps.services.Status.OK) {
        let coords = new kakao.maps.LatLng(result[0].y, result[0].x);
        let marker = new kakao.maps.Marker({ map: map, position: coords });
        map.setCenter(coords);
    }
});