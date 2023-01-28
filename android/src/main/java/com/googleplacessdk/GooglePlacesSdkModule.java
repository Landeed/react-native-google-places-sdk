package com.googleplacessdk;

import com.facebook.react.bridge.ReadableArray;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.gms.common.api.ApiException;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.module.annotations.ReactModule;

import java.util.List;

@ReactModule(name = GooglePlacesSdkModule.NAME)
public class GooglePlacesSdkModule extends ReactContextBaseJavaModule {
  public static final String NAME = "GooglePlacesSdk";
  private String NOT_INITIALIZED_MSG = "Google Places not initialized. Initialize by calling initialize method before calling any other methods";
  private ReactApplicationContext reactContext;
  private String TAG = "GooglePlacesSdk";
  private PlacesClient placesClient;


  public GooglePlacesSdkModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return TAG;
  }

  @ReactMethod
  public void initialize(String apiKey) {
    Places.initialize(reactContext, apiKey);
    placesClient = Places.createClient(reactContext);
  }

  @ReactMethod
  public void fetchPredictions(String query, ReadableMap options, final Promise promise) {
    if (!Places.isInitialized()) {
      promise.reject(
        "-1",
        new Error(NOT_INITIALIZED_MSG)
      );
      return;
    }

    FindAutocompletePredictionsRequest request = GooglePlacesSdkUtils.buildPredictionRequest(query, options);
    placesClient.findAutocompletePredictions(request)
      .addOnSuccessListener((response) -> {
        WritableArray parsedPredictions = GooglePlacesSdkUtils.ParseAutocompletePredictions(
          response.getAutocompletePredictions()
        );
        promise.resolve(parsedPredictions);
      })
      .addOnFailureListener((exception) -> {
        if (exception instanceof ApiException) {
          ApiException apiException = (ApiException) exception;
          promise.reject(
            Integer.toString(apiException.getStatusCode()),
            apiException.getLocalizedMessage()
          );
        }
      });
  }

  @ReactMethod
  public void fetchPlaceByID(String placeID, ReadableArray fields, final Promise promise) {
    if (!Places.isInitialized()) {
      promise.reject(
        "-1",
        new Error(NOT_INITIALIZED_MSG)
      );
      return;
    }

    List<Place.Field> placeFields = GooglePlacesSdkUtils.ParsePlaceFields(fields);
    FetchPlaceRequest placeRequest = FetchPlaceRequest.newInstance(placeID, placeFields);
    placesClient.fetchPlace(placeRequest)
      .addOnSuccessListener((response) -> {
        Place place = response.getPlace();
        promise.resolve(GooglePlacesSdkUtils.ParsePlace((place)));
      })
      .addOnFailureListener((exception) -> {
        if (exception instanceof ApiException) {
          ApiException apiException = (ApiException) exception;
          promise.reject(
            Integer.toString(apiException.getStatusCode()),
            apiException.getLocalizedMessage()
          );
        }
      });
  }
}
