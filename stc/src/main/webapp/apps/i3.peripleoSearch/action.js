$(document).ready(function () {
	var url = Config.servletURL;
	var peripleoURL = url + "getPeripleo";
	var baseLayer = L.tileLayer(
			'http://{s}.tiles.mapbox.com/v3/isawnyu.map-knmctlkh/{z}/{x}/{y}.png', {
				maxZoom: 15,
				attribution: "Tiles &copy; <a href='http://mapbox.com/' target='_blank'>MapBox</a> | " +
						"Data &copy; <a href='http://www.openstreetmap.org/' target='_blank'>OpenStreetMap</a> and contributors, CC-BY-SA |" +
						" Tiles and Data &copy; 2013 <a href='http://www.awmc.unc.edu' target='_blank'>AWMC</a>" +
						" <a href='http://creativecommons.org/licenses/by-nc/3.0/deed.en_US' target='_blank'>CC-BY-NC 3.0</a>"
			}
	);
	// MAP OPTIONS
	var mapY = 50.009167;
	var mapX = 4.666389;
	var mapZOOM = 5;
	var markers;
	var map = new L.Map('map', {
		center: new L.LatLng(mapY, mapX),
		zoom: mapZOOM,
		layers: [baseLayer]
	});
	// ADD SCALE AND COORDINATE VIEW
	L.control.scale().addTo(map);
	L.control.mousePosition().addTo(map);
	// ADD CONTROLS
	var maptitle = L.control({position: 'bottomleft'});
	maptitle.onAdd = function (map) {
		var div = L.DomUtil.create('div', '');
		div.id = "maptitle";
		div.innerHTML = '<span>i3.peripleoSearch</span><br>';
		div.firstChild.onmousedown = div.firstChild.ondblclick = L.DomEvent.stopPropagation;
		return div;
	};
	maptitle.addTo(map);
	var mapselect = L.control({position: 'topright'});
	mapselect.onAdd = function (map) {
		var div = L.DomUtil.create('div', '');
		div.id = "mapselect";
		div.innerHTML = '<span class="maplabel">type:</span><br>';
		div.innerHTML += '<select class="mapdropdown" id="types"><option value="place">place</option><option value="item">item</option><option value="datasets">datasets</option></select><br>';
		div.innerHTML += '<span class="maplabel">start:</span><br>';
		div.innerHTML += '<input type="text" class="mapinput" id="start" value="-100" /><br>';
		div.innerHTML += '<span class="maplabel">end:</span><br>';
		div.innerHTML += '<input type="text" class="mapinput" id="end" value="50" /><br>';
		div.innerHTML += '<span class="maplabel">limit:</span><br>';
		div.innerHTML += '<select class="mapdropdown" id="limit"><option value="100">100</option><option value="500" selected>500</option><option value="1000">1000</option><option value="2500">2500</option><option value="5000">5000</option></select><br>';
		div.innerHTML += '<input type="image" src="../img/world.png" class="button" id="show" alt="show data" title="show data">';
		div.innerHTML += '<input type="image" src="../img/clear.png" class="button" id="clear" alt="clear map" title="clear map">';
		div.firstChild.onmousedown = div.firstChild.ondblclick = L.DomEvent.stopPropagation;
		return div;
	};
	mapselect.addTo(map);
	// LOAD DATA AND SET MAP
	function getMarkergeoJSON() {
		map.spin(true, {top: '50%', left: '50%', lines: 15, length: 50, width: 15, radius: 50, opacity: 0.3, shadow: true});
		$.ajax({
			type: 'GET',
			url: peripleoURL,
			data: {
				from: $("#start").val(),
				to: $("#end").val(),
				types: $("#types").val(),
				prettyprint: "false",
				limit: $("#limit").val()
			},
			error: function (jqXHR, textStatus, errorThrown) {
				map.spin(false);
				clear();
				alert(errorThrown);
			},
			success: function (geojson) {
				try {
					geojson = JSON.parse(geojson);
				} catch (e) {
				}
				map.spin(false);
				setMarker(geojson);
				$("#clear").prop('disabled', false).css('opacity', 1).css('cursor', "pointer");
				$("#copy").prop('disabled', false).css('opacity', 1).css('cursor', "pointer");
				$("#show").prop('disabled', true).css('opacity', 0.5).css('cursor', "no-drop");
			}
		});
	}
	function setMarker(geojson) {
		markers = null;
		markers = L.markerClusterGroup();
		var marker = L.geoJson(geojson, {
			onEachFeature: onEachFeature,
			pointToLayer: function (feature, latlng) {
				return L.circleMarker(latlng, {radius: 6, fillColor: "#e75444", color: "#e75444", weight: 1, opacity: 1, fillOpacity: 1});
			}
		});
		markers.addLayer(marker);
		map.addLayer(markers);
	}
	function onEachFeature(feature, layer) {
		var popupContent = "";
		popupContent += "<b>" + feature.properties.title + "</b><br><a href='" + feature.properties.homepage + "' target='_blank'>Website</a>";
		layer.bindPopup(popupContent);
	}
	// FUNCTIONS FOR BUTTONS
	$('#clear').click(function (e) {
		clear();
	});
	$('#show').click(function (e) {
		getMarkergeoJSON();
	});
	// GENERAL FUNCTIONS
	function clear() {
		$("#clear").prop('disabled', true).css('opacity', 0.5).css('cursor', "no-drop");
		$("#show").prop('disabled', false).css('opacity', 1).css('cursor', "pointer");
		if (markers) {
			map.removeLayer(markers);
		}
		setTimeout(function () {
			map.setView([mapY, mapX], mapZOOM);
		}, 1);
	}
	clear();
});