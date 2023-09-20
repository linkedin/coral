import React from 'react';

export default function ResultCard(props) {
  const { translation } = props;

  return (
    <div className='bg-white p-6 rounded-lg shadow-lg w-1/2 mt-10 mx-auto'>
      <h2 className='text-2xl font-bold mb-2 text-gray-800'>Results</h2>
      {translation}
    </div>
  );
}
