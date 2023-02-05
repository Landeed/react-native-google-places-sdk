import type { PLACE_FIELDS } from './constants';

export type PlacePrediction = {
  description: string;
  placeID: string;
  primaryText: string;
  secondaryText: string;
  types: string[];
  distanceMeters: number;
};

export type LatLng = {
  latitude: number;
  longitude: number;
};

export type LocationBounds = {
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

export type AtmosphereCategoryStatus = 'TRUE' | 'FALSE' | 'UNKNOWN';

export type BusinessStatus =
  | 'OPERATIONAL'
  | 'CLOSED_TEMPORARILY'
  | 'CLOSED_PERMANENTLY'
  | 'UNKNOWN';

export type Place = {
  name: string | null;
  placeID: string | null;
  plusCode: string | null;
  coordinate: LatLng | null;
  openingHours: string | null;
  phoneNumber: string | null;
  types: string[] | null;
  priceLevel: number | null;
  website: string | null;
  viewport: (LocationBounds & { valid: boolean }) | null;
  formattedAddress: string | null;
  addressComponents:
    | {
        types: string[];
        name: string;
        shortName: string;
      }[]
    | null;
  attributions: string | null;
  rating: number;
  userRatingsTotal: number;
  utcOffsetMinutes: number | null;
  iconImageURL: string | null;
  businessStatus: BusinessStatus;
  dineIn: AtmosphereCategoryStatus;
  takeout: AtmosphereCategoryStatus;
  delivery: AtmosphereCategoryStatus;
  curbsidePickup: AtmosphereCategoryStatus;
  photos: {
    attributions: {
      url: string;
      name: string;
    };
    reference: string;
    width: number;
    height: number;
  }[];
};
