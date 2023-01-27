//
//  GooglePlacesSdkUtils.swift
//  GooglePlacesSdk
//
//  Created by Farid Ansari on 26/01/23.
//  Copyright © 2023 Facebook. All rights reserved.
//

import Foundation
import GooglePlaces

struct LocationBounds {
  let northEast: CLLocationCoordinate2D
  let southWest: CLLocationCoordinate2D
}

func ParseCoordinates(_ coordinates: NSDictionary) -> CLLocationCoordinate2D? {
  
  if (coordinates["latitude"] == nil || coordinates["longitude"] ==  nil) {
    return nil
  }
  
  let latitude = coordinates["latitude"] as! Double
  let longitude = coordinates["longitude"] as! Double
  
  return CLLocationCoordinate2DMake(latitude, longitude)
}

func ParseLocationBounds(_ location: NSDictionary) -> LocationBounds? {
  if (location["northEast"] == nil || location["southWest"] == nil) {
    return nil
  }

  let northEast = ParseCoordinates(location["northEast"] as! NSDictionary)
  let southWest = ParseCoordinates(location["southWest"] as! NSDictionary)
  
  if (northEast == nil || southWest == nil) {
    return nil
  }
  
  return LocationBounds(
    northEast: northEast!,
    southWest: southWest!
  )
}

func ParseLocationBias(_ locationBias: NSDictionary) -> GMSPlaceLocationBias? {
  guard let locationBounds = ParseLocationBounds(locationBias) else {
    return nil
  }
  
  return GMSPlaceRectangularLocationOption(
    locationBounds.northEast,
    locationBounds.southWest
  )
}

func ParseLocationRestriction(_ locationRestriction: NSDictionary) -> GMSPlaceLocationRestriction? {
  guard let locationBounds = ParseLocationBounds(locationRestriction) else {
    return nil
  }
  
  return GMSPlaceRectangularLocationOption(
    locationBounds.northEast,
    locationBounds.southWest
  )
}

func ParseOrigin(_ originCoordinates: NSDictionary) -> CLLocation? {
  guard let coordinates = ParseCoordinates(originCoordinates) else {
    return nil
  }
  
  return CLLocation(
    latitude: coordinates.latitude,
    longitude:  coordinates.longitude
  )
}

func AutocompleteFilterFromOptions(_ filterOptions: NSDictionary) -> GMSAutocompleteFilter {
  let filter = GMSAutocompleteFilter()
  if let types = filterOptions["types"] as? Array<String> {
    filter.types = types
  }
  if let countries = filterOptions["countries"] as? Array<String> {
    filter.countries = countries
  }
  
  if let locationBias = filterOptions["locationBias"] as? NSDictionary {
    let parsedLocationBias = ParseLocationBias(locationBias)
    if (parsedLocationBias != nil) {
      filter.locationBias = parsedLocationBias
    }
  }
  
  if let locationRestriction = filterOptions["locationRestriction"] as? NSDictionary {
    let parsedLocationRestriction = ParseLocationRestriction(locationRestriction)
    if (parsedLocationRestriction != nil) {
      filter.locationRestriction = parsedLocationRestriction
    }
  }
  
  if let origin = filterOptions["origin"] as? NSDictionary {
    let parsedOrigin = ParseOrigin(origin)
    if (parsedOrigin != nil) {
      filter.origin = parsedOrigin
    }
  }
  
  return filter
}

