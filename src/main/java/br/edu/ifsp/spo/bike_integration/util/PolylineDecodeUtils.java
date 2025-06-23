package br.edu.ifsp.spo.bike_integration.util;

import java.util.ArrayList;
import java.util.List;

public interface PolylineDecodeUtils {
    /**
     * Decodes a Google Maps encoded polyline into a list of coordinates.
     *
     * @param polyline the encoded polyline string
     * @return a list of coordinates, where each coordinate is a list containing
     *         latitude and longitude
     */

    public static List<List<Double>> decode(String polyline) {
        List<List<Double>> coordinates = new ArrayList<>();
        int index = 0, len = polyline.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = polyline.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = polyline.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            List<Double> coord = new ArrayList<>();
            coord.add(lat / 1E5);
            coord.add(lng / 1E5);
            coordinates.add(coord);
        }
        return coordinates;
    }
}
