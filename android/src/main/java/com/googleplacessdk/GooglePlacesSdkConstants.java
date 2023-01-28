package com.googleplacessdk;

import com.google.android.libraries.places.api.model.Place;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class GooglePlacesSdkConstants {
  static final Map<String, Place.Field> PLACE_FIELD_MAP = createPlaceFieldMap();

  private static Map<String, Place.Field> createPlaceFieldMap() {
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
    fieldMap.put("formattedAddress", Place.Field.ADDRESS);
    fieldMap.put("addressComponents", Place.Field.ADDRESS_COMPONENTS);
    fieldMap.put("rating", Place.Field.RATING);
    fieldMap.put("userRatingsTotal", Place.Field.USER_RATINGS_TOTAL);
    fieldMap.put("utcOffsetMinutes", Place.Field.UTC_OFFSET);
    fieldMap.put("businessStatus", Place.Field.BUSINESS_STATUS);
    fieldMap.put("iconImageURL", Place.Field.ICON_URL);
    fieldMap.put("takeout", Place.Field.TAKEOUT);
    fieldMap.put("delivery", Place.Field.DELIVERY);
    fieldMap.put("dineIn", Place.Field.DINE_IN);
    fieldMap.put("curbsidePickup", Place.Field.CURBSIDE_PICKUP);
    fieldMap.put("photos", Place.Field.PHOTO_METADATAS);
    
    return Collections.unmodifiableMap(fieldMap);
  }
}
