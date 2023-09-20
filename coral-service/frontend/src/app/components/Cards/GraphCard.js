import React from 'react';

export default function GraphCard(props) {
  const { imageIDs } = props;

  return (
    <div className='bg-white p-6 rounded-lg shadow-lg w-1/2 mt-10 mx-auto'>
      <h2 className='text-2xl font-bold mb-2 text-gray-800'>
        Intermediate Representation Graphs
      </h2>
      <img
        src={
          'http://localhost:8080/api/visualizations/' + imageIDs.sqlNodeImageID
        }
      />
      <img
        src={
          'http://localhost:8080/api/visualizations/' + imageIDs.relNodeImageID
        }
      />

      {imageIDs.postRewriteSqlNodeImageID && (
        <img
          src={
            'http://localhost:8080/api/visualizations/' +
            imageIDs.postRewriteSqlNodeImageID
          }
        />
      )}

      {imageIDs.postRewriteRelNodeImageID && (
        <img
          src={
            'http://localhost:8080/api/visualizations/' +
            imageIDs.postRewriteRelNodeImageID
          }
        />
      )}
    </div>
  );
}
