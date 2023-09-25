'use client';

import { useState } from 'react';

import NavBar from '@/app/components/NavBar';
import TranslationForm from '@/app/components/Forms/TranslationForm';
import ResultCard from '@/app/components/Cards/ResultCard';
import GraphCard from '@/app/components/Cards/GraphCard';

export default function TranslationPage() {
  const [translationResult, setTranslationResult] = useState(null);
  const [imageIDs, setImageIDs] = useState(null);
  const [imageFetchError, setImageFetchError] = useState(null);

  const handleTranslationFetch = (result) => {
    setTranslationResult(result);
  };

  const handleImageFetch = (graphs, error) => {
    setImageIDs(graphs);
    setImageFetchError(error);
  };

  return (
    <>
      <NavBar />

      <TranslationForm
        onTranslationFetchComplete={handleTranslationFetch}
        onImageIDsFetchComplete={(graphs, error) => handleImageFetch(graphs, error)}
      />
      {translationResult && <ResultCard translation={translationResult} />}
      {(imageIDs || imageFetchError) && <GraphCard imageIDs={imageIDs} imageFetchError={imageFetchError}/>}
    </>
  );
}