func GMSPlaceFieldsFromFields(fields: NSArray) -> GMSPlaceField {
  let fieldMap: NSDictionary = [
    "name": GMSPlaceField.name,
    "placeID": GMSPlaceField.placeID,
    "plusCode": GMSPlaceField.plusCode,
    "coordinate": GMSPlaceField.coordinate,
    "openingHours": GMSPlaceField.openingHours,
    "phoneNumber": GMSPlaceField.phoneNumber,
    "types": GMSPlaceField.types,
    "priceLevel": GMSPlaceField.priceLevel,
    "website": GMSPlaceField.website,
    "viewport": GMSPlaceField.viewport,
    "formattedAddress": GMSPlaceField.formattedAddress,
    "addressComponents": GMSPlaceField.addressComponents,
    "userRatingsTotal": GMSPlaceField.userRatingsTotal,
    "utcOffsetMinutes": GMSPlaceField.utcOffsetMinutes,
    "businessStatus": GMSPlaceField.businessStatus,
    "iconImageURL": GMSPlaceField.iconImageURL,
    "takeout": GMSPlaceField.takeout,
    "delivery": GMSPlaceField.delivery,
    "dineIn": GMSPlaceField.dineIn,
    "curbsidePickup": GMSPlaceField.curbsidePickup,
  ]
  
  var parsedFields: GMSPlaceField = []
  for field in fields {
    if let parsedField = fieldMap[field] as? GMSPlaceField {
      parsedFields.insert(parsedField)
    }
  }
  
  if (parsedFields.isEmpty) {
    for field in fieldMap.allValues {
      if let parsedField = field as? GMSPlaceField {
        parsedFields.insert(parsedField)
      }
    }
  }
  
  return parsedFields
}

func ParseBooleanPlaceAttribute(val: GMSBooleanPlaceAttribute) -> String {
  if (val == GMSBooleanPlaceAttribute.true) {
    return "yes";
  }
  
  if (val == GMSBooleanPlaceAttribute.false) {
    return "no";
  }
  
  return "unknown"
}

func ParseBusinessStatus(val: GMSPlacesBusinessStatus) -> String {
  if (val == GMSPlacesBusinessStatus.closedPermanently) {
    return "closedPermanently";
  }
  
  if (val == GMSPlacesBusinessStatus.closedTemporarily) {
    return "closedTemporariliy";
  }
  
  if (val == GMSPlacesBusinessStatus.operational) {
    return "operational";
  }
  
  return "unknown";
}

func ParsePlace(place: GMSPlace) -> NSDictionary {
  let addressComponents = place.addressComponents?.compactMap{ [
    "types": $0.types,
    "name": $0.name,
    "shortName": $0.shortName ?? "",
  ]}

  var viewport: [String : Any]? = nil
  if let viewportInfo = place.viewportInfo{
     viewport = [
      "northEast": [
        "latitude": viewportInfo.northEast.latitude,
        "longitude": viewportInfo.northEast.longitude
      ],
      "southWest": [
        "latitude": viewportInfo.southWest.latitude,
        "longitude": viewportInfo.southWest.longitude
      ],
      "valid": viewportInfo.isValid,
    ]
  }
  
  let coordinate = [
      "latitude": place.coordinate.latitude,
      "longitude": place.coordinate.longitude
  ]
  
  
  return [
    "name": place.name ?? NSNull(),
    "placeID": place.placeID ?? NSNull(),
    "plusCode": place.plusCode ?? NSNull(),
    "coordinate": coordinate,
    "openingHours": place.openingHours?.weekdayText ?? NSNull(),
    "phoneNumber": place.phoneNumber ?? "",
    "types": place.types ?? NSNull(),
    "priceLevel": place.priceLevel,
    "website": place.website?.absoluteString ?? NSNull(),
    "viewport": viewport ??  NSNull(),
    "formattedAddress": place.formattedAddress ?? NSNull(),
    "addressComponents": addressComponents ?? NSNull(),
    "rating": place.rating,
    "userRatingsTotal": place.userRatingsTotal,
    "utcOffsetMinutes": place.utcOffsetMinutes ?? NSNull(),
    "businessStatus": ParseBusinessStatus(val: place.businessStatus),
    "iconImageURL": place.iconImageURL?.absoluteString ?? NSNull(),
    "takeout": ParseBooleanPlaceAttribute(val: place.takeout),
    "delivery": ParseBooleanPlaceAttribute(val: place.delivery),
    "dineIn": ParseBooleanPlaceAttribute(val: place.dineIn),
    "curbsidePickup": ParseBooleanPlaceAttribute(val: place.curbsidePickup),
  ]
}
