package com.googleplacessdk;

import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.gms.common.api.ApiException;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.module.annotations.ReactModule;
import com.google.android.libraries.places.api.net.SearchByTextRequest;
import com.google.android.libraries.places.api.net.SearchNearbyRequest;

import android.util.Log;

import java.util.List;

@ReactModule(name = GooglePlacesSdkModule.NAME)
public class GooglePlacesSdkModule extends ReactContextBaseJavaModule {
  public static final String NAME = "GooglePlacesSdk";
  private String NOT_INITIALIZED_MSG = "Google Places not initialized. Initialize by calling initialize method before calling any other methods";
  private final String SESSION_LOG_TAG = "GooglePlacesSession";
  private final String NEW_SESSION_STARTED = "NEW_SESSION_STARTED";
  private final String SESSION_CLEARED = "SESSION_CLEARED";
  private final String NO_ACTIVE_SESSION = "NO_ACTIVE_SESSION";
  private ReactApplicationContext reactContext;
  private String TAG = "GooglePlacesSdk";
  private PlacesClient placesClient;
  private AutocompleteSessionToken sessionToken;

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
    Places.initializeWithNewPlacesApiEnabled(reactContext, apiKey);
    placesClient = Places.createClient(reactContext);
  }

  // generate a new session token manually
  @ReactMethod
  public void startNewSession(final Promise promise) {
    if (!Places.isInitialized()) {
      promise.reject(
        "-1",
        new Error(NOT_INITIALIZED_MSG));
      return;
    }

    sessionToken = AutocompleteSessionToken.newInstance();
    promise.resolve(NEW_SESSION_STARTED);
  }

  @ReactMethod
  public void clearSession(final Promise promise) {
    if (!Places.isInitialized()) {
      promise.reject(
        "-1",
        new Error(NOT_INITIALIZED_MSG));
      return;
    }

    if (sessionToken == null) {
      promise.resolve(NO_ACTIVE_SESSION);
      return;
    }

    sessionToken = null;
    promise.resolve(SESSION_CLEARED);
  }

  @ReactMethod
  public void fetchPredictions(String query, ReadableMap options, final Promise promise) {
    if (!Places.isInitialized()) {
      promise.reject(
        "-1",
        new Error(NOT_INITIALIZED_MSG));
      return;
    }

    if (sessionToken == null) {
      sessionToken = AutocompleteSessionToken.newInstance(); // Auto-generate if missing
    }

    FindAutocompletePredictionsRequest request = GooglePlacesSdkUtils.buildPredictionRequest(query, options,
      sessionToken);
    placesClient.findAutocompletePredictions(request)
      .addOnSuccessListener((response) -> {
        WritableArray parsedPredictions = GooglePlacesSdkUtils.ParseAutocompletePredictions(
          response.getAutocompletePredictions());
        promise.resolve(parsedPredictions);
      })
      .addOnFailureListener((exception) -> {
        if (exception instanceof ApiException) {
          ApiException apiException = (ApiException) exception;
          promise.reject(
            Integer.toString(apiException.getStatusCode()),
            apiException.getLocalizedMessage());
        }
      });
  }

  @ReactMethod
  public void searchNearby(ReadableMap options, ReadableArray fields, final Promise promise) {
    if (!Places.isInitialized()) {
      promise.reject(
        "-1",
        new Error(NOT_INITIALIZED_MSG));
      return;
    }

    if (sessionToken == null) {
      sessionToken = AutocompleteSessionToken.newInstance(); // Auto-generate if missing
    }

    SearchNearbyRequest request = GooglePlacesSdkUtils.buildSearchNearByRequest(options,
      sessionToken);

    placesClient.searchNearby(request)
      .addOnSuccessListener((response) -> {
        WritableArray parsedPredictions = GooglePlacesSdkUtils.ParseSearchByTexts(
          response.getPlaces());
        promise.resolve(parsedPredictions);
      })
      .addOnFailureListener((exception) -> {
        if (exception instanceof ApiException) {
          ApiException apiException = (ApiException) exception;
          promise.reject(
            Integer.toString(apiException.getStatusCode()),
            apiException.getLocalizedMessage());
        }
      });
  }
  //


  @ReactMethod
  public void searchByText(String query, ReadableMap options, final Promise promise) {
    if (!Places.isInitialized()) {
      promise.reject(
        "-1",
        new Error(NOT_INITIALIZED_MSG));
      return;
    }

    if (sessionToken == null) {
      sessionToken = AutocompleteSessionToken.newInstance(); // Auto-generate if missing
    }

    SearchByTextRequest request = GooglePlacesSdkUtils.buildSearchByTextRequest(query, options,
      sessionToken);

    placesClient.searchByText(request)
      .addOnSuccessListener((response) -> {
        WritableArray parsedPredictions = GooglePlacesSdkUtils.ParseSearchByTexts(
          response.getPlaces());
        promise.resolve(parsedPredictions);
      })
      .addOnFailureListener((exception) -> {
        if (exception instanceof ApiException) {
          ApiException apiException = (ApiException) exception;
          promise.reject(
            Integer.toString(apiException.getStatusCode()),
            apiException.getLocalizedMessage());
        }
      });
  }

  @ReactMethod
  public void fetchPlaceByID(String placeID, ReadableArray fields, final Promise promise) {
    if (!Places.isInitialized()) {
      promise.reject(
        "-1",
        new Error(NOT_INITIALIZED_MSG));
      return;
    }

    List<Place.Field> placeFields = GooglePlacesSdkUtils.ParsePlaceFields(fields);
    FetchPlaceRequest.Builder placeRequestBuilder = FetchPlaceRequest.builder(placeID, placeFields);
    if (sessionToken != null) {
      placeRequestBuilder.setSessionToken(sessionToken);
    } else {
      Log.w(SESSION_LOG_TAG, "⚠️ Session Token is null. Place selection might not be billed efficiently.");
    }
    FetchPlaceRequest placeRequest = placeRequestBuilder.build();

    placesClient.fetchPlace(placeRequest)
      .addOnSuccessListener((response) -> {
        Place place = response.getPlace();
        promise.resolve(GooglePlacesSdkUtils.ParsePlace((place)));
        sessionToken = null; // Reset session token after successful fetch
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
