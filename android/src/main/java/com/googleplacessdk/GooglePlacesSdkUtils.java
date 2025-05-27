package com.googleplacessdk;

import android.os.Parcel;
import android.util.Patterns;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.AddressComponent;
import com.google.android.libraries.places.api.model.AddressComponents;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.CircularBounds;
import com.google.android.libraries.places.api.model.LocationRestriction;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlusCode;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.SearchByTextRequest;
import com.google.android.libraries.places.api.net.SearchNearbyRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;

class GooglePlacesSdkUtils {
  static LatLng ParseCoordinates(ReadableMap coordinates) {
    if (!coordinates.hasKey("latitude") || !coordinates.hasKey("longitude")) {
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

    return RectangularBounds.newInstance(southWest, northEast);
  }

  static SearchByTextRequest buildSearchByTextRequest(String query, ReadableMap options,
                                                      AutocompleteSessionToken sessionToken) {

    // Specify the list of fields to return.
    final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME,
      Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.TYPES,
      Place.Field.WEBSITE_URI, Place.Field.ADDRESS_COMPONENTS
    );

    SearchByTextRequest.Builder builder = SearchByTextRequest.builder(query, placeFields).setMaxResultCount(10);

    return builder
      .build();
  }

  static SearchNearbyRequest buildSearchNearByRequest(ReadableMap options,
                                                      AutocompleteSessionToken sessionToken) {

    // Specify the list of fields to return.
    final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME,
      Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.TYPES,
       Place.Field.WEBSITE_URI, Place.Field.ADDRESS_COMPONENTS
     );

    LatLng center = new LatLng(options.getDouble("latitude"), options.getDouble("longitude"));
    CircularBounds circle = CircularBounds.newInstance(center, /* radius = */  options.getDouble("radius"));

    SearchNearbyRequest.Builder builder = SearchNearbyRequest.builder(circle, placeFields).setMaxResultCount(10);

