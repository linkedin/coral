'use client';

import { useState } from 'react';
import LoadingButton from '@/app/components/Buttons/LoadingButton';

export default function TranslationForm({
  onTranslationFetchComplete,
  onImageIDsFetchComplete,
}) {
  const [isLoading, setIsLoading] = useState(false);

  async function onSubmit(event) {
    event.preventDefault();
    setIsLoading(true);

    const formData = new FormData(event.currentTarget);

    await fetch('http://localhost:8080/api/translations/translate', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Accept: 'application/json',
      },
      body: JSON.stringify(Object.fromEntries(formData)),
    })
      .then((response) => {
        if (!response.ok) {
          return response.text().then(errorMessage => {
            throw new Error(errorMessage);
          });
        }

        return response.text();
      })
      .then((data) => {
        onTranslationFetchComplete(data);
      })
      .catch((error) => {
        console.error('Error:', error);
        onTranslationFetchComplete(error.message);
        setIsLoading(false);

      });

    await fetch('http://localhost:8080/api/visualizations/generategraphs', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Accept: 'application/json',
      },
      body: JSON.stringify(Object.fromEntries(formData)),
    })
      .then((response) => {
        if (!response.ok) {
          return response.text().then(errorMessage => {
            throw new Error(errorMessage);
          });
        }

        return response.json();
      })
      .then((data) => {
        onImageIDsFetchComplete(data, null);
        setIsLoading(false);
      })
      .catch((error) => {
        onImageIDsFetchComplete(null, error.message);
        setIsLoading(false);
      });
  }

  return (
    <>
      <div className='flex flex-1 flex-col justify-center px-6 py-12 lg:px-8'>
        <div className='mt-10 sm:mx-auto sm:w-full sm:max-w-sm'>
          <form
            className='space-y-6'
            action='@/app/components/Forms/TranslationForm#'
            method='POST'
            onSubmit={onSubmit}
          >
            <div className='col-span-full'>
              <label
                htmlFor='query'
                className='block text-xl font-medium leading-6 text-gray-900'
              >
                Translate SQL
              </label>
              <div className='mt-2'>
                <textarea
                  id='query'
                  name='query'
                  rows='3'
                  className='block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6'
                  placeholder='SELECT * FROM db.tbl'
                ></textarea>
              </div>
            </div>
            <div className='flex justify-between'>
              <div>From</div>
              <select
                id='sourceLanguage'
                name='sourceLanguage'
                className='mx-6 block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:max-w-xs sm:text-sm sm:leading-6'
              >
                <option value='hive'>Hive</option>
                <option value='trino'>Trino</option>
                <option value='spark'>Spark</option>
              </select>
              <div> to </div>
              <select
                id='targetLanguage'
                name='targetLanguage'
                className='mx-6 block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:max-w-xs sm:text-sm sm:leading-6'
              >
                <option value='trino'>Trino</option>
                <option value='spark'>Spark</option>
              </select>
            </div>

            <div className='col-span-full'>
              <label
                htmlFor='rewriteType'
                className='block text-md font-medium leading-6 text-gray-900'
              >
                Rewrite Type
              </label>

              <div className='mt-2'>
                <select
                  id='rewriteType'
                  name='rewriteType'
                  className='block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:max-w-xs sm:text-sm sm:leading-6'
                >
                  <option value='none'>None</option>
                  <option value='incremental'>Incremental</option>
                </select>
              </div>
            </div>

            <div>
              {isLoading ? (
                <LoadingButton text='Generating' />
              ) : (
                <button
                  type='submit'
                  className='flex w-full justify-center rounded-md bg-indigo-600  px-4 py-2 text-sm font-semibold leading-6 text-white shadow-sm hover:bg-indigo-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600'
                >
                  Translate
                </button>
              )}
            </div>
          </form>
        </div>
      </div>
    </>
  );
}
