import { NativeModules, Platform } from 'react-native';
import type {
  FieldsParam,
  Place,
  PlacePrediction,
  PredictionFiltersParam,
} from './types';

const LINKING_ERROR =
  `The package 'react-native-google-places-sdk' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const GooglePlacesSdk = NativeModules.GooglePlacesSdk
  ? NativeModules.GooglePlacesSdk
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );
console.log(GooglePlacesSdk);
function initialize(apiKey: string) {
  GooglePlacesSdk.initialize(apiKey);
}

async function fetchPredictions(
  query: string,
  filters: PredictionFiltersParam = {}
): Promise<PlacePrediction[]> {
  const predictions = await GooglePlacesSdk.fetchPredictions(query, filters);

  return predictions;
}

async function fetchPlaceByID(
  placeID: string,
  fields: FieldsParam = []
): Promise<Place> {
  const place = await GooglePlacesSdk.fetchPlaceByID(placeID, fields);

  return place;
}

const GooglePlacesSDK = {
  initialize,
  fetchPredictions,
  fetchPlaceByID,
};

export default GooglePlacesSDK;
