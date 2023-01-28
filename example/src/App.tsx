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
  PredictionFiltersParam,
} from 'react-native-google-places-sdk';
import { Debounce } from './utils';

const GOOGLE_PLACES_API_KEY = '';

GooglePlacesSDK.initialize(GOOGLE_PLACES_API_KEY);

export default function App() {
  const [query, setQuery] = useState('');
  const [predictions, setPredictions] = useState<PlacePrediction[]>([]);
  const [place, setPlace] = useState<Place>();

  const fetchPredictions = (
    currQuery: string,
    filters: PredictionFiltersParam
  ) => {
    GooglePlacesSDK.fetchPredictions(currQuery, filters)
      .then((fetchedPredictions) => {
        setPredictions(fetchedPredictions);
      })
      .catch((err) => console.error(err));
  };

  const debouncedFetchPredictions = useRef(Debounce(fetchPredictions, 300));

  const fetchPlace = (placeID: string) => {
    GooglePlacesSDK.fetchPlaceByID(placeID)
      .then((fetchedPlace) => {
        setPlace(fetchedPlace);
      })
      .catch((err) => {
        console.error(err);
      });
  };

  const onChangeQuery = (txt: string) => {
    setQuery(txt);

    const filters: PredictionFiltersParam = {
      countries: ['in', 'us'],
      types: [],
    };
    debouncedFetchPredictions.current(txt, filters);
  };

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.section}>
        <Text style={styles.heading}>Predictions</Text>
        <TextInput
          style={styles.queryInput}
          value={query}
          placeholder="Search Place"
          onChangeText={onChangeQuery}
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
            <Text>{JSON.stringify(place.rating, undefined, 2)}</Text>
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
  section: { flex: 1, marginTop: 10 },
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
