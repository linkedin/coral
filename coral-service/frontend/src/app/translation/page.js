'use client';

import { useState } from 'react';

import NavBar from '@/app/components/NavBar';
import TranslationForm from '@/app/components/Forms/TranslationForm';
import ResultCard from '@/app/components/Cards/ResultCard';
import GraphCard from '@/app/components/Cards/GraphCard';

export default function TranslationPage() {
  const [translationResult, setTranslationResult] = useState(null);
  const [imageIDs, setImageIDs] = useState(null);

  const handleTranslationFetch = (data) => {
    setTranslationResult(data);
  };

  const handleImageFetch = (data) => {
    setImageIDs(data);
  };

  return (
    <>
      <NavBar />

      <TranslationForm
        onTranslationFetchComplete={handleTranslationFetch}
        onImageIDsFetchComplete={handleImageFetch}
      />
      {translationResult && <ResultCard translation={translationResult} />}
      {imageIDs && <GraphCard imageIDs={imageIDs} />}
    </>
  );
}
