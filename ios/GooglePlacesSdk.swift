//
//  GooglePlacesSdkUtils.swift
//  GooglePlacesSdk
//
//  Created by Farid Ansari on 25/01/23.
//  Copyright © 2023 Facebook. All rights reserved.
//

import Foundation
import GooglePlaces

let NOT_INITIALIZED_MSG = "Google Places not initialized. Initialize by calling initialize method before calling any other methods"
let NEW_SESSION_CREATED = "NEW_SESSION_CREATED"
let SESSION_CLEARED = "SESSION_CLEARED";
let NO_ACTIVE_SESSION = "NO_ACTIVE_SESSION";

@objc(GooglePlacesSdk)
class GooglePlacesSdk: NSObject {
  private var client: GMSPlacesClient? = nil;
  private var sessionToken: GMSAutocompleteSessionToken? = nil;
  
  @objc
  func initialize(_ apiKey: String) -> Void {
    DispatchQueue.main.async {
      GMSPlacesClient.provideAPIKey(apiKey)
      self.client = GMSPlacesClient.shared()
    }
  }
  
  @objc
  func startNewSession(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) {
      guard let client = self.client else {
          reject("-1", NOT_INITIALIZED_MSG, NSError(domain: "", code: 0))
          return
      }
      
      self.sessionToken = GMSAutocompleteSessionToken()
      resolve(NEW_SESSION_CREATED)
  }
  
  @objc
  func clearSession(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) {
      guard let _ = self.client else {
          reject("-1", NOT_INITIALIZED_MSG, NSError(domain: "", code: 0))
          return
      }
    
      if self.sessionToken == nil {
          resolve(NO_ACTIVE_SESSION)
          return
      }
      
      self.sessionToken = nil
      resolve(SESSION_CLEARED)
  }
  
  @objc
  func fetchPredictions(_ query: String, filterOptions: NSDictionary,  resolver resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) {
    guard let client = self.client else {
      reject("-1", NOT_INITIALIZED_MSG, NSError(domain: "", code: 0))
      return
    }
    
    // Use sessionToken if it exists, otherwise create a new one
    self.sessionToken = self.sessionToken ?? GMSAutocompleteSessionToken()
    
    let filter = AutocompleteFilterFromOptions(filterOptions)
    client.findAutocompletePredictions(
      fromQuery: query,
      filter: filter,
      sessionToken: self.sessionToken,
      callback: {(results, error) in
        guard let results = results, error == nil else {
          let errorCode = error?._code ?? 0
          let errorMsg = error?.localizedDescription ?? "Unknown Error"
          reject(String(errorCode), errorMsg, error)
          return
        }

        let predictions: NSMutableArray = []
        for result in results {
          let dict: NSMutableDictionary = [
            "placeID": result.placeID,
            "description": result.attributedFullText.string,
            "primaryText": result.attributedPrimaryText.string,
            "secondaryText": result.attributedSecondaryText?.string ?? NSNull(),
            "types": result.types,
            "distanceMeters": result.distanceMeters?.intValue ?? NSNull()
          ]
          predictions.add(dict)
        }
        resolve(predictions)
      }
    )
  }

  
  @objc
  func fetchPlaceByID(_ placeID: String, fields: NSArray, resolver resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) {
    guard let client = self.client else {
      reject("-1", NOT_INITIALIZED_MSG, NSError(domain: "", code: 0))
      return
    }
    
    let parsedFields = GMSPlaceFieldsFromFields(fields: fields)
    let selectedFields: GMSPlaceField = parsedFields
    
    client.fetchPlace(fromPlaceID: placeID, placeFields: selectedFields, sessionToken: self.sessionToken, callback: {(place: GMSPlace?, error: Error?) in
      guard let place = place, error == nil else {
        let errorCode = error?._code ?? 0
        let errorMsg = error?.localizedDescription ?? "Unknown Error"
        reject(String(errorCode), errorMsg, error)
        return
      }
      let parsedPlace = ParsePlace(place: place)
      resolve(parsedPlace)
      
      self.sessionToken = nil
    })
  }
}
