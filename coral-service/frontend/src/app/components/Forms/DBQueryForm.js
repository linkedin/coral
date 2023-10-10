'use client';

import { useState } from 'react';
import ResultModal from '@/app/components/ResultModal';

export default function DBQueryForm() {
  const [statement, setStatement] = useState('');
  const [creationResult, setCreationResult] = useState(null);
  const [modalIsOpen, setModalIsOpen] = useState(false);

  const handleCloseModal = () => {
    setModalIsOpen(false);
  };

  async function onSubmit(event) {
    event.preventDefault();

    await fetch(
      process.env.NEXT_PUBLIC_CORAL_SERVICE_API_URL +
        '/api/catalog-ops/execute',
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: statement,
      },
    )
      .then((response) => response.text())
      .then((data) => {
        setCreationResult(data);
        setModalIsOpen(true);
      });
  }

  return (
    <>
      <div className='flex flex-1 flex-col justify-center px-6 pt-2 pb-12 lg:px-8'>
        <div className='mt-10 sm:mx-auto sm:w-full sm:max-w-lg'>
          <form
            className='space-y-6'
            action='#'
            method='POST'
            onSubmit={onSubmit}
          >
            <div className='col-span-full'>
              <label className='block text-3xl font-medium leading-6 text-gray-900 mb-6'>
                Create database/table/view
              </label>
              <div className='mt-2'>
                <textarea
                  id='statement'
                  name='statement'
                  rows='3'
                  className='block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-coral-blue sm:text-lg sm:leading-6 font-courier'
                  placeholder='CREATE DATABASE/TABLE/VIEW'
                  value={statement}
                  onChange={(e) => setStatement(e.target.value)}
                  required
                ></textarea>
              </div>
              <p className='mt-3 text-md leading-6 text-gray-600'>
                Only happens in local mode.
              </p>
            </div>

            <div>
              <button
                type='submit'
                className='flex w-full justify-center rounded-md px-3 py-1.5 text-md font-semibold leading-6 text-white shadow-sm bg-coral-blue hover:bg-coral-blue-lighter focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-coral-blue'
              >
                Create
              </button>
            </div>
          </form>

          <p className='mt-10 text-center text-sm text-gray-500'>
            Done with creating?{' '}
            <a
              href='/translation'
              className='font-semibold leading-6 text-coral-blue hover:text-coral-blue-lighter'
            >
              Start translating!
            </a>
          </p>
        </div>
      </div>

      <ResultModal
        open={modalIsOpen}
        onClose={handleCloseModal}
        result={creationResult}
      />
    </>
  );
}
