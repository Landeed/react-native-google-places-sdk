# Google Places SDK for React Native

Google Places SDK for React Native. Places SDK allows you to build location aware apps that responds contextutally to the local businesses and other places near the user's device.

[![CI](https://github.com/Kroniac/react-native-google-places-sdk/actions/workflows/ci.yml/badge.svg)](https://github.com/Kroniac/react-native-google-places-sdk/actions/workflows/ci.yml)
[![Licence](https://img.shields.io/github/license/Kroniac/react-native-google-places-sdk)](https://opensource.org/licenses/MIT)

## Table of contents

- [Requirements](#requirements)
  - [Minimum Platform Version](#minimum-platform-version)
  - [Google Places API Key](#google-places-api-key)
- [Installation](#installation)
- [Usage](#usage)
  - [Initialize SDK](#initialize-sdk)
    - [Initialize SDK](#initialize-sdk)
  - [Fetch Predictions](#fetch-predictions)
    - [Sample Implementation](#sample-implementation)
  - [Fetch Place By ID](#fetch-place-by-id)
    - [Sample Implementation](#sample-implementation-1)
- [Contributing](#contributing)
- [Licence](#license)

## Requirements

### Minimum Platform Version

- Android: 21
- iOS: 13

### Google Places API Key

- [Get Android API Key](https://developers.google.com/maps/documentation/places/android-sdk/get-api-key)
- [Get iOS API Key](https://developers.google.com/maps/documentation/places/ios-sdk/get-api-key)

## Installation

```sh
npm install react-native-google-places-sdk
#OR
yarn add react-native-google-places-sdk
```

## Usage

### Initialize SDK

#### initialize(apiKey: string): void

SDK needs to be initialize only once per App start before using any other functions. Preferably in the root file, e.g., App.tsx.

```ts
import GooglePlacesSDK from 'react-native-google-places-sdk';

const GOOGLE_PLACES_API_KEY = ""; // add your Places API key
GooglePlacesSDK.initialize(GOOGLE_PLACES_API_KEY);
```

### Fetch Predictions

#### fetchPredictions(query: string, filters?: PredictionFiltersParam): Promise<PlacePrediction[]>

#### PredictionFiltersParams

```ts
type PredictionFiltersParam = {
  types?: string[];
  countries?: string[];
  locationBias?: LocationBounds;
  locationRestriction?: LocationBounds;
  origin?: LatLng;
};
```

#### PlacePrediction

```ts
type PlacePrediction = {
  description: string;
  placeID: string;
  primaryText: string;
  secondaryText: string;
  types: string[];
  distanceMeters: number;
}
```

#### Sample Output

```json
{
  "description": "Mumbai, Maharashtra, India",
  "distanceMeters": null,
  "placeID": "ChIJwe1EZjDG5zsRaYxkjY_tpF0",
  "primaryText": "Mumbai",
  "secondaryText": "Maharashtra, India",
  "types": [
    "locality",
    "political",
    "geocode"
  ]
}
```

#### Sample Implementation

```ts
import GooglePlacesSDK, { PLACE_FIELDS } from 'react-native-google-places-sdk';

GooglePlacesSDK.fetchPredictions(
  "Mumbai", // query
  { countries: ["in", "us"] } // filters
)
  .then((predictions) => console.log(predictions));
  .catch((error) => console.log(error));

// ...
```

### Fetch Place By ID

#### fetchPlaceByID(placeID: string, fields?: FieldsParam): Promise\<Place\>

#### FieldsParam

- Allowed Fields: Refer PLACE_FIELDS in 'react-native-google-sdk'

- If no fields or empty array is passed, then all fields will be fetched for given the place ID.

```ts
// type
string[]

// Example
import { PLACE_FIELDS } from 'react-native-google-places-sdk';

const fields = [PLACE_FIELDS.NAME, PLACE_FIELDS.PLACE_ID, PLACE_FIELDS.ADDRESS_COMPONENTS]
```

#### Place

```ts
type Place = {
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
```

#### Sample Implementation

```ts
import GooglePlacesSDK, { PLACE_FIELDS } from 'react-native-google-places-sdk';

GooglePlacesSDK.fetchPlaceByID(
  placeID = "ChIJwe1EZjDG5zsRaYxkjY_tpF0",
  fields = [PLACE_FIELDS.NAME, PLACE_FIELDS.TYPES]
)
  .then((place) => console.log(place));
  .catch((error) => console.log(error));
// ...
```

## Example Project


## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
