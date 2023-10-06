import React from 'react';

export default function ResultCard(props) {
  const { translation } = props;

  return (
    <div className='bg-white p-6 border-2 rounded-3xl w-8/12 mx-auto my-3 overflow-auto '>
      <h2 className='text-xl font-bold mb-2 text-gray-800'>Results</h2>
      <div className='font-courier'>
        {translation}
      </div>
    </div>
  );
}
