$(document).ready(function () {
	var url = Config.servletURL;
	var chronontologyURL = "http://localhost:8084/spatialapi/geowidget";
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
	var mapZOOM = 2;
	var markers;
	var map = new L.Map('map', {
		fullscreenControl: {
			pseudoFullscreen: false
		},
		center: new L.LatLng(mapY, mapX),
		zoom: mapZOOM,
		layers: [baseLayer]
	});
	// ADD SCALE AND COORDINATE VIEW

	L.control.scale().addTo(map);

	var legend = L.control({position: 'topright'});
	legend.onAdd = function (map) {
		var div = L.DomUtil.create('div', 'info legend');
		var types = ["spatiallyPartOfRegion", "namedAfter", "hasCoreRegion", "undefinedRegion"];
		var colors = ["#ff0000", "#0000ff", "#00ff00", "#9214ff"];
		for (var i = 0; i < types.length; i++) {
			div.innerHTML += '<i style="background:' + colors[i] + '"></i> ' + types[i] + '<br>';
		}
		return div;
	};
	legend.addTo(map);

	// ADD CONTROLS
	getMarkergeoJSON();
	// LOAD DATA AND SET MAP
	function getMarkergeoJSON() {
		console.log("asd");
		map.spin(true, {top: '50%', left: '50%', lines: 15, length: 50, width: 15, radius: 50, opacity: 0.3, shadow: true});
		$.ajax({
			type: 'GET',
			url: chronontologyURL,
			data: {
				uri: encodeURIComponent(uri)
			},
			error: function (jqXHR, textStatus, errorThrown) {
				map.spin(false);
				console.log(errorThrown);
			},
			success: function (geojson) {
				try {
					geojson = JSON.parse(geojson);
				} catch (e) {
				}
				map.spin(false);
				setMarker(geojson);
			}
		});
	}
	var spatiallyPartOfRegion = {
		"color": "#ff0000",
		"fillColor": "#ff0000",
		"weight": 3,
		"opacity": 0.7,
		"fillOpacity": 0.3
	};
	var namedAfter = {
		"color": "#00ff00",
		"fillColor": "#00ff00",
		"weight": 3,
		"opacity": 0.7,
		"fillOpacity": 0.3
	};
	var hasCoreRegion = {
		"color": "#ff34b3",
		"fillColor": "#ff34b3",
		"weight": 3,
		"opacity": 0.7,
		"fillOpacity": 0.3
	};
	var undefinedRegion = {
		"color": "#9214ff",
		"fillColor": "#9214ff",
		"weight": 1,
		"opacity": 1.0,
		"fillOpacity": 0.8
	};
	var popupinfo = [];
	function setMarker(geojson) {
		markers = null;
		markers = L.markerClusterGroup();
		var marker = L.geoJson(geojson, {
			onEachFeature: onEachFeature,
			style: function (feature) {
				if (feature.geometry.type != "Point") {
					if (feature.properties.relation === "spatiallyPartOfRegion") {
						return spatiallyPartOfRegion;
					} else if (feature.properties.relation === "namedAfter") {
						return namedAfter;
					} else if (feature.properties.relation === "hasCoreRegion") {
						return hasCoreRegion;
					} else if (feature.properties.relation === "undefined") {
						return undefinedRegion;
					} else {
						return spatiallyPartOfRegion;
					}
				}
			},
			pointToLayer: function (feature, latlng) {
				if (feature.properties.relation === "spatiallyPartOfRegion") {
					return L.circleMarker(latlng, {radius: 8, fillColor: "#ffffff", color: "#ff0000", weight: 5, opacity: 1, fillOpacity: 1});
				} else if (feature.properties.relation === "namedAfter") {
					return L.circleMarker(latlng, {radius: 8, fillColor: "#ffffff", color: "#0000ff", weight: 5, opacity: 1, fillOpacity: 1});
				} else if (feature.properties.relation === "hasCoreRegion") {
					return L.circleMarker(latlng, {radius: 8, fillColor: "#ffffff", color: "#ff34b3", weight: 5, opacity: 1, fillOpacity: 1});
				} else {
					return L.circleMarker(latlng, {radius: 8, fillColor: "#ffffff", color: "#ff0000", weight: 5, opacity: 1, fillOpacity: 1});
				}
			}
		});
		markers.addLayer(marker);
		map.addLayer(markers);
		var popupContent = "";
		for (var i=0; i< popupinfo.length; i++) {
			var split = popupinfo[i].split(";");
			popupContent += "<a href='" + split[1] + "' target='_blank'>"+split[0]+"</a><br>";
		}
		marker.bindPopup(popupContent);
	}
	function onEachFeature(feature, layer) {
		popupinfo.push(feature.properties.name + ";" + feature.properties.homepage);
	}
});