import React from 'react';
import Zoom from 'react-medium-image-zoom';
import 'react-medium-image-zoom/dist/styles.css';
const baseUrl = process.env.NEXT_PUBLIC_CORAL_SERVICE_API_URL;

export default function GraphCard(props) {
  const { imageIDs, imageFetchError } = props;

  return (
    <div className='bg-white p-6 border-2 rounded-3xl w-8/12 mx-auto my-10 overflow-auto sm:w-10/12'>
      <h2 className='text-xl font-bold mb-6 text-gray-800'>
        Intermediate Representation Graphs
      </h2>
      {imageFetchError ? (
        <p>{imageFetchError}</p>
      ) : (
        imageIDs && (
          <div className='grid grid-cols-2 gap-4'>
            <div className='border-2 rounded-md	p-2'>
              <h4 className='text-lg font-medium mb-6 text-gray-800'>
                Sql Node
              </h4>
              <Zoom>
                <img
                  src={`${baseUrl}/api/visualizations/${imageIDs.sqlNodeImageID}`}
                  alt='SQL Node Image'
                  className='on-hover-opaque'
                />
              </Zoom>
            </div>

            <div className='border-2 rounded-md	p-2'>
              <h4 className='text-lg font-medium mb-6 text-gray-800'>
                Rel Node
              </h4>
              <Zoom>
                <img
                  src={`${baseUrl}/api/visualizations/${imageIDs.relNodeImageID}`}
                  alt='Rel Node Image'
                  className='on-hover-opaque'
                />
              </Zoom>
            </div>

            {imageIDs.postRewriteSqlNodeImageID && (
              <div className='border-2 rounded-md p-2'>
                <h4 className='text-lg font-medium mb-6 text-gray-800'>
                  Post-Rewrite SQL Node
                </h4>
                <Zoom>
                  <img
                    src={`${baseUrl}/api/visualizations/${imageIDs.postRewriteSqlNodeImageID}`}
                    alt='Post Rewrite SQL Node Image'
                    className='on-hover-opaque'
                  />
                </Zoom>
              </div>
            )}

            {imageIDs.postRewriteRelNodeImageID && (
              <div className='border-2 rounded-md	p-2'>
                <h4 className='text-lg font-medium mb-6 text-gray-800'>
                  Post-Rewrite Rel Node
                </h4>
                <Zoom>
                  <img
                    src={`${baseUrl}/api/visualizations/${imageIDs.postRewriteRelNodeImageID}`}
                    alt='Post Rewrite Rel Node Image'
                    className='on-hover-opaque'
                  />
                </Zoom>
              </div>
            )}
          </div>
        )
      )}
    </div>
  );
}
