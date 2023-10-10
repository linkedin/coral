'use client';

import { useState } from 'react';
import LoadingButton from '@/app/components/Buttons/LoadingButton';
const baseUrl = process.env.NEXT_PUBLIC_CORAL_SERVICE_API_URL;
import { QuestionMarkCircleIcon } from '@heroicons/react/24/outline';

export default function TranslationForm({
  onTranslationFetchComplete,
  onImageIDsFetchComplete,
}) {
  const [isLoading, setIsLoading] = useState(false);

  async function onSubmit(event) {
    event.preventDefault();
    setIsLoading(true);

    // clear old results
    onTranslationFetchComplete(null, null);
    onImageIDsFetchComplete(null, null);

    const formData = new FormData(event.currentTarget);

    await fetch(baseUrl + '/api/translations/translate', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Accept: 'application/json',
      },
      body: JSON.stringify(Object.fromEntries(formData)),
    })
      .then((response) => {
        if (!response.ok) {
          return response.text().then((errorMessage) => {
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

    await fetch(baseUrl + '/api/visualizations/generategraphs', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Accept: 'application/json',
      },
      body: JSON.stringify(Object.fromEntries(formData)),
    })
      .then((response) => {
        if (!response.ok) {
          return response.text().then((errorMessage) => {
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
      <div className='flex flex-1 flex-col justify-center px-6 pt-2 pb-12 lg:px-8'>
        <div className='mt-10 sm:mx-auto sm:w-full sm:max-w-lg'>
          <form
            className='space-y-6'
            action='@/app/components/Forms/TranslationForm#'
            method='POST'
            onSubmit={onSubmit}
          >
            <div className='col-span-full'>
              <label
                htmlFor='query'
                className='block text-3xl font-medium leading-6 text-gray-900 mb-6'
              >
                Translate SQL
              </label>
              <div className='mt-2'>
                <textarea
                  id='query'
                  name='query'
                  rows='3'
                  className='block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-coral-blue sm:text-md sm:leading-6 font-courier'
                  placeholder='SELECT * FROM db.tbl'
                  required
                ></textarea>
              </div>
            </div>
            <div className='flex justify-between'>
              <div>From</div>
              <select
                id='sourceLanguage'
                name='sourceLanguage'
                className='mx-6 block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 focus:ring-2 focus:ring-inset focus:ring-coral-blue sm:max-w-xs sm:text-md sm:leading-6'
              >
                <option value='hive'>Hive</option>
                <option value='trino'>Trino</option>
                <option value='spark'>Spark</option>
              </select>
              <div> to </div>
              <select
                id='targetLanguage'
                name='targetLanguage'
                className='ml-6 block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 focus:ring-2 focus:ring-inset focus:ring-coral-blue sm:max-w-xs sm:text-md sm:leading-6'
              >
                <option value='trino'>Trino</option>
                <option value='spark'>Spark</option>
              </select>
            </div>

            <div className='col-span-full'>
              <div className='flex justify-start ...'>
                <label
                  htmlFor='rewriteType'
                  className='block text-md font-medium leading-6 text-gray-900 mr-1'
                >
                  Rewrite Type
                </label>

                {/* Floating Tooltip */}
                <div className='relative flex flex-col items-center group'>
                  <QuestionMarkCircleIcon className='h-6 w-6' />
                  <div className='absolute bottom-0 flex flex-col items-center hidden mb-6 group-hover:flex'>
                    <span className='relative z-10 p-2 text-xs leading-none text-white whitespace-no-wrap bg-black shadow-lg w-48'>
                      A rewrite type to apply on the input query.
                    </span>
                    <div className='w-4 h-4 -mt-3 rotate-45 bg-black'></div>
                  </div>
                </div>
              </div>

              <div className='mt-2'>
                <select
                  id='rewriteType'
                  name='rewriteType'
                  className='block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 focus:ring-2 focus:ring-inset focus:ring-coral-blue sm:w-1/2 sm:max-w-xs sm:text-md sm:leading-6'
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
                  className='flex w-full justify-center rounded-md px-4 py-2 text-md font-semibold leading-6 text-white shadow-sm bg-coral-blue hover:bg-coral-blue-lighter focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-coral-blue'
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