    return builder
      .build();
  }

  static FindAutocompletePredictionsRequest buildPredictionRequest(String query, ReadableMap options,
                                                                   AutocompleteSessionToken sessionToken) {
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

    if (sessionToken != null) {
      builder.setSessionToken(sessionToken);
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

    map.putString("placeID", prediction.getPlaceId());
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

  static WritableMap ParseSearchByText(Place place) {
    WritableMap map = Arguments.createMap();


    map.putString("name", place.getName());
    map.putString("placeID", place.getId());
    map.putString("phoneNumber", place.getPhoneNumber());
    map.putString("formattedAddress", place.getAddress());
    map.putString("description", place.getAddress());
    map.putString("websiteUri", place.getWebsiteUri() != null ? place.getWebsiteUri().toString() : "");

    if (place.getLatLng() != null) {
      LatLng latLng = place.getLatLng();
      map.putMap("coordinate", ParseLatLngSearchText(latLng));
      map.putMap("location", ParseLatLngSearchText(latLng));
    }

    if (place.getTypes() != null) {
      map.putArray("types", ParsePlaceTypes(place.getTypes()));
    } else map.putNull("types");

    if (place.getAddressComponents() != null) {
      map.putArray("addressComponents", ParseAddressComponents(place.getAddressComponents()));
    }

    return map;
  }

  static WritableArray ParseAutocompletePredictions(List<AutocompletePrediction> predictions) {
    WritableArray parsedPredictions = Arguments.createArray();
    for (AutocompletePrediction prediction : predictions) {
      parsedPredictions.pushMap(ParseAutocompletePrediction(prediction));
    }

    return parsedPredictions;
  }

  static WritableArray ParseSearchByTexts(List<Place> places) {
    WritableArray parsesPlaces = Arguments.createArray();
    for (Place place : places) {
      parsesPlaces.pushMap(ParseSearchByText(place));
    }

    return parsesPlaces;
  }

  static List<Place.Field> ParsePlaceFields(ReadableArray fields) {
    ArrayList<Place.Field> placeFields = new ArrayList<>();
    for (int i = 0; i < fields.size(); i++) {
      String field = fields.getString(i);
      if (GooglePlacesSdkConstants.PLACE_FIELD_MAP.containsKey(field)) {
        placeFields.add(GooglePlacesSdkConstants.PLACE_FIELD_MAP.get(field));
      }
    }

    if (placeFields.size() == 0) {
      for (Place.Field field : GooglePlacesSdkConstants.PLACE_FIELD_MAP.values()) {
        placeFields.add(field);
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

  static WritableMap ParseLatLngSearchText(LatLng latLng) {
    WritableMap map = Arguments.createMap();
    map.putDouble("lat", latLng.latitude);
    map.putDouble("lng", latLng.longitude);

    return map;
  }


  static WritableArray ParseAddressComponents(AddressComponents addressComponents) {
    WritableArray components = Arguments.createArray();
    for (AddressComponent addressComponent : addressComponents.asList()) {
      WritableMap componentMap = Arguments.createMap();
      componentMap.putArray("types", Arguments.fromList(addressComponent.getTypes()));
      componentMap.putString("name", addressComponent.getName());
      componentMap.putString("shortName", addressComponent.getShortName());
      components.pushMap(componentMap);
    }

    return components;
  }

  static ArrayList<String> ParseUrls(String str) {
    Matcher webMatcher = Patterns.WEB_URL.matcher(str);
    ArrayList<String> hyperLinks = new ArrayList<>();

    while (webMatcher.find()) {
      String res = webMatcher.group();
      hyperLinks.add(res);
    }

    return hyperLinks;
  }

  static String ParsePhotoUrl(String str) {
    ArrayList<String> urls = ParseUrls(str);
    if (urls.size() == 0) return "";

    return urls.get(0);
  }

  static WritableMap ParsePhotoAttributions(String attributions) {
    String result = attributions.replaceAll("<[^>]*>", "");
    WritableMap map = Arguments.createMap();
    map.putString("url", ParsePhotoUrl(attributions));
    map.putString("name", result);

    return map;
  }

  static WritableArray ParsePhotos(List<PhotoMetadata> photos) {
    WritableArray components = Arguments.createArray();
    for (PhotoMetadata photo : photos) {
      WritableMap componentMap = Arguments.createMap();
      componentMap.putMap("attributions", ParsePhotoAttributions(photo.getAttributions()));
      componentMap.putDouble("width", photo.getWidth());
      componentMap.putDouble("height", photo.getHeight());
      componentMap.putString("data", photo.toString());
      componentMap.putString("reference", photo.zza());
      components.pushMap(componentMap);
    }

    return components;
  }

  static WritableMap ParsePlusCode(PlusCode plusCode) {
    WritableMap map = Arguments.createMap();
    map.putString("compoundCode", plusCode.getCompoundCode());
    map.putString("globalCode", plusCode.getGlobalCode());

    return map;
  }

  static WritableMap ParsePlace(Place place) {
    WritableMap placeInfo = Arguments.createMap();

    placeInfo.putString("name", place.getName());
    placeInfo.putString("placeID", place.getId());
    placeInfo.putString("phoneNumber", place.getPhoneNumber());
    placeInfo.putString("formattedAddress", place.getAddress());
    placeInfo.putString(
      "businessStatus",
      place.getBusinessStatus() != null ? place.getBusinessStatus().toString() : "UNKNOWN"
    );
    placeInfo.putString("takeout", place.getTakeout().toString());
    placeInfo.putString("delivery", place.getDelivery().toString());
    placeInfo.putString("dineIn", place.getDineIn().toString());
    placeInfo.putString("curbsidePickup", place.getCurbsidePickup().toString());

    if (place.getPhotoMetadatas() != null) {
      placeInfo.putArray("photos", ParsePhotos(place.getPhotoMetadatas()));
    } else placeInfo.putNull("photos");

    if (place.getAttributions() != null) {
      placeInfo.putString("attributions", place.getAttributions().toString());
    } else placeInfo.putNull("attributions");

    if (place.getPlusCode() != null) {
      placeInfo.putMap("plusCode", ParsePlusCode(place.getPlusCode()));
    } else placeInfo.putNull("plusCode");

    if (place.getWebsiteUri() != null) {
      placeInfo.putString("website", place.getWebsiteUri().toString());
    } else placeInfo.putNull("website");

    if (place.getRating() != null) {
      placeInfo.putDouble("rating", place.getRating());
    } else placeInfo.putNull("rating");

    if (place.getUserRatingsTotal() != null) {
      placeInfo.putInt("userRatingsTotal", place.getUserRatingsTotal());
    } else placeInfo.putNull("userRatingsTotal");

    if (place.getPriceLevel() != null) {
      placeInfo.putInt("priceLevel", place.getPriceLevel());
    } else placeInfo.putNull("priceLevel");

    if (place.getOpeningHours() != null) {
      placeInfo.putString("openingHours", place.getOpeningHours().getWeekdayText().toString());
    } else placeInfo.putNull("openingHours");

    if (place.getTypes() != null) {
      placeInfo.putArray("types", ParsePlaceTypes(place.getTypes()));
    } else placeInfo.putNull("types");

    if (place.getAddressComponents() != null) {
      placeInfo.putArray("addressComponents", ParseAddressComponents(place.getAddressComponents()));
    }

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
