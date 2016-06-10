<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<title>i3.chronOntologyGEO</title>
		<link rel="stylesheet" type="text/css" href="../css/default.css">
		<style>
		.map { width:100%; height: 100%;}
		#maptitle {float:left; width:150px; background: transparent; padding:3px; opacity: 0.7; font-size: 1.25em;}
		.legend {line-height: 18px;color: #555;}
		.legend i {width: 18px;height: 18px;float: left; margin-right: 8px;opacity: 0.7;}
		.info { padding: 6px 8px;background: white;background: rgba(255,255,255,0.8);box-shadow: 0 0 15px rgba(0,0,0,0.2);border-radius: 5px;width: auto;}
		</style>
		<!-- jQuery -->
		<script src="http://code.jquery.com/jquery-latest.js"></script>
		<!-- Config File -->
		<script src="../../Config.js"></script>
		<!-- version 0.7.5 -->
		<script src="../vendor/leaflet/leaflet-src.js"></script>
		<link rel="stylesheet" href="../vendor/leaflet/leaflet.css" />
		<!-- https://github.com/fgnass/spin.js -->
		<script src="../vendor/spin.js/spin.js"></script>
		<!-- https://github.com/makinacorpus/Leaflet.Spin -->
		<script src="../vendor/leaflet.spin/leaflet.spin.js"></script>
		<!-- https://github.com/Leaflet/Leaflet.markercluster -->
		<link rel="stylesheet" href="../vendor/leaflet.markercluster/MarkerCluster.css" />
		<link rel="stylesheet" href="../vendor/leaflet.markercluster/MarkerCluster.Default.css" />
		<script src="../vendor/leaflet.markercluster/leaflet.markercluster-src.js"></script>
		<!-- https://github.com/ardhi/Leaflet.MousePosition -->
		<link rel="stylesheet" href="../vendor/leaflet.mouseposition/L.Control.MousePosition.css" />
		<script src="../vendor/leaflet.mouseposition/L.Control.MousePosition.js"></script>
		<!-- https://github.com/Leaflet/Leaflet.fullscreen -->
		<link href='../vendor/leaflet.fullscreen/leaflet.fullscreen.css' rel='stylesheet' />
		<script src="../vendor/leaflet.fullscreen/Leaflet.fullscreen.min.js"></script>
		<!-- own action files -->
		<script src="action.js"></script>
		<script>
		var uri = "<%=request.getParameter("uri")%>";;
		</script>
	</head>
	<body>
		<div id="map" class="map"></div>
	</body>
</html>