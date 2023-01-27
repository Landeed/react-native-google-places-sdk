import React, { useState, useRef } from 'react';

import {
  FlatList,
  SafeAreaView,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  TouchableHighlight,
  View,
} from 'react-native';
import GooglePlacesSDK, {
  Place,
  PlacePrediction,
} from 'react-native-google-places-sdk';
import { Debounce } from './utils';

const GOOGLE_PLACES_API_KEY = '';

GooglePlacesSDK.initialize(GOOGLE_PLACES_API_KEY);

export default function App() {
  const [query, setQuery] = useState('');
  const [predictions, setPredictions] = useState<PlacePrediction[]>([]);
  const [place, setPlace] = useState<Place>();

  const fetchPredictions = (currQuery: string) => {
    GooglePlacesSDK.fetchPredictions(currQuery)
      .then((fetchedPredictions) => setPredictions(fetchedPredictions))
      .catch((err) => console.error(err));
  };

  const debouncedFetchPredictions = useRef(Debounce(fetchPredictions, 300));

  const fetchPlace = (placeID: string) => {
    GooglePlacesSDK.fetchPlaceByID(placeID, [])
      .then((fetchedPlace) => {
        setPlace(fetchedPlace);
      })
      .catch((err) => {
        console.error(err);
      });
  };

  const onChangeText = (txt: string) => {
    setQuery(txt);

    debouncedFetchPredictions.current(txt);
  };

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.section}>
        <Text style={styles.heading}>Predictions</Text>
        <TextInput
          style={styles.queryInput}
          value={query}
          placeholder="Search Place"
          onChangeText={onChangeText}
        />
        <FlatList
          data={predictions}
          renderItem={({ item }) => (
            <TouchableHighlight
              underlayColor={'lightgrey'}
              onPress={() => fetchPlace(item.placeID)}
            >
              <View style={styles.predictionWrapper}>
                <Text style={styles.predictionDescription}>
                  {item.description}
                </Text>
                <Text>{item.types}</Text>
              </View>
            </TouchableHighlight>
          )}
        />
      </View>
      <View style={styles.section}>
        <Text style={styles.heading}>Place Details</Text>
        {!place ? (
          <Text>Click on one of the predictions to show the details</Text>
        ) : (
          <ScrollView>
            <Text>Name: {place.name}</Text>
            <Text>Place ID: {place.placeID}</Text>
            <Text>Types: {place.types?.join(', ')}</Text>
            <Text>Plus Code: {place.plusCode}</Text>
            <Text>Coordinate: {JSON.stringify(place.coordinate)}</Text>
            <Text>Opening Hours: {place.openingHours}</Text>
            <Text>Phone Number: {place.phoneNumber}</Text>
            <Text>Price Level: {place.priceLevel}</Text>
            <Text>Website: {place.website}</Text>
            <Text>Viewport: {JSON.stringify(place.viewport)}</Text>
            <Text>
              Address Components: {JSON.stringify(place.addressComponents)}
            </Text>
            <Text>Phones: {JSON.stringify(place.photos)}</Text>
            <Text>
              User Ratings Total: {JSON.stringify(place.userRatingsTotal)}
            </Text>
            <Text>UTC Offset: {JSON.stringify(place.utcOffsetMinutes)}</Text>
            <Text>Business Status: {JSON.stringify(place.businessStatus)}</Text>
            <Text>Icon Image URL: {JSON.stringify(place.iconImageURL)}</Text>
            <Text />
          </ScrollView>
        )}
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    margin: 10,
  },
  predictionWrapper: { marginTop: 10 },
  predictionDescription: { fontWeight: 'bold' },
  section: { flex: 1 },
  heading: { fontSize: 20 },
  queryInput: {
    borderWidth: 1,
    paddingVertical: 10,
    paddingHorizontal: 5,
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
