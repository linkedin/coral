import React from 'react';

export default function GraphCard(props) {
  const { imageIDs, imageFetchError } = props;

  return (
    <div className='bg-white p-6 rounded-lg shadow-lg w-1/2 mx-auto my-10 overflow-auto '>
      <h2 className='text-2xl font-bold mb-6 text-gray-800'>
        Intermediate Representation Graphs
      </h2>
      {imageFetchError ? (
          <p>{imageFetchError}</p>
      ) : (
          imageIDs && (
              <div className="grid grid-cols-2 gap-4">
                <div className="border-2 rounded-md	p-2">
                  <h4 className='text-lg font-medium mb-6 text-gray-800'>Sql Node</h4>
                  <img
                      src={'http://localhost:8080/api/visualizations/' + imageIDs.sqlNodeImageID}
                      alt='SQL Node Image'
                  />
                </div>

                <div className="border-2 rounded-md	p-2">
                  <h4 className='text-lg font-medium mb-6 text-gray-800'>Rel Node</h4>
                  <img
                      src={'http://localhost:8080/api/visualizations/' + imageIDs.relNodeImageID}
                      alt='Rel Node Image'
                  />

                </div>

                {imageIDs.postRewriteSqlNodeImageID && (
                    <div className="border-2 rounded-md p-2">
                      <h4 className='text-lg font-medium mb-6 text-gray-800'>Post-Rewrite SQL Node</h4>
                      <img
                          src={
                              'http://localhost:8080/api/visualizations/' +
                              imageIDs.postRewriteSqlNodeImageID
                          }
                          alt='Post Rewrite SQL Node Image'
                      />

                    </div>

                )}

                {imageIDs.postRewriteRelNodeImageID && (
                    <div className="border-2 rounded-md	p-2">
                      <h4 className='text-lg font-medium mb-6 text-gray-800'>Post-Rewrite Rel Node</h4>
                      <img
                          src={
                              'http://localhost:8080/api/visualizations/' +
                              imageIDs.postRewriteRelNodeImageID
                          }
                          alt='Post Rewrite Rel Node Image'
                      />

                    </div>
                )}
              </div>
          )
      )}

    </div>
  );
}
