package com.googleplacessdk;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;

@ReactModule(name = GooglePlacesSdkModule.NAME)
public class GooglePlacesSdkModule extends ReactContextBaseJavaModule {
  public static final String NAME = "GooglePlacesSdk";
  private String NOT_INITIALIZED_MSG = "Google Places not initialized. Initialize by calling initialize method before calling any other methods";
  private ReactApplicationContext reactContext;
  private String TAG = "Places";
  private PlacesClient placesClient;


  public GooglePlacesSdkModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }


  @ReactMethod
  public void initialize(String apiKey) {
    Places.initialize(reactContext, apiKey);
    placesClient = Places.createClient(reactContext);
  }

  @ReactMethod
  public void predictions(String query, ReadableMap options, final Promise promise) {
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
  public void placeByID(String placeID, ReadableArray fields, final Promise promise) {
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
