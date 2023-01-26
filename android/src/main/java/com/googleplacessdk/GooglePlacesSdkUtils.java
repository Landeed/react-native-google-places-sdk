package com.googleplacessdk;

import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

class GooglePlacesSdkUtils {
  static LatLng ParseCoordinates(ReadableMap coordinates) {
    if (!coordinates.hasKey("latitude") || coordinates.hasKey("longitude")) {
      return null;
    }

    return new LatLng(coordinates.getDouble("latitude"), coordinates.getDouble("longitude"));
  }

  static RectangularBounds ParseLocationBounds(ReadableMap bounds) {
    if (!bounds.hasKey("northEast") || !bounds.hasKey("southWest")) {
      return null;
    }

    LatLng northEast = ParseCoordinates(bounds.getMap("northEast"));
    LatLng southWest = ParseCoordinates(bounds.getMap("southWest"));

    if (northEast == null || southWest == null) return null;

    return RectangularBounds.newInstance(northEast, southWest);
  }

  static FindAutocompletePredictionsRequest buildPredictionRequest(String query, ReadableMap options) {
    FindAutocompletePredictionsRequest.Builder builder = FindAutocompletePredictionsRequest.builder();
    if (options.hasKey("types")) {
      ArrayList types = options.getArray("types").toArrayList();
      builder.setTypesFilter(types);
    }

    if (options.hasKey("countries")) {
      ArrayList countries = options.getArray("countries").toArrayList();
      builder.setCountries(countries);
    }

    if (options.hasKey("locationBias")) {
      RectangularBounds bounds = ParseLocationBounds(options.getMap("locationBias"));
      if (bounds != null) builder.setLocationBias(bounds);
    }

    if (options.hasKey("locationRestriction")) {
      RectangularBounds bounds = ParseLocationBounds(options.getMap("locationRestriction"));
      if (bounds != null) builder.setLocationRestriction(bounds);
    }

    if (options.hasKey("origin")) {
      LatLng origin = ParseCoordinates(options.getMap("origin"));
      if (origin != null) builder.setOrigin(origin);
    }

    return builder
      .setQuery(query)
      .build();
  }

  static WritableArray ParsePlaceTypes(List<Place.Type> types) {
    WritableArray parsedTypes = Arguments.createArray();
    for (Place.Type placeType : types) {
      parsedTypes.pushString(placeType.toString().toLowerCase(Locale.ROOT));
    }

    return parsedTypes;
  }

  static WritableMap ParseAutocompletePrediction(AutocompletePrediction prediction) {
    WritableMap map = Arguments.createMap();

    map.putString("placeId", prediction.getPlaceId());
    map.putString("description", prediction.getFullText(null).toString());
    map.putString("primaryText", prediction.getPrimaryText(null).toString());
    map.putArray("types", ParsePlaceTypes(prediction.getPlaceTypes()));

    if (prediction.getDistanceMeters() != null) {
      map.putDouble("distanceMeters", prediction.getDistanceMeters());
    } else map.putNull("distanceMeters");

    if (prediction.getSecondaryText(null) != null) {
      map.putString("secondaryText", prediction.getSecondaryText(null).toString());
    } else map.putNull("secondaryText");

    return map;
  }

  static WritableArray ParseAutocompletePredictions(List<AutocompletePrediction> predictions) {
    WritableArray parsedPredictions = Arguments.createArray();
    for (AutocompletePrediction prediction : predictions) {
      parsedPredictions.pushMap(ParseAutocompletePrediction(prediction));
    }

    return parsedPredictions;
  }

  static List<Place.Field> ParsePlaceFields(ReadableArray fields) {
    Map<String, Place.Field> fieldMap = new HashMap<>();
    fieldMap.put("name", Place.Field.NAME);
    fieldMap.put("placeID", Place.Field.ID);
    fieldMap.put("plusCode", Place.Field.PLUS_CODE);
    fieldMap.put("coordinate", Place.Field.LAT_LNG);
    fieldMap.put("openingHours", Place.Field.OPENING_HOURS);
    fieldMap.put("phoneNumber", Place.Field.PHONE_NUMBER);
    fieldMap.put("types", Place.Field.TYPES);
    fieldMap.put("priceLevel", Place.Field.PRICE_LEVEL);
    fieldMap.put("website", Place.Field.WEBSITE_URI);
    fieldMap.put("viewport", Place.Field.VIEWPORT);
    fieldMap.put("addressComponents", Place.Field.ADDRESS_COMPONENTS);
    fieldMap.put("photos", Place.Field.PHOTO_METADATAS);
    fieldMap.put("userRatingsTotal", Place.Field.USER_RATINGS_TOTAL);
    fieldMap.put("utcOffsetMinutes", Place.Field.UTC_OFFSET);
    fieldMap.put("businessStatus", Place.Field.BUSINESS_STATUS);
    fieldMap.put("iconImageURL", Place.Field.ICON_URL);

    ArrayList<Place.Field> placeFields = new ArrayList<>();
    for (int i = 0; i < fields.size(); i++) {
      String field = fields.getString(i);
      if (fieldMap.containsKey(field)) {
        placeFields.add(fieldMap.get(field));
      }
    }

    return placeFields;
  }

  static WritableMap ParseLatLng(LatLng latLng) {
    WritableMap map = Arguments.createMap();
    map.putDouble("latitude", latLng.latitude);
    map.putDouble("longitude", latLng.longitude);

    return map;
  }

  static WritableMap ParsePlace(Place place) {
    WritableMap placeInfo = Arguments.createMap();

    placeInfo.putString("name", place.getName());
    placeInfo.putString("placeID", place.getId());
    placeInfo.putString("phoneNumber", place.getPhoneNumber());

    if (place.getPriceLevel() != null) {
      placeInfo.putInt("priceLevel", place.getPriceLevel());
    } else placeInfo.putNull("priceLevel");

    if (place.getOpeningHours() != null) {
      Log.d("hasOpeningHours", "has");
      placeInfo.putString("openingHours", place.getOpeningHours().getWeekdayText().toString());
    } else placeInfo.putNull("openingHours");

    if (place.getTypes() != null) {
      placeInfo.putArray("types", ParsePlaceTypes(place.getTypes()));
    } else placeInfo.putNull("types");

    if (place.getLatLng() != null) {
      WritableMap coordinate = ParseLatLng(place.getLatLng());
      placeInfo.putMap("coordinate", coordinate);
    } else placeInfo.putNull("coordinate");

    LatLngBounds viewport = place.getViewport();
    if (viewport != null) {
      WritableMap viewportMap = Arguments.createMap();
      viewportMap.putMap("northEast", ParseLatLng(viewport.northeast));
      viewportMap.putMap("southWest", ParseLatLng(viewport.southwest));
      placeInfo.putMap("viewport", viewportMap);
    } else placeInfo.putNull("viewport");

    return placeInfo;
  }
}
