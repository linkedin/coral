import NavBar from '@/app/components/NavBar';
import DBQueryForm from '@/app/components/Forms/DBQueryForm';
import Image from 'next/image';

// Home page also acts as database query page
export default function Home() {
  return (
    <>
      <NavBar />
      <Image
        width='200'
        height='200'
        className='mx-auto pt-4'
        src='/coral-logo.jpg'
        alt='Coral Logo'
      />
      <DBQueryForm />
    </>
  );
}
