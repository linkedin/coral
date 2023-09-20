import NavBar from '@/app/components/NavBar';

// Home page also acts as database query page
export default function Home() {
  return (
    <>
      <NavBar />

      <div className='flex flex-1 flex-col justify-center px-6 py-12 lg:px-8'>
        <div className='mt-10 sm:mx-auto sm:w-full sm:max-w-sm'>
          <form className='space-y-6' action='#' method='POST'>
            <div className='col-span-full'>
              <label
                htmlFor='about'
                className='block text-xl font-medium leading-6 text-gray-900'
              >
                Create database/table/view
              </label>
              <div className='mt-2'>
                <textarea
                  id='about'
                  name='about'
                  rows='3'
                  className='block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6'
                  placeholder='CREATE DATABASE/TABLE/VIEW'
                ></textarea>
              </div>
              <p className='mt-3 text-sm leading-6 text-gray-600'>
                Only happens in local mode.
              </p>
            </div>

            <div>
              <button
                type='submit'
                className='flex w-full justify-center rounded-md bg-indigo-600 px-3 py-1.5 text-sm font-semibold leading-6 text-white shadow-sm hover:bg-indigo-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600'
              >
                Create
              </button>
            </div>
          </form>

          <p className='mt-10 text-center text-sm text-gray-500'>
            Done with creating?{' '}
            <a
              href='/translation'
              className='font-semibold leading-6 text-indigo-600 hover:text-indigo-500'
            >
              Start translating!
            </a>
          </p>
        </div>
      </div>
    </>
  );
}
