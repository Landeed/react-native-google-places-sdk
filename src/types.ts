import type { PLACE_FIELDS } from './constants';

export type PlacePrediction = {
  description: string;
  placeID: string;
  primaryText: string;
  secondaryText: string;
  types: string[];
  distanceMeters: number;
};

type LatLng = {
  latitude: number;
  longtiude: number;
};

type LocationBounds = {
  northEast: LatLng;
  southWest: LatLng;
};

export type PredictionFiltersParam = {
  types?: string[];
  countries?: string[];
  locationBias?: LocationBounds;
  locationRestriction?: LocationBounds;
  origin?: LatLng;
};

export type FieldsParam = (typeof PLACE_FIELDS)[keyof typeof PLACE_FIELDS][];

export type Place = {
  name: String | null;
  placeID: string | null;
  plusCode: string | null;
  coordinate: LatLng | null;
  openingHours: string | null;
  phoneNumber: string | null;
  types: string[] | null;
  priceLevel: number | null;
  website: string | null;
  viewport: (LocationBounds & { valid: boolean }) | null;
  addressComponents:
    | {
        types: string[];
        name: string;
        shortName: string;
      }[]
    | null;
  photos: {
    attributions: string;
    maxSize: number | null;
  } | null;
  userRatingsTotal: number;
  utcOffsetMinutes: number | null;
  businessStatus: string | null;
  iconImageURL: string | null;
};
