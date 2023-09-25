import React from 'react';

export default function GraphCard(props) {
  const { imageIDs, imageFetchError } = props;

  return (
    <div className='bg-white p-6 rounded-lg shadow-lg w-1/2 mx-auto my-10 overflow-auto '>
      <h2 className='text-2xl font-bold mb-2 text-gray-800'>
        Intermediate Representation Graphs
      </h2>
      {imageFetchError ? (
          <p>{imageFetchError}</p>
      ) : (
          imageIDs && (
              <div>
                <img
                    src={'http://localhost:8080/api/visualizations/' + imageIDs.sqlNodeImageID}
                    alt='SQL Node Image'
                />
                <img
                    src={'http://localhost:8080/api/visualizations/' + imageIDs.relNodeImageID}
                    alt='Rel Node Image'
                />

                {imageIDs.postRewriteSqlNodeImageID && (
                    <img
                        src={
                            'http://localhost:8080/api/visualizations/' +
                            imageIDs.postRewriteSqlNodeImageID
                        }
                        alt='Post Rewrite SQL Node Image'
                    />
                )}

                {imageIDs.postRewriteRelNodeImageID && (
                    <img
                        src={
                            'http://localhost:8080/api/visualizations/' +
                            imageIDs.postRewriteRelNodeImageID
                        }
                        alt='Post Rewrite Rel Node Image'
                    />
                )}
              </div>
          )
      )}

    </div>
  );
}
