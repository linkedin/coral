import React from 'react';

export default function ResultCard(props) {
  const { translation } = props;

  {/*// TODO: Change translation endpoint to respond with original query and translated query in 2 different fields*/}
  const TextWithLineBreaks = ({ text }) => {

    if (!text.startsWith("Original query in")) {
      return <div className='font-courier'>{text}</div>;;
    }

    const parts = text.split(':');

    const formattedText = parts.map((part, index) => (
      <span key={index}>
        {
          part.split('Translated to').length === 2 ? (
              <>
                {part.split('Translated to')[0]}
                <br />
                Translated to
                {part.split('Translated to')[1]}
              </>
          ) : part
        }
        {index < parts.length - 1 && ':'}
        <br />
      </span>
    ));

    return <div className='font-courier'>{formattedText}</div>;
  };

  return (
    <div className='bg-white p-6 border-2 rounded-3xl w-8/12 mx-auto my-3 overflow-auto sm:w-10/12'>
      <h2 className='text-xl font-bold mb-2 text-gray-800'>Results</h2>
      <TextWithLineBreaks text={translation} />
    </div>
  );
}
